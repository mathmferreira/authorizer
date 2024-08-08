package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.domain.model.BenefitBalance;
import br.com.caju.authorizer.domain.model.CashBalance;
import br.com.caju.authorizer.domain.model.Merchant;
import br.com.caju.authorizer.enums.BalanceType;
import br.com.caju.authorizer.exception.InsufficientBalanceException;
import br.com.caju.authorizer.exception.InvalidAmountException;
import br.com.caju.authorizer.repository.BenefitBalanceRepository;
import br.com.caju.authorizer.repository.CashBalanceRepository;
import br.com.caju.authorizer.util.BigDecimalUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class BenefitBalanceService {

    protected static final List<Integer> FOOD_CODES = Arrays.asList(5411, 5412);
    protected static final List<Integer> MEAL_CODES = Arrays.asList(5811, 5812);
    public static final Map<BalanceType, List<Integer>> MCC_MAP = Map.of(BalanceType.FOOD, FOOD_CODES, BalanceType.MEAL, MEAL_CODES);

    private final BenefitBalanceRepository repository;
    private final CashBalanceRepository cashBalanceRepository;
    private final MerchantService merchantService;

    public BenefitBalance findByAccountAndBalanceType(Account account, String merchant) {
        return repository.findByAccountAndBalanceType(account, findBalanceTypeByMerchant(merchant)).orElseThrow(EntityNotFoundException::new);
    }

    public List<BenefitBalance> findByAccount(String accountId) {
        Assert.isTrue(StringUtils.isNotBlank(accountId), "accountId cannot be blank");
        return repository.findByAccount(accountId);
    }

    @Transactional
    public void debitBalance(BenefitBalance balance, BigDecimal amount) {
        balance = checkBalanceAndAmount(balance, amount);
        var newAmount = balance.getAmount().subtract(amount);
        balance.setAmount(newAmount);
        repository.save(balance);
    }

    //*****************************************************************************************************************
    //******************************************* PRIVATE/PROTECTED METHODS *******************************************
    //*****************************************************************************************************************

    private BalanceType findBalanceTypeByMerchant(String merchant) {
        Assert.isTrue(StringUtils.isNotBlank(merchant), "merchant cannot be blank");
        var name = merchant.substring(0, merchant.indexOf("  "));
        var merchantEntity = merchantService.findByName(name.toUpperCase());
        return merchantEntity.map(Merchant::getBalanceType).orElse(BalanceType.CASH);
    }

    private BenefitBalance checkBalanceAndAmount(BenefitBalance balance, BigDecimal amount) {
        Assert.notNull(balance, "balance cannot be null");
        if (BigDecimalUtils.isLessThan(amount, BigDecimal.ZERO)) {
            throw new InvalidAmountException();
        }
        if (BigDecimalUtils.isLessThan(balance.getAmount(), amount)) {
            balance = fallbackCashBalance(balance, amount);
        }
        return balance;
    }

    private BenefitBalance fallbackCashBalance(BenefitBalance balance, BigDecimal amount) {
        checkVerifiedBalance(balance);
        return checkCashBalance(balance.getAccount(), amount);
    }

    private void checkVerifiedBalance(BenefitBalance balance) {
        if (balance instanceof CashBalance) {
            throw new InsufficientBalanceException();
        }
    }

    private CashBalance checkCashBalance(Account account, BigDecimal amount) {
        var cashBalance = cashBalanceRepository.findByAccount(account).orElseThrow(EntityNotFoundException::new);
        if (BigDecimalUtils.isLessThan(cashBalance.getAmount(), amount)) {
            throw new InsufficientBalanceException();
        }
        return cashBalance;
    }

}

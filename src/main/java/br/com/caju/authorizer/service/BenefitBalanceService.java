package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.domain.model.BenefitBalance;
import br.com.caju.authorizer.exception.InsufficientBalanceException;
import br.com.caju.authorizer.exception.InvalidAmountException;
import br.com.caju.authorizer.exception.InvalidMccException;
import br.com.caju.authorizer.repository.BenefitBalanceRepository;
import br.com.caju.authorizer.util.BigDecimalUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class BenefitBalanceService {

    private static final List<Integer> FOOD_CODES = Arrays.asList(5411, 5412);
    private static final List<Integer> MEAL_CODES = Arrays.asList(5811, 5812);
    private static final Map<String, List<Integer>> MCC_MAP = Map.of("FOOD", FOOD_CODES, "MEAL", MEAL_CODES);

    private final BenefitBalanceRepository repository;

    public BenefitBalance findByAccountAndMcc(Account account, Integer mcc) {
        Assert.notNull(mcc, "mcc cannot be null");
        return repository.findByAccountAndBalanceType(account, findBalanceTypeByMcc(mcc)).orElseThrow(EntityNotFoundException::new);
    }

    public void debitBalance(BenefitBalance balance, BigDecimal amount) {
        checkAmount(balance);
        if (BigDecimalUtils.isLessThan(balance.getAmount(), amount)) {
            throw new InsufficientBalanceException();
        }
        var newAmount = balance.getAmount().subtract(amount);
        balance.setAmount(newAmount);
        repository.save(balance);
    }

    private String findBalanceTypeByMcc(Integer mcc) {
        return MCC_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().contains(mcc))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(InvalidMccException::new);
    }

    private void checkAmount(BenefitBalance balance) {
        Assert.notNull(balance, "balance cannot be null");
        if (BigDecimalUtils.isLessThan(balance.getAmount(), BigDecimal.ZERO)) {
            throw new InvalidAmountException();
        }
    }

}

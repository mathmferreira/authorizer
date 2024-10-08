package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.Transaction;
import br.com.caju.authorizer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountService accountService;
    private final BenefitBalanceService balanceService;

    @Transactional
    public void authorizeTransaction(Transaction transaction, String accountId) {
        Assert.notNull(transaction, "transaction cannot be null");
        Assert.isTrue(StringUtils.isNotBlank(accountId), "accountId cannot be blank");
        var account = accountService.getReferenceById(Long.valueOf(accountId));
        var benefitBalance = balanceService.findByAccountAndBalanceType(account, transaction.getMerchant());
        balanceService.debitBalance(benefitBalance, transaction.getTotalAmount());
        if (!BenefitBalanceService.MCC_MAP.get(benefitBalance.getBalanceType()).contains(transaction.getMcc())) {
            transaction.setMcc(BenefitBalanceService.MCC_MAP.get(benefitBalance.getBalanceType()).stream().findFirst().orElseThrow());
        }
        transaction.setAccount(account);
        transaction.setId(null);
        repository.save(transaction);
    }

}

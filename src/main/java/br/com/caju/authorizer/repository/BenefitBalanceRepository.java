package br.com.caju.authorizer.repository;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.domain.model.BenefitBalance;
import br.com.caju.authorizer.enums.BalanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BenefitBalanceRepository extends JpaRepository<BenefitBalance, Long> {

    Optional<BenefitBalance> findByAccountAndBalanceType(Account account, BalanceType balanceType);

    @Query("Select bb From BenefitBalance bb " +
            "Inner Join bb.account acc " +
            "Where acc.id = :accountId")
    List<BenefitBalance> findByAccount(String accountId);

}

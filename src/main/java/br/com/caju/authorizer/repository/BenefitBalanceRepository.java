package br.com.caju.authorizer.repository;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.domain.model.BenefitBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BenefitBalanceRepository extends JpaRepository<BenefitBalance, Long> {

    Optional<BenefitBalance> findByAccountAndBalanceType(Account account, String balanceType);

}

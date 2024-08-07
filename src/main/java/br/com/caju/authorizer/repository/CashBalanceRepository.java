package br.com.caju.authorizer.repository;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.domain.model.CashBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashBalanceRepository extends JpaRepository<CashBalance, Long> {

    Optional<CashBalance> findByAccount(Account account);

}

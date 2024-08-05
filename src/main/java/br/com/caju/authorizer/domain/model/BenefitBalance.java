package br.com.caju.authorizer.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "benefit_balance_tb")
@DiscriminatorColumn(name = "balance_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BenefitBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_balance_id")
    private Long id;

    @Column(name = "balance_type", insertable = false, updatable = false)
    private String balanceType;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

}

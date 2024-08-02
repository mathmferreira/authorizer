package br.com.caju.authorizer.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@Table(name = "benefit_balance_tb")
@DiscriminatorColumn(name = "balance_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BenefitBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "benefit_balance_sequence")
    @SequenceGenerator(name = "benefit_balance_sequence", sequenceName = "benefit_balance_id_sequence", allocationSize = 1)
    @Column(name = "benefit_balance_id")
    private Long id;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

}

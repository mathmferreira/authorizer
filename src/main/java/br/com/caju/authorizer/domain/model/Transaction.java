package br.com.caju.authorizer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction_tb")
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaction {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_sequence")
    @SequenceGenerator(name = "transaction_sequence", sequenceName = "transaction_id_sequence", allocationSize = 1)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private Integer mcc;

    @NotBlank
    private String merchant;

}

package br.com.caju.authorizer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account_tb")
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence")
    @SequenceGenerator(name = "account_sequence", sequenceName = "account_id_sequence", allocationSize = 1)
    @Column(name = "account_id")
    private Long id;

    @NotBlank
    private String owner;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<BenefitBalance> balances = new ArrayList<>();

}

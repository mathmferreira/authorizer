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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @NotBlank
    private String owner;

    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<BenefitBalance> balances = new ArrayList<>();

}

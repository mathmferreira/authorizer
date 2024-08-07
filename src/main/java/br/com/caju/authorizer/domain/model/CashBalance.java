package br.com.caju.authorizer.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("CASH")
@EqualsAndHashCode(callSuper = false)
public class CashBalance extends BenefitBalance {
}

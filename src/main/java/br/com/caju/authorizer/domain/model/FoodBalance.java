package br.com.caju.authorizer.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("FOOD")
@EqualsAndHashCode(callSuper = false)
public class FoodBalance extends BenefitBalance {
}

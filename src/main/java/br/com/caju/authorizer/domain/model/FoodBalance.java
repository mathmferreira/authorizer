package br.com.caju.authorizer.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FOOD")
public class FoodBalance extends BenefitBalance {
}

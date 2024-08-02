package br.com.caju.authorizer.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MEAL")
public class MealBalance extends BenefitBalance {
}

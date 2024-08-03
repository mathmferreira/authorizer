package br.com.caju.authorizer.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("MEAL")
@EqualsAndHashCode(callSuper = false)
public class MealBalance extends BenefitBalance {
}

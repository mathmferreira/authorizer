package br.com.caju.authorizer.domain.vo;

import java.math.BigDecimal;

public record BenefitBalanceVO(String balanceType, BigDecimal totalAmount) {
}

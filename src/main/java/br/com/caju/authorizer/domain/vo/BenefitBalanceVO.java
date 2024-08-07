package br.com.caju.authorizer.domain.vo;

import br.com.caju.authorizer.enums.BalanceType;

import java.math.BigDecimal;

public record BenefitBalanceVO(BalanceType balanceType, BigDecimal totalAmount) {
}

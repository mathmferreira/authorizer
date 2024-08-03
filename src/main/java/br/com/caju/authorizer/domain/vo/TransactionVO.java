package br.com.caju.authorizer.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionVO {

    @NotBlank
    private String account;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private Integer mcc;

    @NotBlank
    private String merchant;

}

package br.com.caju.authorizer.controller;

import br.com.caju.authorizer.domain.vo.BenefitBalanceVO;
import br.com.caju.authorizer.service.BenefitBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/benefits/balance")
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class BenefitBalanceController {

    private final BenefitBalanceService service;

    @GetMapping("/{accountId}")
    public List<BenefitBalanceVO> findByAccount(@PathVariable String accountId) {
        var balances = service.findByAccount(accountId);
        return balances.stream().map(it -> new BenefitBalanceVO(it.getBalanceType(), it.getAmount())).toList();
    }

}

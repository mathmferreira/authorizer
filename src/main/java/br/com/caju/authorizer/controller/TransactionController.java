package br.com.caju.authorizer.controller;

import br.com.caju.authorizer.domain.model.Transaction;
import br.com.caju.authorizer.domain.vo.TransactionVO;
import br.com.caju.authorizer.records.TransactionResponseVO;
import br.com.caju.authorizer.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transaction")
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class TransactionController {

    private final TransactionService service;

    @PostMapping("/authorize")
    public TransactionResponseVO authorizeTransaction(@Valid @RequestBody TransactionVO request) {
        var transaction = Transaction.builder()
                .totalAmount(request.getTotalAmount())
                .merchant(request.getMerchant())
                .mcc(request.getMcc())
                .build();
        service.authorizeTransaction(transaction, request.getAccount());
        return new TransactionResponseVO("00");
    }

}

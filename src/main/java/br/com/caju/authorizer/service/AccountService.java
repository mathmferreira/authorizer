package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class AccountService {

    private final AccountRepository repository;

    public Account getReferenceById(Long id) {
        Assert.notNull(id, "id cannot be null");
        return repository.getReferenceById(id);
    }

}

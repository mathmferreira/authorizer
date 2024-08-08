package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.Merchant;
import br.com.caju.authorizer.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class MerchantService {

    private final MerchantRepository repository;

    public Optional<Merchant> findByName(String name) {
        Assert.isTrue(StringUtils.isNotBlank(name), "name cannot be blank");
        return repository.findByName(name);
    }

}

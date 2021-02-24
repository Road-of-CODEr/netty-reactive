package com.study.huisam.mongodb.service;

import com.study.huisam.mongodb.domain.Account;
import com.study.huisam.mongodb.dto.AccountSaveRequestDTO;
import com.study.huisam.mongodb.repository.AccountCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountCrudRepository accountCrudRepository;

    public Mono<String> asyncSave(AccountSaveRequestDTO requestDTO) {
        final Account account = new Account(UUID.randomUUID().toString(), requestDTO.getOwner(), requestDTO.getValue());
        return accountCrudRepository.save(account)
                .map(Account::getId);
    }
}

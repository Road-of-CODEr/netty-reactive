package com.study.huisam.mongodb;

import com.study.huisam.mongodb.domain.Account;
import com.study.huisam.mongodb.repository.AccountCrudRepository;
import com.study.huisam.mongodb.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountCrudRepository accountRepository;
    private final AccountService accountService;

    @PostConstruct
    public void init() {
        final Account account1 = new Account("id1", "huisam", 1.1);
        final Account account2 = new Account("id2", "minhyungPark", 2.1);

        accountRepository.save(account1);
        accountRepository.save(account2);
    }

    @PreDestroy
    public void destroy() {
        accountRepository.deleteAll();
    }

    @GetMapping("/account/{id}")
    public Mono<Account> get(@PathVariable("id") String id) {
        return accountRepository.findById(id);
    }

}

package com.study.minhyungpark.mongodb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.minhyungpark.mongodb.domain.Account;
import com.study.minhyungpark.mongodb.repository.AccountCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AccountController {

    private final AccountCrudRepository repository;

    public AccountController(AccountCrudRepository repository) {
        this.repository = repository;
        init();
    }

    void init() {
        repository.save(new Account(null, "test1", 1.0)).subscribe();
        repository.save(new Account(null, "test2", 2.0)).subscribe();
        repository.save(new Account(null, "test3", 3.0)).subscribe();
    }

    @GetMapping("/reactive/accounts")
    public Flux<Account> findAll() {
        return repository.findAll();
    }

    @GetMapping("/reactive/accounts/{id}")
    public Mono<Account> findById(@PathVariable String id) {
        return repository.findById(id);
    }

    @PostMapping("/reactive/accounts")
    public Mono<Account> save(@RequestBody Account account) {
        return repository.save(account);
    }

    @PutMapping("/reactive/accounts/{id}")
    public Mono<Account> update(@PathVariable String id, @RequestBody Account account) {
        account.setId(id);
        return repository.save(account);
    }
}

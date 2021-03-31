package com.study.minhyungpark.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.minhyungpark.mongodb.domain.Account;
import com.study.minhyungpark.mongodb.domain.request.AccountCreateRequest;
import com.study.minhyungpark.mongodb.repository.AccountCrudRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class KafkaAccountController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountCrudRepository repository;

    public KafkaAccountController(
            KafkaTemplate<String, Object> kafkaTemplate,
            AccountCrudRepository repository) {
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
    }

    @PostMapping("/kafka/account")
    public Mono<String> produce(@RequestBody AccountCreateRequest dto) {
        final var account = new Account(null, dto.getOwner(), dto.getValue());
        return repository.save(account)
                .map(Account::getId)
                .doOnNext(id -> kafkaTemplate.send("account", id));
    }

}

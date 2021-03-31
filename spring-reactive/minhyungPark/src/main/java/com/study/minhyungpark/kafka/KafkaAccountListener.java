package com.study.minhyungpark.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.study.minhyungpark.mongodb.repository.AccountCrudRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaAccountListener {
    private final AccountCrudRepository repository;

    public KafkaAccountListener(AccountCrudRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "account", groupId = "group-id")
    public void consume(String id) {
        repository.findById(id)
                  .doOnNext(account -> log.info("account - {}", account))
                  .subscribe();
    }
}

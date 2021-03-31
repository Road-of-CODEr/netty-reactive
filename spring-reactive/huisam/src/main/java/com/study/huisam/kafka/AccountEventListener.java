package com.study.huisam.kafka;

import com.study.huisam.mongodb.repository.AccountCrudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventListener {

    private final AccountCrudRepository repository;

    @KafkaListener(topics = "account", groupId = "group-id")
    public void accountEvent(String id) {
        repository.findById(id)
                .log()
                .subscribe();
    }
}

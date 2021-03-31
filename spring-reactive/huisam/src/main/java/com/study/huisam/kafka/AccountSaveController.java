package com.study.huisam.kafka;

import com.study.huisam.mongodb.domain.Account;
import com.study.huisam.mongodb.dto.AccountSaveRequestDTO;
import com.study.huisam.mongodb.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AccountSaveController {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountService accountService;


    @GetMapping("/account")
    public Flux<Account> findAll() {
        return accountService.findAll();
    }

    @PostMapping("/account")
    public Mono<String> asyncSave(@RequestBody AccountSaveRequestDTO requestDTO) {
        return accountService.asyncSave(requestDTO)
                .log()
                .doOnNext(id -> kafkaTemplate.send("account", id));
    }
}

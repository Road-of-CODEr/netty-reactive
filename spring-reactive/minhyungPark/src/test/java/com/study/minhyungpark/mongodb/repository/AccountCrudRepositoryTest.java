package com.study.minhyungpark.mongodb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.study.minhyungpark.mongodb.domain.Account;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
class AccountCrudRepositoryTest {

    @Autowired
    private AccountCrudRepository repository;

    @Test
    void save() {
        // given
        final Account minhyung = Account.builder()
                                        .owner("minhyung")
                                        .value(10.0)
                                        .build();
        // when
        final Mono<Account> newAccount = repository.save(minhyung);

        // then
        StepVerifier.create(newAccount)
                    .assertNext(account -> {
                        assertEquals("minhyung", account.getOwner());
                        assertEquals(10.0, account.getValue());
                        assertNotNull(account.getId());
                    })
                    .expectComplete()
                    .verify();
    }

    @Test
    void findById() {
        // given
        final Account testAccount = Account.builder()
                                           .owner("owner")
                                           .value(10.0)
                                           .build();

        final Account savedAccount = repository.save(testAccount).block();

        // when
        final Mono<Account> foundedAccount = repository.findById(savedAccount.getId());

        // then
        StepVerifier.create(foundedAccount)
                    .assertNext(account -> {
                        assertEquals("owner", account.getOwner());
                        assertEquals(10.0, account.getValue());
                    })
                    .expectComplete()
                    .verify();
    }




}
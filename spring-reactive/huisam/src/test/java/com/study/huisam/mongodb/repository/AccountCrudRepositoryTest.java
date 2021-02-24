package com.study.huisam.mongodb.repository;

import com.study.huisam.mongodb.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AccountCrudRepositoryTest {

    @Autowired
    private AccountCrudRepository repository;

    @Test
    @DisplayName("test")
    void test() {
        // given
        final Account account = new Account("id", "owner", 1.1);

        // when
        repository.save(account).block();
        final Mono<Account> result = repository.findById("id");

        // then
        StepVerifier
                .create(result)
                .assertNext(a -> {
                    assertThat(a).isEqualTo(account);
                })
                .expectComplete()
                .verify();
    }
}
package com.study.minhyungpark.mongodb.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.study.minhyungpark.mongodb.domain.Account;

@Repository
public interface AccountCrudRepository extends ReactiveCrudRepository<Account, String> {
}

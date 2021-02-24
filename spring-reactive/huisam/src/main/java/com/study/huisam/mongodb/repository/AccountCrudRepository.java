package com.study.huisam.mongodb.repository;

import com.study.huisam.mongodb.domain.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountCrudRepository extends ReactiveCrudRepository<Account, String> {

}

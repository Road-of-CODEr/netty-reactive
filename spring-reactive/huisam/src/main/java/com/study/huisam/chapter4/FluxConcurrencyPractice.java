package com.study.huisam.chapter4;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class FluxConcurrencyPractice {

    public static Flux<String> requestsBook(String user) {
        return Flux.range(1, 5)
                .map(num -> "book-" + num)
                .delayElements(Duration.ofMillis(3))
                ;
    }


    public static void main(String[] args) throws InterruptedException {
        Flux.just("user-1", "user-2", "user-3")
                .flatMap(user -> requestsBook(user)
                        .map(book -> user + "/" + book)
                )
                .subscribe(data -> log.info("data : {}", data));

        Thread.sleep(1000L);
    }
}

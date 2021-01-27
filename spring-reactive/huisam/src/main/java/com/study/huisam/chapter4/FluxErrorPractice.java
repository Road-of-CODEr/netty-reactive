package com.study.huisam.chapter4;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class FluxErrorPractice {

    public static Flux<String> recommendedBooks(String userId) {
        return Flux.defer(() -> {
            if (ThreadLocalRandom.current().nextInt(10) < 10) {
                return Flux.<String>error(new RuntimeException("err"))
                        .delaySequence(Duration.ofMillis(50L)); // 실패시 50ms 지연
            } else {
                return Flux.just("Hi", "Success")
                        .delayElements(Duration.ofMillis(5L)); // 성공시 5ms 내로 응답
            }
        }).doOnSubscribe(e -> log.info("Request for {} ", userId));
    }

    public static void main(String[] args) throws InterruptedException {
        Flux.just("user-1")
                .flatMap(user -> recommendedBooks(user)
                        .retryWhen(RetrySpec.backoff(5, Duration.ofMillis(100))) // 실패시 최대 100ms 간격으로 5번 재시도
                        .timeout(Duration.ofSeconds(4)) // timeout은 4초로 지정
                        .onErrorResume(e -> Flux.just("The Consuming"))
                )
                .subscribe(
                        b -> log.info("onNext : {}", b),
                        e -> log.warn("onError : {}", e),
                        () -> log.info("onComplete")
                );
        Thread.sleep(3_500L);
    }
}

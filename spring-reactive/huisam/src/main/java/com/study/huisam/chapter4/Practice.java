package com.study.huisam.chapter4;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Comparator;
import java.util.stream.IntStream;

@Slf4j
public class Practice {

    public static void main(String[] args) throws InterruptedException {
        final Disposable disposable = Flux.interval(Duration.ofMillis(50))
                .timestamp()
                .subscribe(
                        data -> log.info("data : {}", data)
                ); // 구독 시작
        Thread.sleep(1000);
        disposable.dispose(); // 구독을 취소

        // 시퀀스 수집하기
        Flux.just(1, 2, 5, 7, 4, -1)
                .collectSortedList(Comparator.reverseOrder())
                .subscribe(System.out::println);

        // 필터링 하기
        Flux.just(3, 5, 7, 9, 11, 15, 16)
                .filter(e -> e % 2 == 0)
                .subscribe(event -> log.info("event : {}", event));

        // 합치기
        Flux.just(3, 5, 7)
                .reduce(0, Integer::sum)
                .subscribe(data -> log.info("reduce : {}", data));

        // 즉시 새로운 스트림 가동
        Flux.just(1, 2, 3)
                .thenMany(Flux.just(4, 5))
                .subscribe(System.out::println);

        // 그룹핑
        Flux.range(1, 12)
                .buffer(4)
                .subscribe(data -> log.info("grouping : {}", data));

        // window 그룹핑
        Flux.range(100, 20)
                .windowUntil(integer -> integer % 3 == 0)
                .subscribe(window -> window.collectList().subscribe(
                        data -> log.info("window : {}", data)
                ));

        // sampling for each seconds
        Flux.range(1, 100)
                .delayElements(Duration.ofMillis(1))
                .sample(Duration.ofMillis(50))
                .subscribe(e -> log.info("sample : {}", e));


        // 싱글 쓰레드 기반의 데이터 제공하는 생성자
        Flux.push(emitter -> IntStream.range(1, 10)
                .forEach(emitter::next))
                .delayElements(Duration.ofMillis(1))
                .subscribe(e -> log.info("push : {}", e));

        Flux.create(emitter -> emitter.onDispose(() -> log.info("Disposed")))
                .subscribe(e -> log.info("subscribed"));

        Thread.sleep(1000);
    }
}

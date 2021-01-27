package com.study.huisam.chapter4;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FluxPublishPractice {
    public static void main(String[] args) throws InterruptedException {
        Flux.range(1, 100)
                .map(String::valueOf)
                .filter(s -> s.length() > 1) // 여기까지 main 쓰레드
                .log()
                .publishOn(Schedulers.parallel()) // 여기서부터 스케줄러 쓰레드 ( queueing )
                .log()
                .subscribe();

        Thread.sleep(1000);
    }
}

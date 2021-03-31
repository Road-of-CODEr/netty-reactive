package com.study.huisam.kafka.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class StreamConfiguration {

    @Bean(name = "tunnel")
    public Consumer<Flux<String>> tunnel() {
        return stringFlux -> stringFlux.log()
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Bean(name = "joyboy")
    public Supplier<Flux<String>> joyboy() {
        return () -> Flux.just("test").subscribeOn(Schedulers.boundedElastic());
    }
}

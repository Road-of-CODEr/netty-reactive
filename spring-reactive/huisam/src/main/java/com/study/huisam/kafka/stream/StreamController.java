package com.study.huisam.kafka.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StreamController {
    private final StreamProducer streamProducer;

    @GetMapping("/stream")
    public Mono<Void> stream() {
        streamProducer.sendMessage("message");
        return Mono.empty();
    }
}

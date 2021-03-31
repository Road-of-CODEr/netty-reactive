package com.study.huisam.kafka.stream;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Component
public class StreamProducer {

    public static final String TOPIC = "student";

    private final StreamBridge streamBridge;

    public void sendMessage(String message) {
        final int number = ThreadLocalRandom.current().nextInt(10);
        streamBridge.send(TOPIC, message + number);
    }
}

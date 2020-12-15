package com.study.minhyungpark.chapter2.rxjava;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class RxTemperatureController {
    private final RxTemperatureSensor temperatureSensor;

    public RxTemperatureController(RxTemperatureSensor temperatureSensor) {
        this.temperatureSensor = temperatureSensor;
    }

    @GetMapping("/temperature-rx-stream")
    public SseEmitter events() {
        final RxSseEmitter emitter = new RxSseEmitter();

        temperatureSensor.temperatureStream()
                         .subscribe(emitter.getSubscriber());

        return emitter;
    }
}

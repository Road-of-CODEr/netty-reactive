package com.study.huisam.chapter2.rxjava;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class RxTemperatureController {
    private final RxTemperatureSensor temperatureSensor;

    @GetMapping("/rx-temperature-stream")
    public SseEmitter events(HttpServletRequest request) {
        final RxSeeEmitter emitter = new RxSeeEmitter();
        temperatureSensor.temperatureStream()
                .subscribe(emitter.getSubscriber());
        return emitter;
    }
}

package com.study.huisam.chapter2.temperature;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@RestController
public class TemperatureController {
    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    @GetMapping("/temperature-stream")
    public SseEmitter events() {
        final SseEmitter sseEmitter = new SseEmitter();
        clients.add(sseEmitter);
        log.info("{}", sseEmitter);

        sseEmitter.onTimeout(() -> clients.remove(sseEmitter));
        sseEmitter.onCompletion(() -> clients.remove(sseEmitter));
        return sseEmitter;
    }

    @Async
    @EventListener
    public void handleMessage(Temperature temperature) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        clients.forEach(emitter -> {
            try {
                emitter.send(temperature, MediaType.APPLICATION_JSON);
            } catch (Exception ignore) {
                log.error("error");
                deadEmitters.add(emitter);
            }
        });
        clients.removeAll(deadEmitters);
    }
}

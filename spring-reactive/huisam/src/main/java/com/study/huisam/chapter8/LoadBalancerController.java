package com.study.huisam.chapter8;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class LoadBalancerController {
    private final LoadBalancerService loadBalancerService;

    @GetMapping("/loadBalance")
    public Mono<String> ribbon() {
        return loadBalancerService.getFlaskHello();
    }
}

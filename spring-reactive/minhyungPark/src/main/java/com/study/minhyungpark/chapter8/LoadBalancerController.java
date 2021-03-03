package com.study.minhyungpark.chapter8;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class LoadBalancerController {
    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public LoadBalancerController(
            WebClient.Builder loadBalancedWebClientBuilder,
            ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
        this.lbFunction = lbFunction;
    }

    @GetMapping("/hello-flask")
    public Mono<String> hi() {
        return loadBalancedWebClientBuilder
                .filter(lbFunction)
                .build()
                .get()
                .uri("http://hello-flask/")
                .retrieve()
                .bodyToMono(String.class);
    }
}

package com.study.huisam.chapter8;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LoadBalancerService {

    private final WebClient webClient;

    public LoadBalancerService(WebClient.Builder webClient, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.webClient = webClient.filter(lbFunction).build();
    }

    public Mono<String> getFlaskHello() {
        return webClient
                .get()
                .uri("http://loadBalancer")
                .retrieve()
                .bodyToMono(String.class);
    }
}

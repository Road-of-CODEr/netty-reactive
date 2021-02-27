package com.study.huisam.chapter8.configuration;

import com.study.huisam.chapter8.AbstractServiceInstanceListSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EnableConfigurationProperties(LoadBalanceProperties.class)
@Configuration
public class LoadBalancerServerConfiguration {
    private final LoadBalanceProperties properties;

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new AbstractServiceInstanceListSupplier("loadBalancer") {
            @Override
            public Flux<List<ServiceInstance>> get() {
                return Flux.just(
                        properties.getServers().stream()
                                .map(server -> createDefaultServiceInstance(server.getHost(), server.getPort()))
                                .collect(Collectors.toUnmodifiableList())
                );
            }
        };
    }
}

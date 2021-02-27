package com.study.huisam.chapter8.configuration;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;


@Configuration
@LoadBalancerClient(name = "loadBalancer", configuration = LoadBalancerServerConfiguration.class)
public class LoadBalancerConfiguration {
}

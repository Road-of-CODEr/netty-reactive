package com.study.huisam.chapter8;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public abstract class AbstractServiceInstanceListSupplier implements ServiceInstanceListSupplier {
    private final String serviceId;
    private final AtomicInteger serviceCount = new AtomicInteger(1);

    @Override
    public String getServiceId() {
        return serviceId;
    }

    public DefaultServiceInstance createDefaultServiceInstance(String host, int port, boolean secure) {
        final String instanceId = getServiceId() + serviceCount.getAndIncrement();
        return new DefaultServiceInstance(instanceId, serviceId, host, port, secure);
    }

    public DefaultServiceInstance createDefaultServiceInstance(String host, int port) {
        return createDefaultServiceInstance(host, port, false);
    }

}

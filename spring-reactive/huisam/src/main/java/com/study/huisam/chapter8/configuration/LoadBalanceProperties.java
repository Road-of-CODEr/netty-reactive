package com.study.huisam.chapter8.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@Data
@ConstructorBinding
@ConfigurationProperties(prefix = "external.flask")
public class LoadBalanceProperties {

    private final List<FlaskServers> servers;

    @Data
    public static class FlaskServers {
        private final String host;
        private final int port;
    }
}

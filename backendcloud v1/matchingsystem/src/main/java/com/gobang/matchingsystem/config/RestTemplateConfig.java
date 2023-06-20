package com.gobang.matchingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        // RestTemplate 可以在两个spring boot之间进行通讯
        return new RestTemplate();
    }
}

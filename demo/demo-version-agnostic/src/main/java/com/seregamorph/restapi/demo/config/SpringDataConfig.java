package com.seregamorph.restapi.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

@Configuration
public class SpringDataConfig {

    @Bean
    SpelAwareProxyProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }
}

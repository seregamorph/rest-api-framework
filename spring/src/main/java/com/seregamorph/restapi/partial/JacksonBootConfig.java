package com.seregamorph.restapi.partial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonBootConfig {

    @Bean
    public PartialPayloadModule partialPayloadModule() {
        return new PartialPayloadModule();
    }

}

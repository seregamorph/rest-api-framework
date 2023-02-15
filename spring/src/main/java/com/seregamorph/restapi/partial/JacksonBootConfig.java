package com.seregamorph.restapi.partial;

import org.springframework.context.annotation.Bean;

public class JacksonBootConfig {

    @Bean
    public PartialPayloadModule partialPayloadModule() {
        return new PartialPayloadModule();
    }

}

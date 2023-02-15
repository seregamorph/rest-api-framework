package com.seregamorph.restapi.test.config;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;

public class MockMvcBootConfig {

    @Bean
    public MockMvcBuilderCustomizer defaultMockMvcBuilderCustomizer() {
        return MockMvcConfig::customizeMockMvc;
    }
}

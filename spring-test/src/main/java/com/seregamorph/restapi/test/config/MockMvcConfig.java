package com.seregamorph.restapi.test.config;

import static com.seregamorph.restapi.test.common.TestConstants.PROFILE_IT;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@Profile(PROFILE_IT)
@SuppressWarnings("unused")
public class MockMvcConfig {

    private static final Set<String> REQUEST_TYPES_WITH_CONTENT = Stream.of(POST, PUT, PATCH)
            .map(HttpMethod::name)
            .collect(Collectors.toSet());

    @Bean
    public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
        val builder = MockMvcBuilders.webAppContextSetup(webApplicationContext);
        customizeMockMvc(builder);
        return builder.build();
    }

    static void customizeMockMvc(ConfigurableMockMvcBuilder<?> builder) {
        builder.defaultRequest(get("/api")
                .header(ORIGIN, "http://localhost/test")
                .accept(APPLICATION_JSON, TEXT_PLAIN, ALL));

        builder.alwaysDo(new MockMvcPrintingResultHandler());
        builder.alwaysDo(new MockMvcProducesResultHandler());
        builder.alwaysDo(new MockMvcResponseStatusResultHandler());
        builder.alwaysDo(new MockMvcResponseContentResultHandler());

        builder.apply(new MockMvcConfigurerAdapter() {
            @Override
            public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder,
                                                             WebApplicationContext cxt) {
                return request -> {
                    if (REQUEST_TYPES_WITH_CONTENT.contains(request.getMethod()) && request.getContentType() == null) {
                        request.setContentType(APPLICATION_JSON_VALUE);
                    }
                    return request;
                };
            }
        });
    }

}

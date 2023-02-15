package com.seregamorph.restapi.test.config;

import com.seregamorph.restapi.client.LoggingInterceptor;
import com.seregamorph.restapi.test.interceptors.SkipTestInterceptor;
import com.seregamorph.restapi.test.interceptors.TestContextInterceptor;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class EmbeddedWebTestRestTemplateConfig {

    @Bean
    @ConditionalOnProperty(prefix = "web" , name = "environment", havingValue = "embedded")
    public RestTemplateCustomizer embeddedWebTestRestTemplateCustomizer() {
        return rest -> {
            // JDK HTTP client does not support PATCH http method
            // https://stackoverflow.com/questions/25163131/httpurlconnection-invalid-http-method-patch/46907921#46907921
            val requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setReadTimeout(60000);
            requestFactory.setConnectTimeout(5000);
            rest.setRequestFactory(requestFactory);

            val interceptors = rest.getInterceptors();
            interceptors.add(new SkipTestInterceptor());
            interceptors.add(new TestContextInterceptor());
            interceptors.add(new LoggingInterceptor());
        };
    }

}

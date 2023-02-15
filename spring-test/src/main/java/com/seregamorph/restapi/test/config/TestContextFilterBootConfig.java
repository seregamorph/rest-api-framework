package com.seregamorph.restapi.test.config;

import com.seregamorph.restapi.test.filters.TestContextFilter;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public class TestContextFilterBootConfig {

    public static final int ORDER = -110;

    @Bean
    @ConditionalOnProperty(prefix = "web" , name = "environment", havingValue = "embedded")
    public FilterRegistrationBean testContextFilter() {
        val filter = new TestContextFilter();
        val bean = new FilterRegistrationBean(filter);
        bean.setOrder(ORDER);
        return bean;
    }

}

package com.seregamorph.restapi.test.config;

import com.seregamorph.restapi.test.filters.SkipTestFilter;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public class SkipTestFilterBootConfig {

    public static final int ORDER = -120;

    @Bean
    @ConditionalOnProperty(prefix = "web" , name = "environment", havingValue = "embedded")
    public FilterRegistrationBean skipTestFilter() {
        val filter = new SkipTestFilter();
        val bean = new FilterRegistrationBean(filter);
        bean.setOrder(ORDER);
        return bean;
    }

}

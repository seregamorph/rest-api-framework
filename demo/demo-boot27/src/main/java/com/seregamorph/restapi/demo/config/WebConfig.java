package com.seregamorph.restapi.demo.config;

import com.seregamorph.restapi.demo.utils.CustomPageableHandlerMethodArgumentResolver;
import com.seregamorph.restapi.partial.JacksonBootConfig;
import com.seregamorph.restapi.resolvers.ProjectionNameHandlerMethodArgumentResolver;
import com.seregamorph.restapi.resolvers.SearchHandlerMethodArgumentResolver;
import com.seregamorph.restapi.resolvers.SortHandlerMethodArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Import(JacksonBootConfig.class)
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ProjectionNameHandlerMethodArgumentResolver());
        argumentResolvers.add(new SearchHandlerMethodArgumentResolver());
        argumentResolvers.add(new SortHandlerMethodArgumentResolver());
        argumentResolvers.add(new CustomPageableHandlerMethodArgumentResolver() {
            @Override
            protected PageRequest createPageRequest(int page, int size) {
                return PageRequest.of(page, size);
            }
        });
    }
}

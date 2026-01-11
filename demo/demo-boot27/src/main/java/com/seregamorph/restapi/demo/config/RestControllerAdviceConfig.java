package com.seregamorph.restapi.demo.config;

import com.seregamorph.restapi.exceptions.RestExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
@ComponentScan(
        basePackageClasses = {RestExceptionHandler.class},
        includeFilters = @ComponentScan.Filter(ControllerAdvice.class),
        useDefaultFilters = false)
public class RestControllerAdviceConfig {

}

package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.test.components.InfiniteRecursionDetector;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackageClasses = {MapperITConfig.class, InfiniteRecursionDetector.class},
        excludeFilters = @ComponentScan.Filter(classes = Configuration.class))
public class MapperITConfig {

}

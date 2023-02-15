package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.demo.VersionAgnosticDemoApplication;
import com.seregamorph.restapi.test.base.BaseResourceIT;
import com.seregamorph.restapi.test.components.InfiniteRecursionDetector;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import(AbstractResourceIT.ResourceConfig.class)
public abstract class AbstractResourceIT extends BaseResourceIT {

    public AbstractResourceIT(Class<? extends BaseResource> resourceClass) {
        super(resourceClass);
    }

    protected void shouldHitInfiniteRecursion(Class<?> projectionClass) throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Infinite recursion (StackOverflowError)");

        infiniteRecursionDetector.detect(resourceClass, projectionClass);
    }

    @Configuration
    @ComponentScan(basePackageClasses = {VersionAgnosticDemoApplication.class, InfiniteRecursionDetector.class})
    public static class ResourceConfig {

    }
}

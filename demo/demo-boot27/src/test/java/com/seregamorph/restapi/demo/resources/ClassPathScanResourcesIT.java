package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.demo.Boot23v210DemoApplication;
import com.seregamorph.restapi.test.base.BaseResourcesIT;
import com.seregamorph.restapi.test.components.InfiniteRecursionDetector;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import(ClassPathScanResourcesIT.ResourceConfig.class)
public class ClassPathScanResourcesIT extends BaseResourcesIT {

    @Configuration
    @ComponentScan(basePackageClasses = {Boot23v210DemoApplication.class, InfiniteRecursionDetector.class})
    public static class ResourceConfig {

    }
}

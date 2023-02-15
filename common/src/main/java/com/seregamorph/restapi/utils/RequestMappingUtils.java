package com.seregamorph.restapi.utils;

import com.seregamorph.restapi.annotations.Compatibility;
import java.util.Arrays;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

@UtilityClass
public class RequestMappingUtils {

    @Nonnull
    @Compatibility("spring 4.2+ allows to make derivative mapping annotations like GetMapping, PostMapping, etc.")
    public static String getControllerMapping(Class<?> controllerClass) {
        RequestMapping mapping;
        if (SpringVersions.isAtLeast("4.2")) {
            mapping = AnnotatedElementUtils.findMergedAnnotation(controllerClass, RequestMapping.class);
        } else {
            mapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
        }
        Assert.notNull(mapping, "Controller " + controllerClass.getName() + " does not have @RequestMapping");
        Assert.isTrue(mapping.value().length == 1, "Illegal paths in @RequestMapping of " + controllerClass + " "
                + Arrays.toString(mapping.value()) + ", should be single sized");
        return mapping.value()[0];
    }
}

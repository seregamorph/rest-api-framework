package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.annotations.Compatibility;
import com.seregamorph.restapi.utils.SpringVersions;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@UtilityClass
class TestRequestMappingUtils {

    @Nullable
    @Compatibility("spring 4.2+ allows to make derivative mapping annotations like GetMapping, PostMapping, etc.")
    static RequestMapping getMapping(Method controllerMethod) {
        if (SpringVersions.isAtLeast("4.2")) {
            return AnnotatedElementUtils.findMergedAnnotation(controllerMethod, RequestMapping.class);
        } else {
            return AnnotationUtils.findAnnotation(controllerMethod, RequestMapping.class);
        }
    }

    @Nullable
    @Compatibility("spring 4.2+ allows to have AliasFor annotation parameters")
    static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        if (SpringVersions.isAtLeast("4.2")) {
            return AnnotationUtils.findAnnotation(annotatedElement, annotationType);
        } else {
            return annotatedElement.getAnnotation(annotationType);
        }
    }

}

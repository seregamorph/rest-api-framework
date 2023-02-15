package com.seregamorph.restapi.test.utils;

import lombok.experimental.UtilityClass;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;

@UtilityClass
public class BaseTestUtils {

    public static boolean isMockWebEnvironment(Class<?> clazz) {
        try {
            Class.forName("org.springframework.boot.test.context.SpringBootTest");
        } catch (ClassNotFoundException e) {
            // org.springframework.boot.test.context.SpringBootTest is not available in non spring boot tests
            return true;
        }

        SpringBootTest springBootTest = AnnotationUtils.getAnnotation(clazz, SpringBootTest.class);
        return springBootTest == null || springBootTest.webEnvironment() == SpringBootTest.WebEnvironment.MOCK;
    }

}

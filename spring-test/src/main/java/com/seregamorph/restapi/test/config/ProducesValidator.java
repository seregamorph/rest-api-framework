package com.seregamorph.restapi.test.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

public class ProducesValidator {

    public boolean shouldValidate(HandlerMethod handlerMethod) {
        return true;
    }

    public void validate(HandlerMethod handlerMethod, HttpStatus httpStatus, MediaType responseContentType) {
        val producesMediaTypes = getProduces(handlerMethod);
        assertTrue(handlerMethod + " or it's enclosing class should declare @RequestMapping.produces "
                        + "that includes returned " + responseContentType,
                contains(producesMediaTypes, responseContentType));
    }

    protected static List<MediaType> getProduces(HandlerMethod handlerMethod) {
        val methodRequestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        assertNotNull("Missing @RequestMapping for " + handlerMethod, methodRequestMapping);
        String[] produces = methodRequestMapping.produces();
        if (produces.length == 0) {
            val controllerRequestMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
            assertNotNull("Missing @RequestMapping for " + handlerMethod.getBeanType()
                    + " (should define \"produces\" parameter)", controllerRequestMapping);
            produces = controllerRequestMapping.produces();
        }

        val result = new ArrayList<MediaType>();
        for (String mediaType : produces) {
            result.addAll(MediaType.parseMediaTypes(mediaType));
        }
        return result;
    }

    private static boolean contains(List<MediaType> producesMediaTypes, MediaType contentType) {
        for (val mediaType : producesMediaTypes) {
            if (mediaType.includes(contentType)) {
                return true;
            }
        }
        return false;
    }

}

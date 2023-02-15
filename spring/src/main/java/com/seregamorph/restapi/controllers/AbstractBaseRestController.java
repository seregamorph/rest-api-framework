package com.seregamorph.restapi.controllers;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;

import com.seregamorph.restapi.base.IdProjection;
import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.config.spi.FrameworkConfigHolder;
import java.io.Serializable;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class AbstractBaseRestController {

    protected final <T extends IdResource<?, T>> ResponseEntity<T> created(T created) {
        return created(getClass(), created);
    }

    protected final <T extends IdResource<?, T>> ResponseEntity<T> created(
            Class<? extends AbstractBaseRestController> controllerClass, T created) {
        return ResponseEntity.created(locationById(controllerClass, created.getId()))
                .body(created);
    }

    protected final <T extends IdProjection> ResponseEntity<T> created(T created) {
        return created(getClass(), created);
    }

    protected final <T extends IdProjection> ResponseEntity<T> created(
            Class<? extends AbstractBaseRestController> controllerClass, T created) {
        return ResponseEntity.created(locationById(controllerClass, created.getId()))
                .body(created);
    }

    protected final ResponseEntity<Void> created(Serializable id) {
        return created(getClass(), id);
    }

    protected final ResponseEntity<Void> created(Class<? extends AbstractBaseRestController> controllerClass,
                                                 Serializable id) {
        return ResponseEntity.created(locationById(controllerClass, id))
                .build();
    }

    protected final URI location(String pathTemplate, Object... pathVariables) {
        return location(getClass(), pathTemplate, pathVariables);
    }

    private static URI locationById(Class<? extends AbstractBaseRestController> controllerClass,
                                    Serializable id) {
        Assert.isTrue(id != null, "id is null");

        return location(controllerClass, ENDPOINT_ID, id);
    }

    private static URI location(Class<? extends AbstractBaseRestController> controllerClass,
                                String pathTemplate, Object... pathVariables) {
        val request = getCurrentRequest();
        return ServletUriComponentsBuilder.fromServletMapping(request)
                .path(getControllerMapping(controllerClass) + pathTemplate)
                .buildAndExpand(pathVariables)
                .toUri();
    }

    @Nonnull
    private static String getControllerMapping(Class<? extends AbstractBaseRestController> controllerClass) {
        return FrameworkConfigHolder.getFrameworkConfig()
                .getControllerMapping(controllerClass);
    }

    @Nonnull
    private static HttpServletRequest getCurrentRequest() {
        val requestAttributes = RequestContextHolder.currentRequestAttributes();
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}

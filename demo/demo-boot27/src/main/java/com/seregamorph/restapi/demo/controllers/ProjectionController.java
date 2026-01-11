package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.utils.ResourceFactory.MIN_USER_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.utils.ResourceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "projection")
@RestController
@RequestMapping(path = "/api/projection", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProjectionController {

    private final ProjectionFactory projectionFactory;

//    @ApiOperation(value = "Get", response = UserResource.class)
    @GetMapping
    public BaseProjection get(
            UserResource.Projection projection
    ) {
        return projectionFactory.createProjection(projection.getProjectionClass(), ResourceFactory.user(MIN_USER_ID));
    }
}

package com.seregamorph.restapi.demo.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.resources.partial.UserPostPartial;
import com.seregamorph.restapi.demo.services.UserService;
import com.seregamorph.restapi.partial.PayloadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "external")
@RestController
@RequestMapping(path = "/api/external", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExternalLocationController extends AbstractBaseRestController {

    private final UserService userService;

//    @ApiOperation("Create a user")
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<UserResource> create(
            @PayloadModel(UserPostPartial.class)
            @RequestBody UserResource user
    ) {
        return created(PartialController.class, userService.create(user));
    }

}

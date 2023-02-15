package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.common.Constants.PARAM_ID;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.resources.partial.UserPatchPartial;
import com.seregamorph.restapi.demo.resources.partial.UserPostPartial;
import com.seregamorph.restapi.demo.services.UserService;
import com.seregamorph.restapi.demo.utils.ResourceFactory;
import com.seregamorph.restapi.partial.PayloadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "partial")
@RestController
@RequestMapping(path = PartialController.ENDPOINT, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PartialController extends AbstractBaseRestController {

    static final String ENDPOINT = "/api/partial";

    private final UserService userService;

//    @ApiOperation("Get a user")
    @GetMapping(ENDPOINT_ID)
    public UserResource get(@PathVariable(PARAM_ID) long userId) {
        return ResourceFactory.user(userId);
    }

//    @ApiOperation("Create a user")
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<UserResource> create(
            @PayloadModel(UserPostPartial.class)
            @RequestBody
            final UserResource user
    ) {
        return created(userService.create(user));
    }

//    @ApiOperation("Update a user")
    @PatchMapping
    public UserResource update(
            @PayloadModel(UserPatchPartial.class)
            @RequestBody
            final UserResource user
    ) {
        return userService.update(user);
    }
}

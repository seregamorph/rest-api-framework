package com.seregamorph.restapi.demo.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import com.seregamorph.restapi.demo.resources.PersonResource;
import com.seregamorph.restapi.demo.resources.TeamResource;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "infinite-recursion")
@RestController
@RequestMapping(path = InfiniteRecursionController.ENDPOINT, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InfiniteRecursionController extends AbstractBaseRestController {

    static final String ENDPOINT = "/api/infinite-recursion";

    @ResponseStatus(CREATED)
//    @ApiOperation("Create a recursive team")
    @PostMapping
    public ResponseEntity<TeamResource> post() {
        val team = new TeamResource()
                .setId(1L);
        return created(team.setMembers(Collections.singletonList(new PersonResource()
                .setTeam(team))));
    }

}

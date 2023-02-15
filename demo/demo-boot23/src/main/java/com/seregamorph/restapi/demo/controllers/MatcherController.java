package com.seregamorph.restapi.demo.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import com.seregamorph.restapi.demo.resources.MatcherResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = "matcher")
@RestController
@RequestMapping(path = "/api/matcher", produces = APPLICATION_JSON_VALUE)
public class MatcherController extends AbstractBaseRestController {

//    @ApiOperation("Get a resource")
    @GetMapping
    public MatcherResource get() {
        return new MatcherResource();
    }
}

package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.common.Constants.PARAM_ID;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import com.seregamorph.restapi.demo.entities.Person;
import com.seregamorph.restapi.demo.mappers.PersonMapper;
import com.seregamorph.restapi.demo.resources.PersonResource;
import com.seregamorph.restapi.demo.resources.partial.PersonPatchPartial;
import com.seregamorph.restapi.demo.resources.partial.PersonPostPartial;
import com.seregamorph.restapi.demo.services.PersonService;
import com.seregamorph.restapi.partial.PayloadModel;
import lombok.RequiredArgsConstructor;
import lombok.val;
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
//@Api(tags = "framework")
@RestController
@RequestMapping(path = FrameworkController.ENDPOINT, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FrameworkController extends AbstractBaseRestController {

    static final String ENDPOINT = "/api/framework";

    private final PersonService personService;
    private final PersonMapper personMapper;

//    @ApiOperation("Get person by id")
    @GetMapping(ENDPOINT_ID)
    public PersonResource get(@PathVariable(PARAM_ID) long id) {
        val person = personService.getOne(id);
        return personMapper.pruningMap(person);
    }

//    @ApiOperation("Create a person")
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<PersonResource> create(
            @PayloadModel(PersonPostPartial.class)
            @RequestBody PersonResource resource
    ) {
        val created = personService.create(personMapper.map(resource));
        return created(personMapper.pruningMap(created));
    }

//    @ApiOperation("Update a person")
    @PatchMapping(ENDPOINT_ID)
    public PersonResource update(
            @PathVariable(PARAM_ID) long id,
            @PayloadModel(PersonPatchPartial.class)
            @RequestBody PersonResource update
    ) {
        Person person = personService.getOne(id);
        PersonResource resource = personMapper.map(person);
        update.copyTo(resource);
        Person personToUpdate = personMapper.map(resource);
        Person updated = personService.update(id, personToUpdate);
        return personMapper.pruningMap(updated);
    }

}

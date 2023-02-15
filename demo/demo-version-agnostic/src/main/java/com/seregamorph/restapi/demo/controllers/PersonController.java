package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_BULK_CREATE;
import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.common.Constants.PARAM_ID;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.IdProjection;
import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import com.seregamorph.restapi.demo.entities.Checkin;
import com.seregamorph.restapi.demo.entities.Person;
import com.seregamorph.restapi.demo.mappers.CheckinMapper;
import com.seregamorph.restapi.demo.mappers.PersonMapper;
import com.seregamorph.restapi.demo.resources.CheckinResource;
import com.seregamorph.restapi.demo.resources.PersonResource;
import com.seregamorph.restapi.demo.resources.partial.CheckinPutPartial;
import com.seregamorph.restapi.demo.resources.partial.PersonPatchPartial;
import com.seregamorph.restapi.demo.resources.partial.PersonPostPartial;
import com.seregamorph.restapi.demo.resources.partial.PersonPostPartials;
import com.seregamorph.restapi.demo.services.CheckinService;
import com.seregamorph.restapi.demo.services.PersonService;
import com.seregamorph.restapi.partial.PayloadModel;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "person")
@RestController
@RequestMapping(path = "/api/persons", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PersonController extends AbstractBaseRestController {

    static final String PARAM_PERSON_ID = "personId";
    static final String PARAM_CHECKIN_DATE = "checkinDate";

    // We'll use these parameters to avoid creating too many APIs. We wish to cover as many cases as possible
    // to test the framework and to compare performance.

    static final String PARAM_DIRECT_PROJECTION = "directProjection";
    static final String PARAM_DIRECT_TRANSFER = "directTransfer";

    static final String ENDPOINT_SINGLE_CHECKIN = "/{" + PARAM_PERSON_ID + "}/checkins/{" + PARAM_CHECKIN_DATE + "}";

    static final String ENDPOINT_LIST_RESOURCES = "/list-resources";
    static final String ENDPOINT_LIST_PROJECTIONS = "/list-projections";

    static final String ENDPOINT_GET_PROJECTION = ENDPOINT_ID + "/get-projection";

    private final PersonService personService;
    private final CheckinService checkinService;

    private final PersonMapper personMapper;
    private final CheckinMapper checkinMapper;

    private final ProjectionFactory projectionFactory;

//    @ApiOperation(value = "List person resources",
//            response = PersonResource.class,
//            responseContainer = RESPONSE_CONTAINER_LIST)
    @GetMapping(ENDPOINT_LIST_RESOURCES)
    public List<PersonResource> listResources() {
        return personService.getAll().stream().map(personMapper::pruningMap).collect(Collectors.toList());
    }

//    @ApiOperation(value = "List person projections",
//            response = PersonResource.class,
//            responseContainer = RESPONSE_CONTAINER_LIST)
    @GetMapping(ENDPOINT_LIST_PROJECTIONS)
    public List<BaseProjection> listProjections(
            @RequestParam(PARAM_DIRECT_PROJECTION)
            final boolean directProjection,

            PersonResource.Projection projection
    ) {
        Function<Person, Object> mapper = directProjection
                ? person -> person
                : person -> personMapper.map(person, projection.getProjectionClass());
        return personService.getAll()
                .stream()
                .map(mapper)
                .map(person -> projectionFactory.createProjection(projection.getProjectionClass(), person))
                .collect(Collectors.toList());
    }

//    @ApiOperation(value = "Get person resource", response = PersonResource.class)
    @GetMapping(ENDPOINT_ID)
    public PersonResource getResource(
            @PathVariable(PARAM_ID)
            final long id
    ) {
        Person person = personService.getOne(id);
        return personMapper.pruningMap(person);
    }

//    @ApiOperation(value = "Get person projection", response = PersonResource.class)
    @GetMapping(ENDPOINT_GET_PROJECTION)
    public BaseProjection getProjection(
            @PathVariable(PARAM_ID)
            final long id,

            @RequestParam(PARAM_DIRECT_PROJECTION)
            final boolean directProjection,

            PersonResource.Projection projection
    ) {
        Person person = personService.getOne(id);
        Object projectionSource = directProjection ? person : personMapper.map(person, projection.getProjectionClass());
        return projectionFactory.createProjection(projection.getProjectionClass(), projectionSource);
    }

//    @ApiOperation("Create a person")
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<IdProjection> create(
            @PayloadModel(PersonPostPartial.class)
            @RequestBody
            final PersonResource resource,

            @RequestParam(value = PARAM_DIRECT_PROJECTION, required = false)
            final boolean directProjection,

            PersonResource.Projection projection
    ) {
        return created(createInternally(resource, directProjection, projection));
    }

//    @ApiOperation("Bulk create persons")
    @PostMapping(ENDPOINT_BULK_CREATE)
    public List<IdProjection> bulkCreate(
            @PayloadModel(PersonPostPartials.class)
            @RequestBody
            final List<PersonResource> resources,

            @RequestParam(value = PARAM_DIRECT_PROJECTION, required = false)
            final boolean directProjection,

            PersonResource.Projection projection
    ) {
        return resources.stream()
                .map(resource -> createInternally(resource, directProjection, projection))
                .collect(Collectors.toList());
    }

    private IdProjection createInternally(PersonResource resource,
                                          boolean directProjection,
                                          PersonResource.Projection projection) {
        Person person = personMapper.map(resource);
        Person createdPerson = personService.create(person);
        Object projectionSource = directProjection
                ? createdPerson
                : personMapper.map(createdPerson, projection.getProjectionClass());
        return projectionFactory.createProjection(projection.getProjectionClass(), projectionSource);
    }

//    @ApiOperation("Update a person")
    @PatchMapping(ENDPOINT_ID)
    public BaseProjection update(
            @PathVariable(PARAM_ID)
            final long id,

            @PayloadModel(PersonPatchPartial.class)
            @RequestBody
            final PersonResource update,

            @RequestParam(value = PARAM_DIRECT_PROJECTION, required = false)
            final boolean directProjection,

            @RequestParam(value = PARAM_DIRECT_TRANSFER, required = false)
            final boolean directTransfer,

            PersonResource.Projection projection
    ) {
        Person person = personService.getOne(id);
        Person updatedPerson;
        if (directTransfer) {
            update.copyTo(person);
            updatedPerson = personService.update(id, person);
        } else {
            PersonResource resource = personMapper.map(person);
            update.copyTo(resource);
            Person personToUpdate = personMapper.map(resource);
            updatedPerson = personService.update(id, personToUpdate);
        }
        Object projectionSource = directProjection
                ? updatedPerson
                : personMapper.map(updatedPerson, projection.getProjectionClass());
        return projectionFactory.createProjection(projection.getProjectionClass(), projectionSource);
    }

    // Note: For Put, we return resource directly here! No projection support, no transfer strategy!
//    @ApiOperation("Replace a checkin")
    @PutMapping(ENDPOINT_SINGLE_CHECKIN)
    public CheckinResource put(
            @PathVariable(PARAM_PERSON_ID)
            final long personId,

            @PathVariable(PARAM_CHECKIN_DATE)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            final LocalDate checkinDate,

            @PayloadModel(CheckinPutPartial.class)
            @RequestBody
            final CheckinResource resource
    ) {
        Checkin checkin = checkinMapper.map(resource);
        Checkin savedCheckin = checkinService.replace(personId, checkinDate, checkin);
        return checkinMapper.pruningMap(savedCheckin);
    }

//    @ApiOperation("Delete a person")
    @DeleteMapping(ENDPOINT_ID)
    @ResponseStatus(NO_CONTENT)
    public void delete(
            @PathVariable(PARAM_ID)
            final long id
    ) {
        personService.delete(id);
    }
}

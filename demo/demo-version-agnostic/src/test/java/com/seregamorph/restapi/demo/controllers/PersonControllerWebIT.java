package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_BULK_CREATE;
import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.demo.controllers.PersonController.ENDPOINT_GET_PROJECTION;
import static com.seregamorph.restapi.demo.controllers.PersonController.ENDPOINT_LIST_PROJECTIONS;
import static com.seregamorph.restapi.demo.controllers.PersonController.ENDPOINT_LIST_RESOURCES;
import static com.seregamorph.restapi.demo.controllers.PersonController.ENDPOINT_SINGLE_CHECKIN;
import static com.seregamorph.restapi.demo.controllers.PersonController.PARAM_DIRECT_PROJECTION;
import static com.seregamorph.restapi.demo.controllers.PersonController.PARAM_DIRECT_TRANSFER;
import static com.seregamorph.restapi.test.base.JsonMatcher.auditableValue;
import static com.seregamorph.restapi.test.base.JsonMatcher.immutableValue;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.JsonMatcher.matching;
import static com.seregamorph.restapi.test.base.ResourceType.CREATED;
import static com.seregamorph.restapi.test.base.ResourceType.EXISTING;
import static com.seregamorph.restapi.test.base.ResourceType.UPDATED;
import static com.seregamorph.restapi.test.base.ResultType.LIST;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.generic;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.required;
import static com.seregamorph.restapi.test.base.support.RequestType.RETRIEVAL;

import com.seregamorph.restapi.demo.resources.CheckinIdResource;
import com.seregamorph.restapi.demo.resources.CheckinResource;
import com.seregamorph.restapi.demo.resources.PersonResource;
import com.seregamorph.restapi.demo.resources.TeamResource;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.ResourceType;
import com.seregamorph.restapi.test.base.setup.DeleteSetup;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;
import com.seregamorph.restapi.test.base.setup.PatchSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import com.seregamorph.restapi.test.base.setup.PutSetup;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@InitTest(PersonController.class)
public abstract class PersonControllerWebIT extends AbstractBaseWebIT {

    private static final TeamResource TEAM_1 = new TeamResource();
    private static final PersonResource PERSON_1 = new PersonResource();
    private static final PersonResource PERSON_4 = new PersonResource();
    private static final PersonResource PERSON_NEW = new PersonResource();
    private static final PersonResource PERSON_UPDATED = new PersonResource();
    private static final CheckinResource CHECKIN = new CheckinResource();

    static {
        TEAM_1
                .setId(1L)
                .setName("First Team")
                .setDescription("The first team")
                .setManager(PERSON_1)
                .setMembers(Arrays.asList(PERSON_1, PERSON_4))
                .setCreatedDate(Instant.parse("2020-12-01T04:05:48.757Z"))
                .setLastModifiedDate(Instant.parse("2020-12-01T04:05:48.757Z"));
    }

    static {
        PERSON_1
                .setId(1L)
                .setName("John Smith")
                .setYearOfBirth(1950)
                .setEmailAddress("john.smith@company.com")
                .setActivationDate(Instant.parse("2020-12-01T04:05:48.757Z"))
                .setTeam(TEAM_1)
                .setManagedTeams(Collections.singletonList(TEAM_1))
                .setCreatedDate(Instant.parse("2020-12-01T04:05:48.757Z"))
                .setLastModifiedDate(Instant.parse("2020-12-01T04:05:48.757Z"));
    }

    static {
        PERSON_4
                .setId(4L)
                .setName("Michael Geller")
                .setYearOfBirth(1951)
                .setEmailAddress("michael.geller@company.com")
                .setActivationDate(Instant.parse("2020-12-01T04:05:48.757Z"))
                .setTeam(TEAM_1)
                .setManagedTeams(Collections.singletonList(TEAM_1))
                .setCreatedDate(Instant.parse("2020-12-01T04:05:48.757Z"))
                .setLastModifiedDate(Instant.parse("2020-12-01T04:05:48.757Z"));
    }

    static {
        PERSON_NEW
                .setName("Person")
                .setYearOfBirth(1950)
                .setEmailAddress("person@address.com")
                .setActivationDate(Instant.now())
                .setTeam(TEAM_1);
    }

    static {
        PERSON_UPDATED
                .setId(PERSON_1.getId())
                .setName("AnotherPerson")
                .setYearOfBirth(1960)
                .setEmailAddress("another.person@address.com")
                .setActivationDate(Instant.now())
                .setTeam(TEAM_1)
                .setManagedTeams(Collections.singletonList(TEAM_1))
                .setCreatedDate(PERSON_1.getCreatedDate())
                .setLastModifiedDate(PERSON_1.getLastModifiedDate());
    }

    static {
        CHECKIN
                .setId(new CheckinIdResource()
                        .setPersonId(PERSON_1.getId())
                        .setCheckinDate(LocalDate.now()))
                .setPerson(PERSON_1)
                .setMessage("message")
                .setCreatedDate(Instant.now())
                .setLastModifiedDate(Instant.now());
    }

    @InitTest
    public static GetAllSetup getAllSetup() {
        // List resources
        return new GetAllSetup(ENDPOINT_LIST_RESOURCES)
                .setTotalElements(10)
                .setDefaultResultMatchers(prunedMatcher());
    }

    @InitTest
    public static GetAllSetup getAllSetupListProjectionsDirect() {
        // List projections, directProjection = true
        return new GetAllSetup(ENDPOINT_LIST_PROJECTIONS)
                .setTotalElements(10)
                .setDefaultResultMatchers(defaultMatcher())
                .provideParameter(PARAM_DIRECT_PROJECTION, true)
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher())
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher())
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher());
    }

    @InitTest
    public static GetAllSetup getAllSetupListProjectionsWithoutDirect() {
        // List projections, directProjection = false
        return new GetAllSetup(ENDPOINT_LIST_PROJECTIONS)
                .setTotalElements(10)
                .setDefaultResultMatchers(defaultMatcher())
                .provideParameter(PARAM_DIRECT_PROJECTION, false)
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher())
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher())
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher());
    }

    @InitTest
    public static GetOneSetup getOneSetupGetResource() {
        // Get resource
        return new GetOneSetup(ENDPOINT_ID, PERSON_1.getId())
                .setDefaultResultMatchers(prunedMatcher());
    }

    @InitTest
    public static GetOneSetup getOneSetupGetProjectionDirect() {
        // Get projection, directProjection = true
        return new GetOneSetup(ENDPOINT_GET_PROJECTION, PERSON_1.getId())
                .setDefaultResultMatchers(defaultMatcher())
                .provideParameter(PARAM_DIRECT_PROJECTION, true)
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher())
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher())
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher());
    }

    @InitTest
    public static GetOneSetup getOneSetupGetProjectionWithoutDirect() {
        // Get projection, directProjection = false
        return new GetOneSetup(ENDPOINT_GET_PROJECTION, PERSON_1.getId())
                .setDefaultResultMatchers(defaultMatcher())
                .provideParameter(PARAM_DIRECT_PROJECTION, false)
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher())
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher())
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher());
    }

    @InitTest
    public static PostSetup postSetupDirect() {
        // Post, directProjection = true
        return new PostSetup()
                .provideParameter(PARAM_DIRECT_PROJECTION, true)
                .setRequestPayload(genericPostPayload())
                .setDefaultResultMatchers(defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_NEW, CREATED));
    }

    @InitTest
    public static PostSetup postSetupWithoutDirectProjection() {
        // Post, directProjection = false
        return new PostSetup()
                .provideParameter(PARAM_DIRECT_PROJECTION, false)
                .setRequestPayload(genericPostPayload())
                .setDefaultResultMatchers(defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_NEW, CREATED));
    }

    @InitTest
    public static PostSetup postSetupBulkCreateDirect() {
        // Bulk post, directProjection = true
        return new PostSetup(ENDPOINT_BULK_CREATE)
                .provideParameter(PARAM_DIRECT_PROJECTION, true)
                .setRequestPayload(Collections.singletonList(genericPostPayload()))
                .setRequestType(RETRIEVAL)
                .setResultType(LIST)
                .setDefaultResultMatchers(defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_NEW, CREATED));
    }

    @InitTest
    public static PostSetup postSetupBulkCreateWithoutDirect() {
        // Bulk post, directProjection = false
        return new PostSetup(ENDPOINT_BULK_CREATE)
                .provideParameter(PARAM_DIRECT_PROJECTION, false)
                .setRequestPayload(Collections.singletonList(genericPostPayload()))
                .setRequestType(RETRIEVAL)
                .setResultType(LIST)
                .setDefaultResultMatchers(defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_NEW, CREATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_NEW, CREATED));
    }

    @InitTest
    public static PatchSetup patchSetupDirectTransfer() {
        // Patch, directProjection = true, directTransfer = true
        return new PatchSetup(ENDPOINT_ID, PERSON_UPDATED.getId())
                .provideParameter(PARAM_DIRECT_PROJECTION, true)
                .provideParameter(PARAM_DIRECT_TRANSFER, true)
                .setRequestType(RETRIEVAL)
                .setRequestPayload(genericPatchPayload())
                .setDefaultResultMatchers(defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_UPDATED, UPDATED));
    }

    @InitTest
    public static PatchSetup patchSetupWithoutDirectTransfer() {
        // Patch, directProjection = true, directTransfer = false
        return new PatchSetup(ENDPOINT_ID, PERSON_UPDATED.getId())
                .provideParameter(PARAM_DIRECT_PROJECTION, true)
                .provideParameter(PARAM_DIRECT_TRANSFER, false)
                .setRequestType(RETRIEVAL)
                .setRequestPayload(genericPatchPayload())
                .setDefaultResultMatchers(defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_UPDATED, UPDATED));
    }

    @InitTest
    public static PatchSetup patchSetupWithoutDirectProjection() {
        // Patch, directProjection = false, directTransfer = true
        return new PatchSetup(ENDPOINT_ID, PERSON_UPDATED.getId())
                .provideParameter(PARAM_DIRECT_PROJECTION, false)
                .provideParameter(PARAM_DIRECT_TRANSFER, true)
                .setRequestType(RETRIEVAL)
                .setRequestPayload(genericPatchPayload())
                .setDefaultResultMatchers(defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_UPDATED, UPDATED));
    }

    @InitTest
    public static PatchSetup patchSetupWithoutDirectProjectionAndWithoutDirectTransfer() {
        // Patch, directProjection = false, directTransfer = false
        return new PatchSetup(ENDPOINT_ID, PERSON_UPDATED.getId())
                .provideParameter(PARAM_DIRECT_PROJECTION, false)
                .provideParameter(PARAM_DIRECT_TRANSFER, false)
                .setRequestType(RETRIEVAL)
                .setRequestPayload(genericPatchPayload())
                .setDefaultResultMatchers(defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DEFAULT, defaultMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.COMPACT, compactMatcher(PERSON_UPDATED, UPDATED))
                .supportProjection(PersonResource.Projection.DETAIL, detailMatcher(PERSON_UPDATED, UPDATED));
    }

    @InitTest
    public static PutSetup putSetupSingleCheckin() {
        return new PutSetup(ENDPOINT_SINGLE_CHECKIN,
                CHECKIN.getId().getPersonId(), CHECKIN.getId().getCheckinDate())
                .setRequestType(RETRIEVAL)
                .setRequestPayload(genericPutPayload())
                .setDefaultResultMatchers(checkinMatcher());
    }

    @InitTest
    public static DeleteSetup deleteSetup() {
        return new DeleteSetup(ENDPOINT_ID, PERSON_UPDATED.getId());
    }

    private static PersonResource genericPostPayload() {
        return generic(PersonResource.class)
                .setName(required(PERSON_NEW.getName()))
                .setYearOfBirth(PERSON_NEW.getYearOfBirth())
                .setEmailAddress(required(PERSON_NEW.getEmailAddress()))
                .setActivationDate(PERSON_NEW.getActivationDate())
                .setTeam(required(generic(TeamResource.class)
                        .setId(required(PERSON_NEW.getTeam().getId()))));
    }

    private static PersonResource genericPatchPayload() {
        return generic(PersonResource.class)
                .setName(PERSON_UPDATED.getName())
                .setYearOfBirth(PERSON_UPDATED.getYearOfBirth())
                .setEmailAddress(PERSON_UPDATED.getEmailAddress())
                .setActivationDate(PERSON_UPDATED.getActivationDate())
                .setTeam(generic(TeamResource.class)
                        .setId(required(PERSON_UPDATED.getTeam().getId())));
    }

    private static CheckinResource genericPutPayload() {
        return generic(CheckinResource.class)
                .setMessage("message");
    }

    private static TeamResource teamCompactMatcher(TeamResource team) {
        return jsonMatching(TeamResource.class)
                .setId(team.getId())
                .setName(team.getName())
                .setDescription(team.getDescription());
    }

    private static TeamResource teamPrunedMatcher(TeamResource team) {
        return jsonMatching(TeamResource.class)
                .setId(team.getId())
                .setName(team.getName())
                .setDescription(team.getDescription())
                .setManager(matching(team.getManager(), PersonControllerWebIT::defaultMatcher))
                .setMembers(team.getMembers()
                        .stream()
                        .map(PersonControllerWebIT::defaultMatcher)
                        .collect(Collectors.toList()))
                .setCreatedDate(team.getCreatedDate())
                .setLastModifiedDate(team.getLastModifiedDate());
    }

    private static PersonResource defaultMatcher() {
        return defaultMatcher(PERSON_1);
    }

    private static PersonResource defaultMatcher(PersonResource person) {
        return defaultMatcher(person, EXISTING);
    }

    private static PersonResource defaultMatcher(PersonResource person, ResourceType resourceType) {
        return jsonMatching(PersonResource.class)
                .setId(immutableValue(resourceType, person.getId()));
    }

    private static PersonResource detailMatcher() {
        return detailMatcher(PERSON_1);
    }

    @SuppressWarnings("SameParameterValue")
    private static PersonResource detailMatcher(PersonResource person) {
        return detailMatcher(person, EXISTING);
    }

    private static PersonResource detailMatcher(PersonResource person, ResourceType resourceType) {
        return jsonMatching(PersonResource.class)
                .setId(immutableValue(resourceType, person.getId()))
                .setName(person.getName())
                .setYearOfBirth(person.getYearOfBirth())
                .setEmailAddress(person.getEmailAddress())
                .setActivationDate(person.getActivationDate())
                .setTeam(teamCompactMatcher(person.getTeam()))
                .setManagedTeams(person.getManagedTeams()
                        .stream()
                        .map(PersonControllerWebIT::teamCompactMatcher)
                        .collect(Collectors.toList()))
                .setCreatedDate(immutableValue(resourceType, person.getCreatedDate()))
                .setLastModifiedDate(auditableValue(resourceType, person.getLastModifiedDate()));
    }

    private static PersonResource compactMatcher() {
        return compactMatcher(PERSON_1);
    }

    @SuppressWarnings("SameParameterValue")
    private static PersonResource compactMatcher(PersonResource person) {
        return compactMatcher(person, EXISTING);
    }

    private static PersonResource compactMatcher(PersonResource person, ResourceType resourceType) {
        return jsonMatching(PersonResource.class)
                .setId(immutableValue(resourceType, person.getId()))
                .setName(person.getName())
                .setYearOfBirth(person.getYearOfBirth())
                .setEmailAddress(person.getEmailAddress())
                .setActivationDate(person.getActivationDate())
                .setCreatedDate(immutableValue(resourceType, person.getCreatedDate()))
                .setLastModifiedDate(auditableValue(resourceType, person.getLastModifiedDate()));
    }

    private static PersonResource prunedMatcher() {
        return jsonMatching(PersonResource.class)
                .setId(PERSON_1.getId())
                .setName(PERSON_1.getName())
                .setYearOfBirth(PERSON_1.getYearOfBirth())
                .setEmailAddress(PERSON_1.getEmailAddress())
                .setActivationDate(PERSON_1.getActivationDate())
                .setTeam(matching(PERSON_1.getTeam(), PersonControllerWebIT::teamPrunedMatcher))
                .setManagedTeams(PERSON_1.getManagedTeams()
                        .stream()
                        .map(PersonControllerWebIT::teamPrunedMatcher)
                        .collect(Collectors.toList()))
                .setCreatedDate(PERSON_1.getCreatedDate())
                .setLastModifiedDate(PERSON_1.getLastModifiedDate());
    }

    private static CheckinResource checkinMatcher() {
        return jsonMatching(CheckinResource.class)
                .setId(jsonMatching(CheckinIdResource.class)
                        .setPersonId(CHECKIN.getId().getPersonId())
                        .setCheckinDate(CHECKIN.getId().getCheckinDate()))
                .setMessage(CHECKIN.getMessage())
                .setPerson(prunedMatcher())
                .setCreatedDate(immutableValue(CREATED, CHECKIN.getCreatedDate()))
                .setLastModifiedDate(auditableValue(CREATED, CHECKIN.getLastModifiedDate()));
    }
}

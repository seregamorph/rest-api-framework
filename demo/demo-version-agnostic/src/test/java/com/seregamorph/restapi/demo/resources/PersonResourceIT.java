package com.seregamorph.restapi.demo.resources;

import org.junit.Test;

public class PersonResourceIT extends AbstractResourceIT {

    public PersonResourceIT() {
        super(PersonResource.class);
    }

    @Test
    public void personWithNestedTeamProjectionShouldLeadToInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(PersonWithNestedTeamProjection.class);
    }

    @Test
    public void personWithNestedManagedTeamsProjectionShouldLeadToInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(PersonWithNestedManagedTeamsProjection.class);
    }
}

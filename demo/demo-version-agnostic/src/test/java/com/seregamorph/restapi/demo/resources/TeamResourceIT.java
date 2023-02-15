package com.seregamorph.restapi.demo.resources;

import org.junit.Test;

public class TeamResourceIT extends AbstractResourceIT {

    public TeamResourceIT() {
        super(TeamResource.class);
    }

    @Test
    public void teamWithNestedManagerProjectionShouldLeadToInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(TeamWithNestedManagerProjection.class);
    }

    @Test
    public void teamWithNestedMembersProjectionShouldLeadToInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(TeamWithNestedMembersProjection.class);
    }
}

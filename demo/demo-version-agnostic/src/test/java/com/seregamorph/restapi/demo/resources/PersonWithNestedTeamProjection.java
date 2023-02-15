package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.IdProjection;

// For infinite recursion testing (not listed in Projection enum)
public interface PersonWithNestedTeamProjection extends IdProjection {

    String getName();

    String getEmailAddress();

    TeamWithNestedMembersProjection getTeam();
}

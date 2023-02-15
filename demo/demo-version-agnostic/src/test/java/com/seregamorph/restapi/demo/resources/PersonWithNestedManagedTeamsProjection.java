package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.IdProjection;
import java.util.List;

// For infinite recursion testing (not listed in Projection enum)
public interface PersonWithNestedManagedTeamsProjection extends IdProjection {

    String getName();

    String getEmailAddress();

    List<TeamWithNestedManagerProjection> getManagedTeams();
}

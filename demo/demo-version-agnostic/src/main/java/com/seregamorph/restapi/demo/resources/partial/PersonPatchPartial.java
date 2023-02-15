package com.seregamorph.restapi.demo.resources.partial;

import com.seregamorph.restapi.base.BasePartial;
import com.seregamorph.restapi.base.IdPartial;
import java.time.Instant;

public interface PersonPatchPartial extends BasePartial {

    String getName();

    Integer getYearOfBirth();

    String getEmailAddress();

    Instant getActivationDate();

    IdPartial<Long> getTeam();
}

package com.seregamorph.restapi.demo.resources.partial;

import com.seregamorph.restapi.base.BasePartial;
import com.seregamorph.restapi.base.IdPartial;
import com.seregamorph.restapi.partial.Required;
import java.time.Instant;

public interface PersonPostPartial extends BasePartial {

    @Required
    String getName();

    Integer getYearOfBirth();

    @Required
    String getEmailAddress();

    Instant getActivationDate();

    @Required
    IdPartial<Long> getTeam();
}

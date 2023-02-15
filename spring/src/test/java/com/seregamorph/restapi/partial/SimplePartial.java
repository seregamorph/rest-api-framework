package com.seregamorph.restapi.partial;

import com.seregamorph.restapi.base.IdPartial;

public interface SimplePartial extends IdPartial<Long> {

    @Required
    String getName();

    @Required
    String getTitle();
}

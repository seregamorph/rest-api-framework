package com.seregamorph.restapi.demo.resources.partial;

import com.seregamorph.restapi.base.BasePartial;
import com.seregamorph.restapi.base.IdPartial;
import com.seregamorph.restapi.partial.Required;

public interface UserPostPartial extends BasePartial {

    @Required
    String getName();

    int getAge();

    String getAddress();

    @Required
    IdPartial<Long> getGroup();

}

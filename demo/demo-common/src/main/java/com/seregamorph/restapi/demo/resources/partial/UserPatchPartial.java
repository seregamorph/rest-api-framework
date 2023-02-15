package com.seregamorph.restapi.demo.resources.partial;

import com.seregamorph.restapi.base.BasePartial;
import com.seregamorph.restapi.base.IdPartial;
import com.seregamorph.restapi.demo.resources.UserStatus;

public interface UserPatchPartial extends BasePartial {

    String getName();

    int getAge();

    String getAddress();

    UserStatus getStatus();

    IdPartial<Long> getGroup();

}

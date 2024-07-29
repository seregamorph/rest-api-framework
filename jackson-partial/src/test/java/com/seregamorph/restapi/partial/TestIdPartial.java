package com.seregamorph.restapi.partial;

import com.seregamorph.restapi.base.BasePartial;

import java.io.Serializable;

public interface TestIdPartial<T extends Serializable> extends BasePartial {

    @Required
    T getId();
}

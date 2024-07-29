package com.seregamorph.restapi.base;

import com.seregamorph.restapi.partial.Required;
import java.io.Serializable;

public interface IdPartial<T extends Serializable> extends BasePartial {

    @Required
    T getId();
}

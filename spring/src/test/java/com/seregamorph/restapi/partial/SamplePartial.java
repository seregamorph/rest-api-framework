package com.seregamorph.restapi.partial;

import com.seregamorph.restapi.base.IdPartial;
import java.util.List;

public interface SamplePartial extends IdPartial<Long> {

    @Required
    String getNormalField1();

    @Required
    SimplePartial getPartialResourceField1();

    @Required
    List<? extends SimplePartial> getPartialResourceCollection1();

    @Required
    SimplePartial[] getPartialResourceArray1();
}

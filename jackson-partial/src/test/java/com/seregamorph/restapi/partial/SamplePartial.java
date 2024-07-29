package com.seregamorph.restapi.partial;

import java.util.List;

public interface SamplePartial extends TestIdPartial<Long> {

    @Required
    String getNormalField1();

    @Required
    SimplePartial getPartialResourceField1();

    @Required
    List<? extends SimplePartial> getPartialResourceCollection1();

    @Required
    SimplePartial[] getPartialResourceArray1();
}

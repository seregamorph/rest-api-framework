package com.seregamorph.restapi.partial;

public interface SimplePartial extends TestIdPartial<Long> {

    @Required
    String getName();

    @Required
    String getTitle();
}

package com.seregamorph.restapi.utils;

@SuppressWarnings("unused")
interface TestParentInterface {

    String getName();

    default String getAlias() {
        return getName();
    }
}

package com.seregamorph.restapi.test.base;

public enum ResourceType {

    /**
     * Existing resource,e.g. retrieved from a GET request.
     */
    EXISTING,
    /**
     * Newly created resource, e.g. result of a POST request.
     */
    CREATED,
    /**
     * Newly updated resource, e.g. result of a PATCH request. May be applicable to PUT also, because IDs for PUT are
     * known upfront.
     */
    UPDATED,
    /**
     * Newly created or updated resource. Applicable in the case business logic is uncertain,
     * e.g. if unique fields exist, update; otherwise, persist.
     */
    SAVED
}

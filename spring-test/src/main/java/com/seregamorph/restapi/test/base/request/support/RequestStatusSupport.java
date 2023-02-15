package com.seregamorph.restapi.test.base.request.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class RequestStatusSupport<P> {

    private final P parent;

    /**
     * True - expect 400 Bad Request. False - expect 200 OK. Notice: Headers / Parameters are not resource identifiers.
     * Whether the values can't be mapped correctly (e.g. data type is Long, but we receive String), or the values
     * can be mapped and are identifiers for related resources, but can't be found in db, we still return HTTP 400
     * Bad Request.
     */
    @Getter
    private boolean badRequest;

    public P setBadRequest(boolean badRequest) {
        this.badRequest = badRequest;
        return this.parent;
    }
}

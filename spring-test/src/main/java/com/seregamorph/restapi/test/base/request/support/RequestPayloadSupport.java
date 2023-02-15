package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.test.base.support.PayloadSupportDelegate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestPayloadSupport<P> {

    private final P parent;

    // The payload can be any object as long as it can be turned into a Json string by invoking Object.toString()
    private Object validPayload;

    public P setValidPayload(Object validPayload) {
        this.validPayload = validPayload;
        return this.parent;
    }

    public Object getValidPayload(PayloadSupportDelegate<?> setup) {
        return validPayload == null ? setup.getDefaultPayload() : validPayload;
    }
}

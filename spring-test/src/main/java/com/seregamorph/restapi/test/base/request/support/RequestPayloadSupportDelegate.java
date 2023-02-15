package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.test.base.support.PayloadSupportDelegate;

public interface RequestPayloadSupportDelegate<P> {

    RequestPayloadSupport<P> getRequestPayloadSupport();

    default P setValidPayload(Object validPayload) {
        return getRequestPayloadSupport().setValidPayload(validPayload);
    }

    default Object getValidPayload(PayloadSupportDelegate<?> setup) {
        return getRequestPayloadSupport().getValidPayload(setup);
    }
}

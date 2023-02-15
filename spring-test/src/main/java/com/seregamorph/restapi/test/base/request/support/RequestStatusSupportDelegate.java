package com.seregamorph.restapi.test.base.request.support;

public interface RequestStatusSupportDelegate<P> {

    RequestStatusSupport<P> getRequestStatusSupport();

    default P setBadRequest(boolean badRequest) {
        return getRequestStatusSupport().setBadRequest(badRequest);
    }

    default boolean isBadRequest() {
        return getRequestStatusSupport().isBadRequest();
    }
}

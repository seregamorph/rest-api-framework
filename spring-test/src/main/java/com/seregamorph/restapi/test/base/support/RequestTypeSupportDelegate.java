package com.seregamorph.restapi.test.base.support;

import javax.annotation.Nonnull;

public interface RequestTypeSupportDelegate<P> {

    RequestTypeSupport<P> getRequestTypeSupport();

    default P setRequestType(@Nonnull RequestType requestType) {
        return getRequestTypeSupport().setRequestType(requestType);
    }

    default RequestType getRequestType() {
        return getRequestTypeSupport().getRequestType();
    }
}

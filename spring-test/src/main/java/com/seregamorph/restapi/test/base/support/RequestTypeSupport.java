package com.seregamorph.restapi.test.base.support;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

@AllArgsConstructor
public class RequestTypeSupport<P> {

    private final P parent;

    /**
     * If {@link null}, we expect HTTP 201 Created with Location header (POST requests only).
     * If {@link RequestType#RETRIEVAL}, we expect HTTP 200 OK.
     * If {@link RequestType#NO_RETRIEVAL}, we expect HTTP 204 No Content.
     */
    @Getter
    private RequestType requestType;

    @SuppressWarnings("UnusedReturnValue")
    public P setRequestType(@Nonnull RequestType requestType) {
        Validate.notNull(requestType, "requestType should not be null");
        this.requestType = requestType;
        return this.parent;
    }
}

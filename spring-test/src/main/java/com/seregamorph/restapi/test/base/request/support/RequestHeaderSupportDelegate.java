package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.test.base.support.HeaderSupportDelegate;
import java.util.Date;
import java.util.Map;

public interface RequestHeaderSupportDelegate<P> {

    RequestHeaderSupport<P> getRequestHeaderSupport();

    default P setHeaders(Map<String, Object> headers) {
        return getRequestHeaderSupport().setHeaders(headers);
    }

    default Map<String, Object> getHeaders(HeaderSupportDelegate<?> setup) {
        return getRequestHeaderSupport().getHeaders(setup);
    }

    default P putHeader(String name, Object value) {
        return getRequestHeaderSupport().putHeader(name, value);
    }

    default P putHeader(String name, Date value, String format) {
        return getRequestHeaderSupport().putHeader(name, value, format);
    }
}

package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.test.base.support.HeaderSupportDelegate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class RequestHeaderSupport<P> {

    private final P parent;

    private Map<String, Object> headers;

    public Map<String, Object> getHeaders(HeaderSupportDelegate<?> setup) {
        return headers == null ? setup.getHeaders() : headers;
    }

    public P putHeader(String name, Object value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(name, value);
        return this.parent;
    }

    public P putHeader(String name, Date value, String format) {
        return this.putHeader(name, new SimpleDateFormat(format).format(value));
    }

    public P setHeaders(Map<String, Object> headers) {
        this.headers = new HashMap<>(headers);
        return this.parent;
    }
}

package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.test.base.support.ParameterSupportDelegate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class RequestParameterSupport<P> {

    private final P parent;

    private Map<String, Object> parameters;

    public P putParameter(String name, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(name, value);
        return this.parent;
    }

    public P putParameter(String name, Date value, String format) {
        return this.putParameter(name, new SimpleDateFormat(format).format(value));
    }

    public P setParameters(Map<String, Object> parameters) {
        this.parameters = new HashMap<>(parameters);
        return this.parent;
    }

    public Map<String, Object> getParameters(ParameterSupportDelegate<?> setup) {
        return parameters == null ? setup.getParameters() : parameters;
    }
}

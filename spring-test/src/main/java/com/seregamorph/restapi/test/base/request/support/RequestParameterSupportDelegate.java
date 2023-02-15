package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.test.base.support.ParameterSupportDelegate;
import java.util.Date;
import java.util.Map;

public interface RequestParameterSupportDelegate<P> {

    RequestParameterSupport<P> getRequestParameterSupport();

    default P setParameters(Map<String, Object> parameters) {
        return getRequestParameterSupport().setParameters(parameters);
    }

    default Map<String, Object> getParameters(ParameterSupportDelegate<?> setup) {
        return getRequestParameterSupport().getParameters(setup);
    }

    default P putParameter(String name, Object value) {
        return getRequestParameterSupport().putParameter(name, value);
    }

    default P putParameter(String name, Date value, String format) {
        return getRequestParameterSupport().putParameter(name, value, format);
    }
}

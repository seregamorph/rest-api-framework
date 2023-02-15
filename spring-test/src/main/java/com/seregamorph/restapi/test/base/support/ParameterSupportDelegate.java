package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.setup.common.VerifiableParameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.test.web.servlet.ResultMatcher;

public interface ParameterSupportDelegate<P> {

    ParameterSupport<P> getParameterSupport();

    default P provideParameter(String name, Object value, ResultMatcher... resultMatchersIfMissing) {
        return provideParameter(name, value, Arrays.asList(resultMatchersIfMissing));
    }

    default P provideParameter(String name, Object value, Collection<ResultMatcher> resultMatchersIfMissing) {
        return getParameterSupport().provideParameter(name, value, resultMatchersIfMissing);
    }

    default P provideParameter(String name, Date value, String format,
                               ResultMatcher... resultMatchersIfMissing) {
        return provideParameter(name, value, format, Arrays.asList(resultMatchersIfMissing));
    }

    default P provideParameter(String name, Date value, String format,
                               Collection<ResultMatcher> resultMatchersIfMissing) {
        return getParameterSupport().provideParameter(name, value, format, resultMatchersIfMissing);
    }

    default P provideParameter(String name, Object value) {
        return getParameterSupport().provideParameter(name, value);
    }

    default P provideParameter(String name, Date value, String format) {
        return getParameterSupport().provideParameter(name, value, format);
    }

    default P addBadParameterValue(String name, Object badValue, ResultMatcher... badValueResultMatchers) {
        return addBadParameterValue(name, badValue, Arrays.asList(badValueResultMatchers));
    }

    default P addBadParameterValue(String name, Object badValue, Collection<ResultMatcher> badValueResultMatchers) {
        return getParameterSupport().addBadParameterValue(name, badValue, badValueResultMatchers);
    }

    default P addBadParameterValue(String name, Date value, String badFormat,
                                   ResultMatcher... badValueResultMatchers) {
        return addBadParameterValue(name, value, badFormat, Arrays.asList(badValueResultMatchers));
    }

    default P addBadParameterValue(String name, Date value, String badFormat,
                                   Collection<ResultMatcher> badValueResultMatchers) {
        return getParameterSupport().addBadParameterValue(name, value, badFormat, badValueResultMatchers);
    }

    default List<VerifiableParameter> getRequiredParameters() {
        return getParameterSupport().getRequiredParameters();
    }

    default List<VerifiableParameter> getBadParameters() {
        return getParameterSupport().getBadParameters();
    }

    default Map<String, Object> getParameters() {
        return getParameterSupport().getParameters();
    }
}

package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.setup.common.VerifiableParameter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.ResultMatcher;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class ParameterSupport<P> {

    // Notice that values for these parameters are result of {@link Object#toString()}.

    private final P parent;

    /**
     * Parameters with valid values. If we need to send valid parameters with our requests, then all of these parameters
     * will be sent. Notice that these parameters may be required (we must provide them, otherwise the server will
     * reject the request with 400 Bad Request) or not required (we don't need to provide them as per the API contract,
     * we intentionally provide them in order to get the desired results).
     */
    @Getter
    private final Map<String, Object> parameters = new HashMap<>();

    /**
     * Parameters which are required by the endpoint. This is a subset of {@link #parameters}, and is only used to
     * verify that a parameter is truly required. E.g. there are 3 required parameters. If we send a request without
     * any one of them, we expect 400 Bad Request. If this map has size = n, then our base tests will perform n requests
     * with each of them containing n - 1 required parameters.
     */
    @Getter
    private final List<VerifiableParameter> requiredParameters = new ArrayList<>();

    /**
     * This is only used to verify that a parameter is 'accepted' by the server. E.g. if a parameter has
     * type = number, and we send a request with that parameter having an alphabetical value, then we expect
     * 400 Bad Request. If matchers are specified, then response body will be validated against the matchers. If
     * this map has size = n, then our base tests will perform n requests with each of them containing 1 bad value.
     * Notice that a parameter may appear several times in the collection.
     */
    @Getter
    private final List<VerifiableParameter> badParameters = new ArrayList<>();

    public P provideParameter(String name, Object value, Collection<ResultMatcher> resultMatchersIfMissing) {
        this.parameters.put(name, value);
        this.requiredParameters.add(new VerifiableParameter(name, value, resultMatchersIfMissing));
        return this.parent;
    }

    public P provideParameter(String name,
                              Date value,
                              String format,
                              Collection<ResultMatcher> resultMatchersIfMissing) {
        return this.provideParameter(name, new SimpleDateFormat(format).format(value), resultMatchersIfMissing);
    }

    public P provideParameter(String name, Object value) {
        this.parameters.put(name, value);
        return this.parent;
    }

    public P provideParameter(String name, Date value, String format) {
        return this.provideParameter(name, new SimpleDateFormat(format).format(value));
    }

    public P addBadParameterValue(String name, Object badValue, Collection<ResultMatcher> badValueResultMatchers) {
        this.badParameters.add(new VerifiableParameter(name, badValue, badValueResultMatchers));
        return this.parent;
    }

    public P addBadParameterValue(String name,
                                  Date value,
                                  String badFormat,
                                  Collection<ResultMatcher> badValueResultMatchers) {
        return this.addBadParameterValue(name, new SimpleDateFormat(badFormat).format(value), badValueResultMatchers);
    }
}

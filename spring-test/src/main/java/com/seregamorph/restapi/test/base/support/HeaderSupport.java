package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.setup.common.VerifiableHeader;
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
public class HeaderSupport<P> {

    // Notice that values for these headers are result of {@link Object#toString()}.

    private final P parent;

    /**
     * Headers with valid values. If we need to send valid headers with our requests, then all of these headers
     * will be sent. Notice that these headers may be required (we must provide them, otherwise the server will
     * reject the request with 400 Bad Request) or not required (we don't need to provide them as per the API contract,
     * we intentionally provide them in order to get the desired results).
     */
    @Getter
    private final Map<String, Object> headers = new HashMap<>();

    /**
     * Headers which are required by the endpoint. This is a subset of {@link #headers}, and is only used to
     * verify that a header is truly required. E.g. there are 3 required headers. If we send a request without
     * any one of them, we expect 400 Bad Request. If this map has size = n, then our base tests will perform n requests
     * with each of them containing n - 1 required headers.
     */
    @Getter
    private final List<VerifiableHeader> requiredHeaders = new ArrayList<>();

    /**
     * This is only used to verify that a header is 'accepted' by the server. E.g. if a header has
     * type = number, and we send a request with that header having an alphabetical value, then we expect
     * 400 Bad Request. If matchers are specified, then response body will be validated against the matchers. If
     * this map has size = n, then our base tests will perform n requests with each of them containing 1 bad value.
     * Notice that a header may appear several times in the collection.
     */
    @Getter
    private final List<VerifiableHeader> badHeaders = new ArrayList<>();

    public P provideHeader(String name, Object value, Collection<ResultMatcher> resultMatchersIfMissing) {
        this.headers.put(name, value);
        this.requiredHeaders.add(new VerifiableHeader(name, value, resultMatchersIfMissing));
        return this.parent;
    }

    public P provideHeader(String name, Date value, String format, Collection<ResultMatcher> resultMatchersIfMissing) {
        return this.provideHeader(name, new SimpleDateFormat(format).format(value), resultMatchersIfMissing);
    }

    public P provideHeader(String name, Object value) {
        this.headers.put(name, value);
        return this.parent;
    }

    public P provideHeader(String name, Date value, String format) {
        return this.provideHeader(name, new SimpleDateFormat(format).format(value));
    }

    public P addBadHeaderValue(String name, Object badValue, Collection<ResultMatcher> badValueResultMatchers) {
        this.badHeaders.add(new VerifiableHeader(name, badValue, badValueResultMatchers));
        return this.parent;
    }

    public P addBadHeaderValue(String name,
                               Date value,
                               String badFormat,
                               Collection<ResultMatcher> badValueResultMatchers) {
        return this.addBadHeaderValue(name, new SimpleDateFormat(badFormat).format(value), badValueResultMatchers);
    }
}

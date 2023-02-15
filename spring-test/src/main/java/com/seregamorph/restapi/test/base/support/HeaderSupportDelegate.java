package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.setup.common.VerifiableHeader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.test.web.servlet.ResultMatcher;

public interface HeaderSupportDelegate<P> {

    HeaderSupport<P> getHeaderSupport();

    default P provideHeader(String name, Object value, ResultMatcher... resultMatchersIfMissing) {
        return provideHeader(name, value, Arrays.asList(resultMatchersIfMissing));
    }

    default P provideHeader(String name, Object value, Collection<ResultMatcher> resultMatchersIfMissing) {
        return getHeaderSupport().provideHeader(name, value, resultMatchersIfMissing);
    }

    default P provideHeader(String name, Date value, String format, ResultMatcher... resultMatchersIfMissing) {
        return provideHeader(name, value, format, Arrays.asList(resultMatchersIfMissing));
    }

    default P provideHeader(String name, Date value, String format, Collection<ResultMatcher> resultMatchersIfMissing) {
        return getHeaderSupport().provideHeader(name, value, format, resultMatchersIfMissing);
    }

    default P provideHeader(String name, Object value) {
        return getHeaderSupport().provideHeader(name, value);
    }

    default P provideHeader(String name, Date value, String format) {
        return getHeaderSupport().provideHeader(name, value, format);
    }

    default P addBadHeaderValue(String name, Object badValue, ResultMatcher... badValueResultMatchers) {
        return addBadHeaderValue(name, badValue, Arrays.asList(badValueResultMatchers));
    }

    default P addBadHeaderValue(String name, Object badValue, Collection<ResultMatcher> badValueResultMatchers) {
        return getHeaderSupport().addBadHeaderValue(name, badValue, badValueResultMatchers);
    }

    default P addBadHeaderValue(String name, Date value, String badFormat,
                                ResultMatcher... badValueResultMatchers) {
        return addBadHeaderValue(name, value, badFormat, Arrays.asList(badValueResultMatchers));
    }

    default P addBadHeaderValue(String name, Date value, String badFormat,
                                Collection<ResultMatcher> badValueResultMatchers) {
        return getHeaderSupport().addBadHeaderValue(name, value, badFormat, badValueResultMatchers);
    }

    default List<VerifiableHeader> getRequiredHeaders() {
        return getHeaderSupport().getRequiredHeaders();
    }

    default List<VerifiableHeader> getBadHeaders() {
        return getHeaderSupport().getBadHeaders();
    }

    default Map<String, Object> getHeaders() {
        return getHeaderSupport().getHeaders();
    }
}

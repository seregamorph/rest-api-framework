package com.seregamorph.restapi.test.interceptors;

import com.seregamorph.restapi.test.filters.SkipTestFilter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.AssumptionViolatedException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Handles HTTP response and re-throws {@link AssumptionViolatedException}.
 *
 * @see SkipTestFilter
 */
@Slf4j
public class SkipTestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] requestBody,
                                        ClientHttpRequestExecution execution) throws IOException {
        val response = execution.execute(request, requestBody);
        val headerSkipTest = response.getHeaders().getFirst(SkipTestFilter.HEADER_TEST_SKIP);
        if (headerSkipTest != null) {
            throw new AssumptionViolatedException(headerSkipTest);
        }
        return response;
    }

}

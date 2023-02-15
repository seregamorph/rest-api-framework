package com.seregamorph.restapi.test.interceptors;

import static com.seregamorph.restapi.test.filters.TestContextFilter.HEADER_TEST_CLASS_NAME;
import static com.seregamorph.restapi.test.filters.TestContextFilter.HEADER_TEST_EXECUTION_ID;
import static com.seregamorph.restapi.test.filters.TestContextFilter.HEADER_TEST_METHOD_GROUP;
import static com.seregamorph.restapi.test.filters.TestContextFilter.HEADER_TEST_METHOD_NAME;
import static com.seregamorph.restapi.test.filters.TestContextFilter.HEADER_TEST_TARGET_METHOD_GROUP;

import com.seregamorph.restapi.test.TestContext;
import java.io.IOException;
import javax.annotation.Nullable;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Provides headers to initialize TestContext in the thread of endpoint execution.
 *
 * @see com.seregamorph.restapi.test.filters.TestContextFilter
 */
public class TestContextInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        val headers = request.getHeaders();
        val testDescription = TestContext.getCurrentTest();
        if (testDescription != null) {
            headers.set(HEADER_TEST_CLASS_NAME, testDescription.getDescription().getClassName());
            headers.set(HEADER_TEST_METHOD_NAME, testDescription.getDescription().getMethodName());
            headers.set(HEADER_TEST_METHOD_GROUP, testDescription.getMethodGroup());
            headers.set(HEADER_TEST_TARGET_METHOD_GROUP, testDescription.getTargetMethodGroup());
            setHeader(headers, HEADER_TEST_EXECUTION_ID, testDescription.getExecutionId());
        }

        return execution.execute(request, body);
    }

    private static void setHeader(HttpHeaders headers, String headerName, @Nullable String headerValue) {
        if (headerValue != null) {
            headers.set(headerName, headerValue);
        }
    }

}

package com.seregamorph.restapi.test.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.AssumptionViolatedException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that adds MockMvc test behaviour:
 * Rethrow AssumptionViolationException
 *
 * @see com.seregamorph.restapi.test.config.SkipTestFilterBootConfig
 * @see com.seregamorph.restapi.test.interceptors.SkipTestInterceptor
 */
public class SkipTestFilter extends OncePerRequestFilter {

    public static final String HEADER_TEST_SKIP = "X-Test-Skip";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(request, response);
        } catch (AssumptionViolatedException e) {
            response.setHeader(HEADER_TEST_SKIP, e.getMessage());
        }
    }

}

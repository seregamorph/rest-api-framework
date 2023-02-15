package com.seregamorph.restapi.test.filters;

import static com.seregamorph.restapi.test.security.TestAuthorityUtils.doWithPermissions;

import com.seregamorph.restapi.test.TestContext;
import com.seregamorph.restapi.test.TestDescription;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.runner.Description;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that adds MockMvc test behaviour:
 * Sets up TestContext current test
 * delegates execution with passed authorities.
 *
 * @see com.seregamorph.restapi.test.config.TestContextFilterBootConfig
 * @see com.seregamorph.restapi.test.interceptors.TestContextInterceptor
 */
@Slf4j
@RequiredArgsConstructor
public class TestContextFilter extends OncePerRequestFilter {

    public static final String HEADER_TEST_CLASS_NAME = "X-Test-ClassName";
    public static final String HEADER_TEST_METHOD_NAME = "X-Test-MethodName";
    public static final String HEADER_TEST_METHOD_GROUP = "X-Test-MethodGroup";
    public static final String HEADER_TEST_TARGET_METHOD_GROUP = "X-Test-TargetMethodGroup";
    public static final String HEADER_TEST_EXECUTION_ID = "X-Test-ExecutionId";
    public static final String HEADER_TEST_AUTHORITIES = "X-Test-Authorities";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        Assert.isNull(TestContext.getCurrentTest(), "TestContext is already initialized");

        val description = Description.createTestDescription(
                request.getHeader(HEADER_TEST_CLASS_NAME),
                request.getHeader(HEADER_TEST_METHOD_NAME)
        );
        TestContext.setCurrentTest(new TestDescription(description)
                .setMethodGroup(request.getHeader(HEADER_TEST_METHOD_GROUP))
                .setTargetMethodGroup(request.getHeader(HEADER_TEST_TARGET_METHOD_GROUP))
                .setExecutionId(request.getHeader(HEADER_TEST_EXECUTION_ID)));

        try {
            val authoritiesHeader = request.getHeader(HEADER_TEST_AUTHORITIES);
            if (authoritiesHeader == null) {
                filterChain.doFilter(request, response);
            } else {
                val authorities = authoritiesHeader.split(",");
                doWithPermissions(() -> filterChain.doFilter(request, response), authorities);
            }
        } catch (Error | RuntimeException | IOException | ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            TestContext.removeCurrentTest();
        }
    }

}

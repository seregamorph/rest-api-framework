package com.seregamorph.restapi.test.base.setup;

import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.request.support.BaseRequest;
import com.seregamorph.restapi.test.base.setup.common.VerifiablePathVariables;
import com.seregamorph.restapi.test.config.spi.AuthKeyProvider;
import com.seregamorph.restapi.test.config.spi.TestFrameworkConfigHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultMatcher;

@SuppressWarnings("unused")
public abstract class BaseSetup<S extends BaseSetup<S, R>, R extends BaseRequest<R>>
        extends AbstractStackTraceHolder {

    @Getter
    private final List<R> extraRequests = new ArrayList<>();

    @Getter
    private final HttpMethod httpMethod;

    @Getter
    private final String pathTemplate;

    private final Object[] pathVariablesOrSuppliers;

    @Getter
    private final String initTestMethodName;

    /**
     * HTTP 400 Bad Request.
     */
    @Getter
    private final List<VerifiablePathVariables> illegalPathVariables = new ArrayList<>();

    /**
     * HTTP 404 Not Found.
     */
    @Getter
    private final List<VerifiablePathVariables> invalidPathVariables = new ArrayList<>();

    @Nullable
    @Getter
    private AuthKeyProvider authKeyProvider = TestFrameworkConfigHolder.getTestFrameworkConfig()
            .getAuthenticationKeyProvider();

    /**
     * ALL authorities will be used for authorized endpoints. That means, if an endpoint accepts EITHER
     * AUTHORITY_1 OR AUTHORITY_2, then you need to specify either of them, but not both, as an argument
     * in the setter. If you wish to also test with additional authorities (like the above case),
     * refer to classes extending from this class.
     * Remember that authorities = null means anonymous, authorities != null means authenticated. If authorities
     * is an empty array, it means user is authenticated but has no authorities.
     * @see com.seregamorph.restapi.test.base.RequestBuilderDelegate
     */
    @Nullable
    @Getter
    private String[] authorities;

    BaseSetup(HttpMethod httpMethod, @Nonnull String pathTemplate, Object... pathVariablesOrSuppliers) {
        this.httpMethod = httpMethod;
        this.pathTemplate = pathTemplate;
        this.pathVariablesOrSuppliers = pathVariablesOrSuppliers;
        initTestMethodName = BaseSetupSupport.getCurrentInitTestMethod().getName();
        if (TestFrameworkConfigHolder.getTestFrameworkConfig().isDefaultAuthenticationRequired()) {
            // If authentication is required, by default, authorities is empty
            // See com.seregamorph.restapi.test.base.RequestBuilderDelegate
            authorities = new String[0];
        }
    }

    abstract ResultType getDefaultResultType();

    public ResultType getResultType() {
        return getDefaultResultType();
    }

    public Object[] getPathVariables() {
        return getPathVariables(true);
    }

    /**
     * Get path variables. If the path variable is a {@link Supplier}, its evaluated value is returned.
     */
    public Object[] getPathVariables(boolean resolveSuppliers) {
        return Stream.of(pathVariablesOrSuppliers)
                .map(variable -> {
                    if (variable instanceof Supplier) {
                        if (resolveSuppliers) {
                            return ((Supplier<?>) variable).get();
                        } else {
                            return "{}";
                        }
                    }
                    return variable;
                })
                .toArray(Object[]::new);
    }

    public S add(R extraRequest) {
        // request merge is called before request execution
        this.extraRequests.add(extraRequest);
        // There is a reason for this workaround update of trace array. Without it the line number is not evaluated
        // correctly in chain calls inside of `initTest` method.
        extraRequest.initTrace();
        return self();
    }

    public S addIllegalPathVariables(Object var1, Object var2, Object var3, Object var4, ResultMatcher... resultMatchers) {
        return addIllegalPathVariables(var1, var2, var3, var4, Arrays.asList(resultMatchers));
    }

    public S addIllegalPathVariables(Object var1, Object var2, Object var3, Object var4, Collection<ResultMatcher> resultMatchers) {
        this.illegalPathVariables.add(new VerifiablePathVariables(new Object[]{var1, var2, var3, var4}, resultMatchers));
        return self();
    }

    public S addIllegalPathVariables(Object var1, Object var2, Object var3, ResultMatcher... resultMatchers) {
        return addIllegalPathVariables(var1, var2, var3, Arrays.asList(resultMatchers));
    }

    public S addIllegalPathVariables(Object var1, Object var2, Object var3, Collection<ResultMatcher> resultMatchers) {
        this.illegalPathVariables.add(new VerifiablePathVariables(new Object[]{var1, var2, var3}, resultMatchers));
        return self();
    }

    public S addIllegalPathVariables(Object var1, Object var2, ResultMatcher... resultMatchers) {
        return addIllegalPathVariables(var1, var2, Arrays.asList(resultMatchers));
    }

    public S addIllegalPathVariables(Object var1, Object var2, Collection<ResultMatcher> resultMatchers) {
        this.illegalPathVariables.add(new VerifiablePathVariables(new Object[]{var1, var2}, resultMatchers));
        return self();
    }

    public S addIllegalPathVariables(Object pathVariable, ResultMatcher... resultMatchers) {
        return addIllegalPathVariables(pathVariable, Arrays.asList(resultMatchers));
    }

    public S addIllegalPathVariables(Object pathVariable, Collection<ResultMatcher> resultMatchers) {
        this.illegalPathVariables.add(new VerifiablePathVariables(new Object[]{pathVariable}, resultMatchers));
        return self();
    }

    public S addInvalidPathVariables(Object var1, Object var2, Object var3, Object var4, ResultMatcher... resultMatchers) {
        return addInvalidPathVariables(var1, var2, var3, var4, Arrays.asList(resultMatchers));
    }

    public S addInvalidPathVariables(Object var1, Object var2, Object var3, Object var4, Collection<ResultMatcher> resultMatchers) {
        this.invalidPathVariables.add(new VerifiablePathVariables(new Object[]{var1, var2, var3, var4}, resultMatchers));
        return self();
    }

    public S addInvalidPathVariables(Object var1, Object var2, Object var3, ResultMatcher... resultMatchers) {
        return addInvalidPathVariables(var1, var2, var3, Arrays.asList(resultMatchers));
    }

    public S addInvalidPathVariables(Object var1, Object var2, Object var3, Collection<ResultMatcher> resultMatchers) {
        this.invalidPathVariables.add(new VerifiablePathVariables(new Object[]{var1, var2, var3}, resultMatchers));
        return self();
    }

    public S addInvalidPathVariables(Object var1, Object var2, ResultMatcher... resultMatchers) {
        return addInvalidPathVariables(var1, var2, Arrays.asList(resultMatchers));
    }

    public S addInvalidPathVariables(Object var1, Object var2, Collection<ResultMatcher> resultMatchers) {
        this.invalidPathVariables.add(new VerifiablePathVariables(new Object[]{var1, var2}, resultMatchers));
        return self();
    }

    public S addInvalidPathVariables(Object pathVariable, ResultMatcher... resultMatchers) {
        return addInvalidPathVariables(pathVariable, Arrays.asList(resultMatchers));
    }

    public S addInvalidPathVariables(Object pathVariable, Collection<ResultMatcher> resultMatchers) {
        this.invalidPathVariables.add(new VerifiablePathVariables(new Object[]{pathVariable}, resultMatchers));
        return self();
    }

    public S setAuthKeyProvider(AuthKeyProvider authKeyProvider) {
        this.authKeyProvider = authKeyProvider;
        return setAuthenticationRequired(true);
    }

    public S setAuthenticationRequired(boolean authenticationRequired) {
        if (!authenticationRequired) {
            this.authorities = null;
        } else if (this.authorities == null) {
            // If authentication is required, by default, authorities is empty
            // See com.seregamorph.restapi.test.base.RequestBuilderDelegate
            this.authorities = new String[0];
        }
        return self();
    }

    public boolean isAuthenticationRequired() {
        return this.authorities != null;
    }

    public boolean hasAuthorities() {
        return ArrayUtils.isNotEmpty(this.authorities);
    }

    public S setAuthorities(String... authorities) {
        this.authorities = authorities;
        return self();
    }

    public S setAuthorities(List<String> authorities) {
        return setAuthorities(authorities.toArray(new String[0]));
    }

    @SuppressWarnings("unchecked")
    private S self() {
        return (S) this;
    }

    @Override
    public String toString() {
        return "Endpoint URL: " + pathTemplate + " " + Arrays.toString(getPathVariables()) + ", "
                + "Authentication Required: " + isAuthenticationRequired() + ", "
                + "Authorities: [" + StringUtils.join(authorities, ", ") + "]";
    }
}

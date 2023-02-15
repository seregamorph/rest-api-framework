package com.seregamorph.restapi.test.config.spi;

import static com.seregamorph.restapi.test.utils.MoreMatchers.matches;
import static java.util.stream.Collectors.joining;

import com.seregamorph.restapi.test.config.ProducesValidator;
import com.seregamorph.restapi.test.config.ResponseStatusValidator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.ResultMatcher;

public interface TestFrameworkConfig {

    default String getDefaultMockUsername() {
        return "default-mock-username";
    }

    default Authentication createAuthentication(String username, @Nullable List<String> permissions) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (permissions != null) {
            for (String permission : permissions) {
                if (StringUtils.isNotBlank(permission)) {
                    authorities.add(new SimpleGrantedAuthority(permission));
                }
            }
        }

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    /**
     * To configure whether we consider that all APIs require authentication by default. You might want to change this
     * to false in the case of public APIs.
     */
    default boolean isDefaultAuthenticationRequired() {
        return true;
    }

    default ResultMatcher getRequiredMatcher(Class<?> resourceClass, String fieldName) {
        throw new UnsupportedOperationException("Please provide ResultMatcher(s) for missing required fields in "
                + getClass().getName() + ".");
    }

    default ResultMatcher getRedundantMatcher(Class<?> resourceClass, String fieldName) {
        throw new UnsupportedOperationException("Please provide ResultMatcher(s) for redundant fields in "
                + getClass().getName() + ".");
    }

    default ResultMatcher getTypeMismatchMatcher(String errorMessage) {
        throw new UnsupportedOperationException("Please provide ResultMatcher(s) for TypeMismatchException in "
                + getClass().getName() + ".");
    }

    default ResultMatcher getNotFoundMatcher(Object[] pathVariables) {
        throw new UnsupportedOperationException("Please provide ResultMatcher(s) for NotFoundException in "
                + getClass().getName() + ".");
    }

    default Matcher<String> notFoundDefaultMessageMatcher(Object[] pathVariables) {
        val ids = Arrays.stream(pathVariables).map(String::valueOf).collect(joining(", "));
        val regex = String.format("^\\w+ \\[%s\\] can't be found\\.$", ids);
        return matches(regex);
    }

    default boolean isDefaultPaginationSupported() {
        return true;
    }

    @Nullable
    default AuthKeyProvider getAuthenticationKeyProvider() {
        return null;
    }

    default ProducesValidator getProducesValidator() {
        return new ProducesValidator();
    }

    default ResponseStatusValidator getResponseStatusValidator() {
        return new ResponseStatusValidator();
    }

}

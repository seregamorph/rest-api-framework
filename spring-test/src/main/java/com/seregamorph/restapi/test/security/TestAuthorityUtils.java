package com.seregamorph.restapi.test.security;

import com.seregamorph.restapi.test.config.spi.TestFrameworkConfigHolder;
import com.seregamorph.restapi.test.utils.ThrowingRunnable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;

@UtilityClass
public class TestAuthorityUtils {

    public static <T> T doWithPermissions(Callable<T> callable, String... permissions) throws Exception {
        return doWithPermissions(callable, permissionsList(permissions));
    }

    public static <T> T doWithPermissions(Callable<T> callable, List<String> permissions) throws Exception {
        val username = TestFrameworkConfigHolder.getTestFrameworkConfig()
                .getDefaultMockUsername();
        return doWithPermissions(username, callable, permissions);
    }

    public static void doWithPermissions(ThrowingRunnable runnable, String... permissions) throws Exception {
        doWithPermissions(runnable, permissionsList(permissions));
    }

    public static void doWithPermissions(ThrowingRunnable runnable, List<String> permissions) throws Exception {
        doWithPermissions(() -> {
            runnable.run();
            return null;
        }, permissions);
    }

    /**
     * Wrap call with custom SecurityContext with required permissions.
     *
     * @param username    Authentication username
     * @param callable    callback to call
     * @param permissions required permissions (authority names)
     * @return callback result
     */
    public static <T> T doWithPermissions(String username, Callable<T> callable, String... permissions) throws Exception {
        return doWithPermissions(username, callable, permissionsList(permissions));
    }

    public static <T> T doWithPermissions(String username, Callable<T> callable, List<String> permissions) throws Exception {
        val originalSecurityContext = TestSecurityContextHolder.getContext();
        try {
            val authentication = TestFrameworkConfigHolder.getTestFrameworkConfig()
                    .createAuthentication(username, permissions);
            val context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            TestSecurityContextHolder.setContext(context);
            return callable.call();
        } finally {
            TestSecurityContextHolder.setContext(originalSecurityContext);
        }
    }

    private static List<String> permissionsList(String[] permissions) {
        return permissions == null ? null : Arrays.asList(permissions);
    }
}

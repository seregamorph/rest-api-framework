package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.ClassPathScanner.scan;

import com.seregamorph.restapi.base.BaseResource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@RequiredArgsConstructor
public abstract class BaseResourcesIT extends AbstractBaseResourceIT {

    private final Set<String> packageNames;

    /**
     * Resolves classes based on this class package.
     */
    protected BaseResourcesIT() {
        packageNames = Collections.singleton(getClass().getPackage().getName());
    }

    protected BaseResourcesIT(String packageName, String... otherPackageNames) {
        packageNames = new LinkedHashSet<>();
        packageNames.add(packageName);
        Collections.addAll(packageNames, otherPackageNames);
    }

    @Test
    public void noProjectionsShouldHitInfiniteRecursionError() throws Exception {
        for (val packageName : packageNames) {
            Set<Class<? extends BaseResource>> resourceClasses = scan(BaseResource.class, packageName);
            Assert.isTrue(!resourceClasses.isEmpty(), "Resource classes not found in package " + packageName);
            for (Class<? extends BaseResource> resourceClass : resourceClasses) {
                log.info("Testing {}...", resourceClass.getName());
                super.noProjectionsShouldHitInfiniteRecursionError(resourceClass);
            }
        }
    }
}

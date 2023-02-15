package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.BaseResource;
import java.util.Collection;
import java.util.List;
import lombok.val;
import org.junit.Test;
import org.springframework.util.Assert;

public abstract class BaseResourcesTest extends BasePayloadsTest {

    protected BaseResourcesTest() {
        super();
    }

    protected BaseResourcesTest(String packageName, String... otherPackageNames) {
        super(packageName, otherPackageNames);
    }

    @Test
    public void validateProjections() {
        for (val clazz : getResourceClasses()) {
            logger.info("Testing {}...", clazz.getName());
            super.validateProjections(clazz);
        }
    }

    @Test
    public void validatePartials() {
        for (val clazz : getResourceClasses()) {
            logger.info("Testing {}...", clazz.getName());
            super.validatePartials(clazz);
        }
    }

    @Override
    void validatePayloadClasses(Collection<Class<? extends BasePayload>> classes, String message) {
        super.validatePayloadClasses(classes, message);
        Assert.isTrue(classes.stream().anyMatch(BaseResource.class::isAssignableFrom), message);
    }

    private List<? extends Class<? extends BaseResource>> getResourceClasses() {
        return getPayloadClasses(BaseResource.class);
    }

}

package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.ClassPathScanner.scan;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.BaseResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.junit.Test;
import org.springframework.util.Assert;

public abstract class BasePayloadsTest extends AbstractBaseResourceTest {

    private final Set<Class<? extends BasePayload>> payloadClasses = new LinkedHashSet<>();

    protected BasePayloadsTest() {
        init(getClass().getPackage().getName());
    }

    protected BasePayloadsTest(String packageName, String... otherPackageNames) {
        init(packageName, otherPackageNames);
    }

    private void init(String packageName, String... otherPackageNames) {
        val packageNames = new ArrayList<String>();
        packageNames.add(packageName);
        Collections.addAll(packageNames, otherPackageNames);

        for (val pkg : packageNames) {
            val set = scan(BasePayload.class, pkg);
            validatePayloadClasses(set, "No matching classes found in " + pkg);
            payloadClasses.addAll(set);
        }
    }

    @Test
    public void validatePOJOStructure() {
        for (val clazz : payloadClasses) {
            logger.info("Testing {}...", clazz.getName());
            validatePOJOStructure(clazz);
        }
    }

    @Test
    public void validateToString() throws Exception {
        for (val clazz : payloadClasses) {
            logger.info("Testing {}...", clazz.getName());
            super.validateToString(clazz);
        }
    }

    @Test
    public void validateEqualsAndHashCodeDefaultInstance() throws Exception {
        for (val clazz : payloadClasses) {
            logger.info("Testing {}...", clazz.getName());
            super.validateEqualsAndHashCodeDefaultInstance(clazz);
        }
    }

    @Test
    public void validateEqualsAndHashCodeSameRandomInstance() {
        for (val clazz : payloadClasses) {
            logger.info("Testing {}...", clazz.getName());
            super.validateEqualsAndHashCodeSameRandomInstance(clazz);
        }
    }

    @Test
    public void validateNotEqualsAndHashCodeDifferentRandomInstances() {
        for (val clazz : payloadClasses) {
            logger.info("Testing {}...", clazz.getName());
            super.validateNotEqualsAndHashCodeDifferentRandomInstances(clazz);
        }
    }

    @Test
    public void validateAccept() {
        for (val clazz : payloadClasses) {
            logger.info("Testing {}...", clazz.getName());
            validateAccept(clazz);
        }
    }

    @Test
    public void validatePayloadFields() {
        for (val clazz : payloadClasses) {
            logger.info("Testing {}...", clazz.getName());
            super.validatePayloadFields(clazz);
        }
    }

    void validatePayloadClasses(Collection<Class<? extends BasePayload>> classes, String message) {
        Assert.isTrue(!classes.isEmpty(), message);
    }

    <T extends BasePayload> List<Class<? extends T>> getPayloadClasses(Class<T> requiredSuperType) {
        val result = new ArrayList<Class<? extends T>>();
        payloadClasses.forEach(clazz -> {
            if (BaseResource.class.isAssignableFrom(clazz)) {
                result.add(clazz.asSubclass(requiredSuperType));
            }
        });
        return result;
    }

}

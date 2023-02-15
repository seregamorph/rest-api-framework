package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.base.BasePayload;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

@RequiredArgsConstructor
public abstract class BasePayloadTest extends AbstractBaseResourceTest {

    protected final Class<? extends BasePayload> clazz;

    @Test
    public void testClassShouldHaveRightName() {
        super.testClassShouldHaveRightName(clazz);
    }

    @Test
    public void validatePOJOStructure() {
        validatePOJOStructure(clazz);
    }

    @Test
    public void validateToString() throws Exception {
        super.validateToString(clazz);
    }

    @Test
    public void validateEqualsAndHashCodeDefaultInstance() throws Exception {
        super.validateEqualsAndHashCodeDefaultInstance(clazz);
    }

    @Test
    public void validateEqualsAndHashCodeSameRandomInstance() {
        super.validateEqualsAndHashCodeSameRandomInstance(clazz);
    }

    @Test
    public void validateNotEqualsAndHashCodeDifferentRandomInstances() {
        super.validateNotEqualsAndHashCodeDifferentRandomInstances(clazz);
    }

    @Test
    public void validateAccept() {
        validateAccept(clazz);
    }

    @Test
    public void validatePayloadFields() {
        super.validatePayloadFields(clazz);
    }

}

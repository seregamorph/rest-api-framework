package com.seregamorph.restapi.test.base;

import org.junit.Test;

/**
 * This base test validates getters, setters and other rules over POJOs.
 */
public abstract class BasePOJOTest extends AbstractBasePOJOTest {

    protected final Class<?> clazz;

    /**
     * @param clazz The class to validate
     */
    protected BasePOJOTest(Class<?> clazz) {
        this.clazz = clazz;
    }

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
}

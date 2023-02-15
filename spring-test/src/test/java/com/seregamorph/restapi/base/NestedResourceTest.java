package com.seregamorph.restapi.base;

import com.seregamorph.restapi.test.base.BaseResourceTest;

public class NestedResourceTest extends BaseResourceTest {

    public NestedResourceTest() {
        super(NestedResource.class);
    }

    @Override
    public void validateToString() throws Exception {
        expectedException.expect(StackOverflowError.class);

        super.validateToString();
    }

    @Override
    public void validateEqualsAndHashCodeSameRandomInstance() {
        expectedException.expect(StackOverflowError.class);

        super.validateEqualsAndHashCodeSameRandomInstance();
    }

    @Override
    public void validateNotEqualsAndHashCodeDifferentRandomInstances() {
        expectedException.expect(StackOverflowError.class);

        super.validateNotEqualsAndHashCodeDifferentRandomInstances();
    }
}

package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.base.BaseResource;
import org.junit.Test;

public abstract class BaseResourceTest extends BasePayloadTest {

    /**
     * @param clazz The class to validate
     */
    protected BaseResourceTest(Class<? extends BaseResource> clazz) {
        super(clazz);
    }

    @Test
    public void validateProjections() {
        super.validateProjections(getResourceClass());
    }

    @Test
    public void validatePartials() {
        super.validatePartials(getResourceClass());
    }

    private Class<? extends BaseResource> getResourceClass() {
        return clazz.asSubclass(BaseResource.class);
    }

}

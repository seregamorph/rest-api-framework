package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.base.BaseResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseResourceIT extends AbstractBaseResourceIT {

    protected final Class<? extends BaseResource> resourceClass;

    protected BaseResourceIT(Class<? extends BaseResource> resourceClass) {
        this.resourceClass = resourceClass;
    }

    @Test
    public void noProjectionsShouldHitInfiniteRecursionError() throws Exception {
        super.noProjectionsShouldHitInfiniteRecursionError(resourceClass);
    }
}

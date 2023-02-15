package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.AbstractBaseSpringWebIT;

@FunctionalInterface
public interface AroundRequestAction<T extends AbstractBaseSpringWebIT> {

    void perform(T test) throws Exception;
}

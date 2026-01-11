package com.seregamorph.restapi.demo.controllers;

import com.seregamorph.restapi.test.base.AbstractBaseSpringBootWebIT;
import com.seregamorph.restapi.test.config.EmptyTransactionManagerConfig;
import org.springframework.context.annotation.Import;

@Import(EmptyTransactionManagerConfig.class)
public abstract class AbstractBaseWebIT extends AbstractBaseSpringBootWebIT {

}

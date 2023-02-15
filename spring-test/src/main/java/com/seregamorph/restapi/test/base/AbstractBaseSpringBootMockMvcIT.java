package com.seregamorph.restapi.test.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@MockWebIT
public abstract class AbstractBaseSpringBootMockMvcIT extends AbstractBaseSpringBootWebIT {

    @Autowired
    protected MockMvc mockMvc;

}

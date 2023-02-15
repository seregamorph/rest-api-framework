package com.seregamorph.restapi.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebAppConfiguration
public abstract class AbstractSpringMockMvcIT extends AbstractSpringWebIT {

    @Autowired
    protected MockMvc mockMvc;

}

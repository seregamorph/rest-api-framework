package com.seregamorph.restapi.demo.controllers;

import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;

@InitTest(PageController.class)
public abstract class PageControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static GetAllSetup getAllSetup() {
        return new GetAllSetup()
                .setPaginationSupported(true)
                .setTotalElements(4);
    }

}

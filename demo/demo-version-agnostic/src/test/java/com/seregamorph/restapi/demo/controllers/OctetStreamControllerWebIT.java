package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.controllers.OctetStreamController.ENDPOINT_ZIP;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;

@InitTest(OctetStreamController.class)
public abstract class OctetStreamControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static GetOneSetup getOneSetup() {
        return new GetOneSetup(ENDPOINT_ZIP)
                // repeatable is true by default, emphasizing to focus main
                // test purpose (getOneShouldRepeatedManageProvidedHeadersAndParameters)
                .setRepeatable(true)
                .setDefaultResultMatchers(content().contentType(APPLICATION_OCTET_STREAM));
    }

}

package com.seregamorph.restapi.demo.controllers;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//@Api(tags = "matcher")
@RestController
@RequestMapping(path = ExceptionController.ENDPOINT, produces = APPLICATION_JSON_VALUE)
public class ExceptionController extends AbstractBaseRestController {

    static final String ENDPOINT = "/api/exceptions";
    static final String ENDPOINT_MISSING_SERVLET_REQUEST_PART = "/missing-servlet-request-part";

    static final String PARAM_DATA = "data";

//    @ApiOperation("MissingServletRequestPartException")
    @PostMapping(ENDPOINT_MISSING_SERVLET_REQUEST_PART)
    @ResponseStatus(NO_CONTENT)
    public void multipart(
            @RequestParam(PARAM_DATA)
            final MultipartFile fileContent
    ) {
        // No-op
    }
}

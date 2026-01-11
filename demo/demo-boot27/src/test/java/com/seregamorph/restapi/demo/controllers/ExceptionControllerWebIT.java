package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.controllers.ExceptionController.ENDPOINT;
import static com.seregamorph.restapi.demo.controllers.ExceptionController.ENDPOINT_MISSING_SERVLET_REQUEST_PART;
import static com.seregamorph.restapi.demo.controllers.ExceptionController.PARAM_DATA;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.seregamorph.restapi.errors.ErrorResponse;
import com.seregamorph.restapi.test.base.InitTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

@InitTest(ExceptionController.class)
public abstract class ExceptionControllerWebIT extends AbstractBaseWebIT {

    @Test
    public void missingServletRequestPartExceptionShouldReturn400() throws Exception {
        rest
                .perform(multipart(ENDPOINT + ENDPOINT_MISSING_SERVLET_REQUEST_PART))
                .andExpect(status().isBadRequest())
                .andExpect(SINGLE.matcherOf(missingServletRequestPartResponseMatcher()));
    }

    @Test
    public void noMissingServletRequestPartExceptionShouldSucceed() throws Exception {
        rest
                .perform(multipart(ENDPOINT + ENDPOINT_MISSING_SERVLET_REQUEST_PART)
                        .file(new MockMultipartFile(PARAM_DATA, "test".getBytes(UTF_8))))
                .andExpect(status().isNoContent());
    }

    private static ErrorResponse missingServletRequestPartResponseMatcher() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Required request part '%s' is not present", PARAM_DATA));
        return jsonMatching(ErrorResponse.class)
                .setCode(errorResponse.getCode())
                .setMessage(errorResponse.getMessage());
    }
}

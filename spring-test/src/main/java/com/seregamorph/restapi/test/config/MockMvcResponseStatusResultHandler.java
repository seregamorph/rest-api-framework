package com.seregamorph.restapi.test.config;

import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpStatus.OK;

import com.seregamorph.restapi.test.config.spi.TestFrameworkConfigHolder;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.method.HandlerMethod;

class MockMvcResponseStatusResultHandler implements ResultHandler {

    @Override
    public void handle(MvcResult result) throws Exception {
        val httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());
        if (httpStatus.is2xxSuccessful() && httpStatus != OK) {
            val handlerMethod = (HandlerMethod) result.getHandler();
            assertNotNull("MvcResult.handler is null", handlerMethod);
            val responseStatusValidator = TestFrameworkConfigHolder.getTestFrameworkConfig()
                    .getResponseStatusValidator();
            if (responseStatusValidator.shouldValidate(handlerMethod)) {
                responseStatusValidator.validate(handlerMethod, httpStatus);
            }
        }
    }

}

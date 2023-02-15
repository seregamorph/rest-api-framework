package com.seregamorph.restapi.test.config;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.seregamorph.restapi.test.config.spi.TestFrameworkConfigHolder;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.method.HandlerMethod;

class MockMvcProducesResultHandler implements ResultHandler {

    @Override
    public void handle(MvcResult result) throws Exception {
        val contentType = result.getResponse().getHeader(CONTENT_TYPE);
        if (contentType == null) {
            if (result.getResponse().getContentLength() == 0) {
                return;
            } else {
                throw new AssertionError("Missing Content-Type header in non-empty response");
            }
        }
        val handlerMethod = (HandlerMethod) result.getHandler();
        if (handlerMethod != null) {
            val responseContentType = MediaType.parseMediaType(contentType);
            val producesValidator = TestFrameworkConfigHolder.getTestFrameworkConfig()
                    .getProducesValidator();
            if (producesValidator.shouldValidate(handlerMethod)) {
                producesValidator.validate(handlerMethod,
                        HttpStatus.valueOf(result.getResponse().getStatus()), responseContentType);
            }
        }
    }

}

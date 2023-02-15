package com.seregamorph.restapi.test.config;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

class MockMvcResponseContentResultHandler implements ResultHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(MvcResult result) throws Exception {
        val contentType = result.getResponse().getContentType();
        if (contentType != null) {
            val contentMediaType = MediaType.parseMediaType(contentType);
            if (APPLICATION_JSON.includes(contentMediaType)) {
                val content = result.getResponse().getContentAsString();
                try {
                    objectMapper.readTree(content);
                } catch (IOException e) {
                    throw new AssertionError("Content-Type is \"" + contentType + "\", "
                            + "but the content is not parsable", e);
                }
            }
        }

        if (result.getResolvedException() instanceof HttpMessageNotWritableException) {
            throw new AssertionError("Failed to serialize response", result.getResolvedException());
        }
    }

}

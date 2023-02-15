package com.seregamorph.restapi.test.mocks.http;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.springframework.http.HttpStatus;

public class HttpStatusDeserializer extends JsonDeserializer<HttpStatus> {

    @Override
    public HttpStatus deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        try {
            return HttpStatus.valueOf(jp.getIntValue());
        } catch (Exception e) {
            // We intentionally catch all exceptions here as all of them mean invalid HTTP status
            throw new IllegalArgumentException("Invalid HTTP status: " + jp.getText());
        }
    }
}

package com.seregamorph.restapi.test.mocks.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class MockHttpResponse {

    @JsonDeserialize(using = HttpStatusDeserializer.class)
    private HttpStatus status = HttpStatus.OK;

    private Map<String, List<String>> headers = new HashMap<>();

    private JsonNode body;
}

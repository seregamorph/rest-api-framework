package com.seregamorph.restapi.test.mocks.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.test.mocks.MockJsonDataStore;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

@Slf4j
public class MockHttpDataStore extends MockJsonDataStore<MockHttpResponseSetup, MockHttpResponse> {

    private final ObjectMapper objectMapper;

    public MockHttpDataStore(String resourcePath) {
        this(new ObjectMapper(), resourcePath);
    }

    public MockHttpDataStore(ObjectMapper objectMapper, String resourcePath) {
        super(MockHttpResponseSetup.class, resourcePath);
        this.objectMapper = objectMapper;
    }

    public <T> Optional<ResponseEntity<T>> getResponse(Class<T> responseType) {
        return findJsonResponse()
                .map(mockHttpResponse -> {
                    val headers = new LinkedMultiValueMap<String, String>(mockHttpResponse.getHeaders());

                    if ((mockHttpResponse.getStatus() == HttpStatus.NO_CONTENT && mockHttpResponse.getBody() == null)
                            || (!mockHttpResponse.getStatus().is2xxSuccessful() && responseType != String.class)) {
                        // No translation of response body
                        log.info("Mock response HTTP {} with headers {}", mockHttpResponse.getStatus(), headers);
                        return new ResponseEntity<>(headers, mockHttpResponse.getStatus());
                    }

                    T body = getBody(mockHttpResponse.getBody(), responseType);

                    log.info("Mock response HTTP {} with headers {} and body {}",
                            mockHttpResponse.getStatus(), headers, mockHttpResponse.getBody());
                    return new ResponseEntity<>(body, headers, mockHttpResponse.getStatus());
                });
    }

    @Override
    protected void validate(MockHttpResponse response) {
        if (response.getStatus() == HttpStatus.NO_CONTENT && response.getBody() != null) {
            throw new IllegalArgumentException("If status is 204, response body should be empty.");
        }
    }

    private <T> T getBody(JsonNode rawBody, Class<T> responseType) {
        if (responseType == String.class) {
            return responseType.cast(rawBody.toString());
        } else {
            try {
                return objectMapper.readerFor(responseType)
                        .readValue(rawBody);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to deserialize json to " + responseType.getName(), e);
            }
        }
    }

}

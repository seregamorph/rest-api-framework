package com.seregamorph.restapi.test.mocks;

import com.fasterxml.jackson.databind.JsonNode;

public class MockJsonNodeDataStore extends MockJsonDataStore<MockJsonNodeResponseSetup, JsonNode> {

    public MockJsonNodeDataStore(String resourcePath) {
        super(MockJsonNodeResponseSetup.class, resourcePath);
    }

}

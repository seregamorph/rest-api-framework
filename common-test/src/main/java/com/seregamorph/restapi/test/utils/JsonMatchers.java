package com.seregamorph.restapi.test.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

@UtilityClass
public class JsonMatchers {

    public static Matcher<JsonNode> jsonNodeEquals(String expectedString) {
        val mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        return new TypeSafeDiagnosingMatcher<JsonNode>() {

            @Override
            @SneakyThrows
            protected boolean matchesSafely(JsonNode actual, Description mismatchDescription) {
                val parsedExpected = mapper.readTree(expectedString);
                val marshalledActual = mapper.writeValueAsString(actual);
                val parsedActual = mapper.readTree(marshalledActual);
                if (!parsedExpected.equals(parsedActual)) {
                    mismatchDescription.appendText("was " + marshalledActual);
                    return false;
                }
                return true;
            }

            @Override
            @SneakyThrows
            public void describeTo(Description description) {
                description.appendText("to equal " + mapper.writeValueAsString(mapper.readTree(expectedString)));
            }
        };
    }
}

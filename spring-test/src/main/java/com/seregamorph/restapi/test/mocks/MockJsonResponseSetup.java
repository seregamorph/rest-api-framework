package com.seregamorph.restapi.test.mocks;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@SuppressWarnings("WeakerAccess")
public class MockJsonResponseSetup<T> {

    /**
     * Any description for the setup. The purpose is like normal comments in Java, just that it has the same format
     * as a normal json property. We won't process its value. Notice that JSON standard does not support comments.
     */
    private String description;

    @NotNull
    private List<MockJsonMatcher> matchers;

    private T response;
}

package com.seregamorph.restapi.test.base.support;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RepeatedCheckSupport<P> {

    @Getter
    private final List<String> ignoredJsonPaths = new ArrayList<>();

    private final P parent;

    @Getter
    private boolean repeatable = true;

    /**
     * json path to implicitly updated fields, that should be ignored on repeated requests
     */
    public P addAutoUpdateJsonPath(String jsonPath) {
        ignoredJsonPaths.add(jsonPath);
        return parent;
    }

    /**
     * If set to false, repeatable GET request validation is omitted.
     */
    public P setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
        return parent;
    }

}

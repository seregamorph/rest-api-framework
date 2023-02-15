package com.seregamorph.restapi.test.base.support;

import java.util.List;

public interface RepeatedCheckSupportDelegate<P> {

    RepeatedCheckSupport<P> getRepeatedCheckSupport();

    default P addAutoUpdateJsonPath(String jsonPath) {
        return getRepeatedCheckSupport().addAutoUpdateJsonPath(jsonPath);
    }

    default List<String> getIgnoredJsonPaths() {
        return getRepeatedCheckSupport().getIgnoredJsonPaths();
    }

    default P setRepeatable(boolean repeatable) {
        return getRepeatedCheckSupport().setRepeatable(repeatable);
    }

    default boolean isRepeatable() {
        return getRepeatedCheckSupport().isRepeatable();
    }
}

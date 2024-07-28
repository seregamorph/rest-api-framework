package com.seregamorph.restapi.test.utils;

import com.seregamorph.restapi.utils.RelaxedObjects;
import lombok.experimental.UtilityClass;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

@UtilityClass
public class RelaxedMatchers {

    public static Matcher<Object> hasRelaxedValue(Object expected) {
        return new TypeSafeMatcher<Object>() {

            @Override
            protected boolean matchesSafely(Object item) {
                return RelaxedObjects.equals(item, expected);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("<" + expected + ">");
            }
        };
    }
}

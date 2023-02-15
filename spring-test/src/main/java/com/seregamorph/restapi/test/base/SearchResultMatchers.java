package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.search.SearchMatcherUtils;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.utils.ObjectUtils;
import java.util.Collection;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

@UtilityClass
class SearchResultMatchers {

    // If an API supports searching, it should return an array of resources (in the form of a list or a page).
    // This means:
    // - We do not accept the case where the result is not an array
    // - We do not accept the case where the result is an empty array (because it means no match!)
    // - The expected search value is applicable to each and every single element in the array
    // - In the case each element in the array is another array, then the expected search result is still applicable
    // to that sub-array as a whole
    // - In the case of custom matchers (Hamcrest matcher or ResultMatcher), then it's applicable to the entire array

    static Matcher<Object> matches(Matcher<?> valueMatcher) {
        return new SearchResultMatcher() {

            @Override
            protected boolean matches(Collection<?> collection, Description mismatchDescription) {
                return valueMatcher.matches(collection);
            }

            @Override
            public void describeTo(Description description) {
                valueMatcher.describeTo(description);
            }
        };
    }

    static Matcher<Object> eachMatches(SearchOperator searchOperator, Object searchValue) {
        return new SearchResultMatcher() {
            @Override
            public boolean matches(Collection<?> collection, Description mismatchDescription) {
                return eachMatches(collection, searchOperator, searchValue);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("to match " + searchValue);
            }
        };
    }

    // Note: We don't use TypeSafeDiagnosingMatcher because (1) we are dealing with plain objects
    // (2) we want to handle null, and TypeSafeDiagnosingMatcher won't be executed if the term is null
    static abstract class SearchResultMatcher extends BaseMatcher<Object> {

        protected abstract boolean matches(Collection<?> collection, Description mismatchDescription);

        private boolean matches(Object value, Description mismatchDescription) {
            // Non collection values (including arrays) are rejected! This matcher is dedicated to search APIs only,
            // and we don't need to support those values.
            if (!(value instanceof Collection)) {
                mismatchDescription.appendText("Found a non collection value: " + value);
                return false;
            }

            Collection<?> collection = ObjectUtils.collection(value);

            if (CollectionUtils.isEmpty(collection)) {
                mismatchDescription.appendText("Found an empty collection: " + value);
                return false;
            }

            return matches(collection, new StringDescription());
        }

        @Override
        public boolean matches(Object value) {
            return matches(value, new StringDescription());
        }

        @Override
        public final void describeMismatch(Object item, Description mismatchDescription) {
            matches(item, mismatchDescription);
        }
    }

    private static boolean eachMatches(Collection<?> collectionValue, SearchOperator searchOperator, Object searchValue) {
        for (Object elementValue : collectionValue) {
            if (!SearchMatcherUtils.matches(elementValue, searchOperator, searchValue)) {
                return false;
            }
        }

        return true;
    }
}

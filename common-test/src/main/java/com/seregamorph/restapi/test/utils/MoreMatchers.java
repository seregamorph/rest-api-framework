package com.seregamorph.restapi.test.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.seregamorph.restapi.utils.RelaxedObjects;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class MoreMatchers {

    public static <U, V> Matcher<U> where(ThrowingFunction<U, V> extractor, Matcher<V> matcher) {
        return new TypeSafeMatcher<U>() {
            @Override
            protected boolean matchesSafely(U item) {
                if (item == null) {
                    return false;
                }
                V target;
                try {
                    target = extractor.apply(item);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
                return matcher.matches(target);
            }

            @Override
            protected void describeMismatchSafely(U item, Description mismatchDescription) {
                V target;
                try {
                    target = item == null ? null : extractor.apply(item);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
                matcher.describeMismatch(target, mismatchDescription);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Object that matches ")
                        .appendDescriptionOf(matcher);

                val methodReference = TestLambdaUtils.unreferenceLambdaMethod(extractor);
                if (methodReference == null) {
                    description.appendText(" after being extracted");
                } else {
                    val shortDescription = TestLambdaUtils.getMethodShortReference(methodReference);
                    description.appendText(" after call " + shortDescription);
                }
            }
        };
    }

    /**
     * Matcher, that fails with expectedDescription diagnostics in case when predicate returns false.
     * Note: predicate should be only checked on non-null items.
     *
     * @param predicate           predicate to check
     * @param expectedDescription diagnostic message (Expected)
     * @return matcher
     */
    public static <T> Matcher<T> predicate(Predicate<T> predicate, String expectedDescription) {
        return new TypeSafeMatcher<T>() {

            @Override
            protected boolean matchesSafely(T item) {
                return predicate.test(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedDescription);
            }
        };
    }

    /**
     * Checks that collection is ordered by comparator. Does not allow equal (by compare) elements.
     *
     * @param comparator comparator to use
     * @return matcher
     */
    public static <T> Matcher<Collection<? extends T>> strictOrdered(Comparator<? super T> comparator,
                                                                     String comparatorDescription) {
        return ordered(comparator, false, comparatorDescription);
    }

    /**
     * Checks that collection is ordered by comparator. Does not allow equal (by compare) elements.
     *
     * @param comparator comparator to use
     * @return matcher
     */
    public static <T> Matcher<Collection<? extends T>> strictOrdered(Comparator<? super T> comparator) {
        return strictOrdered(comparator, null);
    }

    /**
     * Checks that collection is ordered by comparator. Allows equal (by compare) elements.
     *
     * @param comparator comparator to use
     * @return matcher
     */
    public static <T> Matcher<Collection<? extends T>> softOrdered(Comparator<? super T> comparator,
                                                                   String comparatorDescription) {
        return ordered(comparator, true, comparatorDescription);
    }

    /**
     * Checks that collection is ordered by comparator. Allows equal (by compare) elements.
     *
     * @param comparator comparator to use
     * @return matcher
     */
    public static <T> Matcher<Collection<? extends T>> softOrdered(Comparator<? super T> comparator) {
        return softOrdered(comparator, null);
    }

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

    public static Matcher<String> matches(Pattern pattern) {
        return new TypeSafeDiagnosingMatcher<String>() {

            @Override
            protected boolean matchesSafely(String value, Description mismatchDescription) {
                mismatchDescription.appendText("actual was `" + value + "`");
                val matcher = pattern.matcher(value);
                return matcher.matches();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("to match " + pattern);
            }
        };
    }

    public static Matcher<String> matches(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return matches(pattern);
    }

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

    private static <T> Matcher<Collection<? extends T>> ordered(Comparator<? super T> comparator, boolean allowEqual,
                                                                @Nullable String comparatorDescription) {
        return new TypeSafeDiagnosingMatcher<Collection<? extends T>>() {

            @Override
            protected boolean matchesSafely(Collection<? extends T> item, Description mismatchDescription) {
                Iterator<? extends T> iterator = item.iterator();
                if (!iterator.hasNext()) {
                    return true;
                }

                T first = iterator.next();
                while (iterator.hasNext()) {
                    T next = iterator.next();
                    int result = comparator.compare(first, next);
                    if (result == 0 && !allowEqual) {
                        mismatchDescription.appendText("Found equal elements " + first + " and " + next);
                        return false;
                    } else if (result > 0) {
                        mismatchDescription.appendText("Found unordered elements " + first + " and " + next);
                        return false;
                    }
                    first = next;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText((allowEqual ? "Softly" : "Strictly") + " ordered by comparator"
                        + (comparatorDescription == null ? "" : " " + comparatorDescription));
            }
        };
    }
}

package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.search.SearchOperator.CONTAINS;
import static com.seregamorph.restapi.search.SearchOperator.EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.NOT_CONTAINS;
import static com.seregamorph.restapi.search.SearchOperator.NOT_EQUAL;
import static com.seregamorph.restapi.test.base.SearchResultMatchers.eachMatches;
import static com.seregamorph.restapi.test.base.SearchResultMatchers.matches;
import static com.seregamorph.restapi.test.utils.MoreMatchers.hasRelaxedValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class SearchResultMatchersTest extends AbstractUnitTest {

    private static final String STRING_1 = "abcd";
    private static final String STRING_2 = "efghij";
    private static final String STRING_3 = "klmnopq";
    private static final String STRING_4 = "rstuvxyz";
    private static final int INT_1 = 1;
    private static final int INT_2 = 2;
    private static final int INT_3 = 3;
    private static final int INT_4 = 4;
    private static final long LONG_1 = 1L;
    private static final long LONG_2 = 2L;
    private static final long LONG_3 = 3L;
    private static final long LONG_4 = 4L;
    private static final String OFFSET_DATE_TIME_STRING_1 = "2020-05-27T14:41:18+07:00";
    private static final String OFFSET_DATE_TIME_STRING_2 = "2020-05-27T14:42:18+07:00";
    private static final String OFFSET_DATE_TIME_STRING_3 = "2020-05-27T14:43:18+07:00";
    private static final OffsetDateTime OFFSET_DATE_TIME_1 = OffsetDateTime.parse("2020-05-27T07:41:18Z");
    private static final OffsetDateTime OFFSET_DATE_TIME_2 = OffsetDateTime.parse("2020-05-27T07:42:18Z");
    private static final OffsetDateTime OFFSET_DATE_TIME_3 = OffsetDateTime.parse("2020-05-27T07:43:18Z");
    private static final OffsetDateTime OFFSET_DATE_TIME_4 = OffsetDateTime.parse("2020-05-27T07:44:18Z");

    @Test
    public void matchesShouldRejectNonCollectionValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found a non collection value: 1");

        collector.checkThat(INT_1, matches(equalTo(INT_1)));
    }

    @Test
    public void matchesShouldRejectEmptyCollectionValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found an empty collection: []");

        collector.checkThat(Collections.emptyList(), matches(equalTo(Collections.emptyList())));
    }

    @Test
    public void matchesShouldMatchCollectionValues() {
        collector.checkThat(Collections.singleton(STRING_1), matches(hasItem(STRING_1)));
        collector.checkThat(Collections.singleton(OFFSET_DATE_TIME_STRING_1),
                matches(hasItem(hasRelaxedValue(OFFSET_DATE_TIME_1))));
        collector.checkThat(Collections.singleton(INT_1), matches(hasItem(INT_1)));
        collector.checkThat(Collections.singleton(LONG_1), matches(hasItem(hasRelaxedValue(INT_1))));
        collector.checkThat(Collections.singleton(INT_1), matches(hasItem(hasRelaxedValue(LONG_1))));

        collector.checkThat(Collections.singleton(STRING_1), matches(not(hasItem(STRING_2))));
        collector.checkThat(Collections.singleton(OFFSET_DATE_TIME_STRING_1),
                matches(not(hasItem(hasRelaxedValue(OFFSET_DATE_TIME_2)))));
        collector.checkThat(Collections.singleton(INT_1), matches(not(hasItem(INT_2))));
        collector.checkThat(Collections.singleton(LONG_1), matches(not(hasItem(INT_2))));
        collector.checkThat(Collections.singleton(INT_1), matches(not(hasItem(LONG_2))));

        collector.checkThat(Arrays.asList(STRING_1, STRING_2, STRING_3), matches(hasItem(STRING_3)));
        collector.checkThat(
                Arrays.asList(OFFSET_DATE_TIME_STRING_1, OFFSET_DATE_TIME_STRING_2, OFFSET_DATE_TIME_STRING_3),
                matches(hasItem(hasRelaxedValue(OFFSET_DATE_TIME_3))));
        collector.checkThat(Arrays.asList(INT_1, INT_2, INT_3), matches(hasItem(INT_3)));
        collector.checkThat(Arrays.asList(LONG_1, LONG_2, LONG_3), matches(hasItem(hasRelaxedValue(INT_3))));
        collector.checkThat(Arrays.asList(INT_1, INT_2, INT_3), matches(hasItem(hasRelaxedValue(LONG_3))));

        collector.checkThat(Arrays.asList(STRING_1, STRING_2, STRING_3), matches(not(hasItem(STRING_4))));
        collector.checkThat(
                Arrays.asList(OFFSET_DATE_TIME_STRING_1, OFFSET_DATE_TIME_STRING_2, OFFSET_DATE_TIME_STRING_3),
                matches(not(hasItem(hasRelaxedValue(OFFSET_DATE_TIME_4)))));
        collector.checkThat(Arrays.asList(INT_1, INT_2, INT_3), matches(not(hasItem(INT_4))));
        collector.checkThat(Arrays.asList(LONG_1, LONG_2, LONG_3), matches(not(hasItem(INT_4))));
        collector.checkThat(Arrays.asList(INT_1, INT_2, INT_3), matches(not(hasItem(LONG_4))));
    }

    @Test
    public void eachMatchesShouldRejectNonCollectionValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found a non collection value: 1");

        collector.checkThat(INT_1, eachMatches(EQUAL, INT_1));
    }

    @Test
    public void eachMatchesShouldRejectEmptyCollectionValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found an empty collection: []");

        collector.checkThat(Collections.emptyList(), eachMatches(EQUAL, Collections.emptyList()));
    }

    @Test
    public void eachMatchesShouldMatchCollectionValues() {
        collector.checkThat(Collections.singleton(STRING_1), eachMatches(EQUAL, STRING_1));
        collector.checkThat(Collections.singleton(INT_1), eachMatches(EQUAL, INT_1));
        collector.checkThat(Collections.singleton(LONG_1), eachMatches(EQUAL, INT_1));
        collector.checkThat(Collections.singleton(INT_1), eachMatches(EQUAL, LONG_1));

        collector.checkThat(Collections.singleton(STRING_1), not(eachMatches(EQUAL, STRING_2)));
        collector.checkThat(Collections.singleton(INT_1), not(eachMatches(EQUAL, INT_2)));
        collector.checkThat(Collections.singleton(LONG_1), not(eachMatches(EQUAL, INT_2)));
        collector.checkThat(Collections.singleton(INT_1), not(eachMatches(EQUAL, LONG_2)));

        collector.checkThat(Arrays.asList(STRING_1, STRING_1), eachMatches(NOT_EQUAL, STRING_2));
        collector.checkThat(Arrays.asList(INT_1, INT_1, INT_1), eachMatches(NOT_EQUAL, INT_2));
        collector.checkThat(Arrays.asList(LONG_1, LONG_1, LONG_1), eachMatches(NOT_EQUAL, INT_2));
        collector.checkThat(Arrays.asList(INT_1, INT_1, INT_1), eachMatches(NOT_EQUAL, LONG_2));

        collector.checkThat(Arrays.asList(STRING_1, STRING_1), not(eachMatches(NOT_EQUAL, STRING_1)));
        collector.checkThat(Arrays.asList(INT_1, INT_1, INT_1), not(eachMatches(NOT_EQUAL, INT_1)));
        collector.checkThat(Arrays.asList(LONG_1, LONG_1, LONG_1), not(eachMatches(NOT_EQUAL, INT_1)));
        collector.checkThat(Arrays.asList(INT_1, INT_1, INT_1), not(eachMatches(NOT_EQUAL, LONG_1)));

        collector.checkThat(Collections.singleton(Arrays.asList(STRING_1, STRING_2, STRING_3)),
                eachMatches(CONTAINS, STRING_1));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)), eachMatches(CONTAINS, INT_3));
        collector.checkThat(Collections.singleton(Arrays.asList(LONG_1, LONG_2, LONG_3)), eachMatches(CONTAINS, INT_3));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)), eachMatches(CONTAINS, LONG_3));

        collector.checkThat(Collections.singleton(Arrays.asList(STRING_1, STRING_2, STRING_3)),
                not(eachMatches(CONTAINS, STRING_4)));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)),
                not(eachMatches(CONTAINS, INT_4)));
        collector.checkThat(Collections.singleton(Arrays.asList(LONG_1, LONG_2, LONG_3)),
                not(eachMatches(CONTAINS, INT_4)));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)),
                not(eachMatches(CONTAINS, LONG_4)));

        collector.checkThat(Collections.singleton(Arrays.asList(STRING_1, STRING_2, STRING_3)),
                eachMatches(NOT_CONTAINS, STRING_4));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)),
                eachMatches(NOT_CONTAINS, INT_4));
        collector.checkThat(Collections.singleton(Arrays.asList(LONG_1, LONG_2, LONG_3)),
                eachMatches(NOT_CONTAINS, INT_4));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)),
                eachMatches(NOT_CONTAINS, LONG_4));

        collector.checkThat(Collections.singleton(Arrays.asList(STRING_1, STRING_2, STRING_3)),
                not(eachMatches(NOT_CONTAINS, STRING_2)));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)),
                not(eachMatches(NOT_CONTAINS, INT_3)));
        collector.checkThat(Collections.singleton(Arrays.asList(LONG_1, LONG_2, LONG_3)),
                not(eachMatches(NOT_CONTAINS, INT_3)));
        collector.checkThat(Collections.singleton(Arrays.asList(INT_1, INT_2, INT_3)),
                not(eachMatches(NOT_CONTAINS, LONG_3)));
    }
}

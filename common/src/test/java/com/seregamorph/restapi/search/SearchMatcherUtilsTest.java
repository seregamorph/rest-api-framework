package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.SearchOperator.IS;
import static com.seregamorph.restapi.search.SearchOperator.IS_NOT;
import static com.seregamorph.restapi.search.SearchValue.BLANK;
import static com.seregamorph.restapi.search.SearchValue.EMPTY;
import static com.seregamorph.restapi.search.SearchValue.NULL;
import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;

public class SearchMatcherUtilsTest extends AbstractUnitTest {

    @Test
    public void nullShouldMatchNullEmptyAndBlank() {
        collector.checkThat(SearchMatcherUtils.matches(null, IS, NULL), is(true));
        collector.checkThat(SearchMatcherUtils.matches(null, IS_NOT, NULL), is(false));

        collector.checkThat(SearchMatcherUtils.matches(null, IS, EMPTY), is(true));
        collector.checkThat(SearchMatcherUtils.matches(null, IS_NOT, EMPTY), is(false));

        collector.checkThat(SearchMatcherUtils.matches(null, IS, BLANK), is(true));
        collector.checkThat(SearchMatcherUtils.matches(null, IS_NOT, BLANK), is(false));
    }

    @Test
    public void emptyShouldMatchEmptyAndBlank() {
        collector.checkThat(SearchMatcherUtils.matches("", IS, NULL), is(false));
        collector.checkThat(SearchMatcherUtils.matches("", IS_NOT, NULL), is(true));

        collector.checkThat(SearchMatcherUtils.matches("", IS, EMPTY), is(true));
        collector.checkThat(SearchMatcherUtils.matches("", IS_NOT, EMPTY), is(false));

        collector.checkThat(SearchMatcherUtils.matches("", IS, BLANK), is(true));
        collector.checkThat(SearchMatcherUtils.matches("", IS_NOT, BLANK), is(false));
    }

    @Test
    public void blankShouldNotMatchNullOrEmpty() {
        collector.checkThat(SearchMatcherUtils.matches(" ", IS, NULL), is(false));
        collector.checkThat(SearchMatcherUtils.matches(" ", IS_NOT, NULL), is(true));

        collector.checkThat(SearchMatcherUtils.matches(" ", IS, EMPTY), is(false));
        collector.checkThat(SearchMatcherUtils.matches(" ", IS_NOT, EMPTY), is(true));

        collector.checkThat(SearchMatcherUtils.matches(" ", IS, BLANK), is(true));
        collector.checkThat(SearchMatcherUtils.matches(" ", IS_NOT, BLANK), is(false));
    }

    @Test
    public void numberShouldNotMatchNullEmptyOrBlank() {
        collector.checkThat(SearchMatcherUtils.matches(Integer.MAX_VALUE, IS, NULL), is(false));
        collector.checkThat(SearchMatcherUtils.matches(Integer.MAX_VALUE, IS_NOT, NULL), is(true));

        collector.checkThat(SearchMatcherUtils.matches(Integer.MAX_VALUE, IS, EMPTY), is(false));
        collector.checkThat(SearchMatcherUtils.matches(Integer.MAX_VALUE, IS_NOT, EMPTY), is(true));

        collector.checkThat(SearchMatcherUtils.matches(Integer.MAX_VALUE, IS, BLANK), is(false));
        collector.checkThat(SearchMatcherUtils.matches(Integer.MAX_VALUE, IS_NOT, BLANK), is(true));
    }

    @Test
    public void nonBlankStringShouldNotMatchNullEmptyOrBlank() {
        collector.checkThat(SearchMatcherUtils.matches("foo", IS, NULL), is(false));
        collector.checkThat(SearchMatcherUtils.matches("foo", IS_NOT, NULL), is(true));

        collector.checkThat(SearchMatcherUtils.matches("foo", IS, EMPTY), is(false));
        collector.checkThat(SearchMatcherUtils.matches("foo", IS_NOT, EMPTY), is(true));

        collector.checkThat(SearchMatcherUtils.matches("foo", IS, BLANK), is(false));
        collector.checkThat(SearchMatcherUtils.matches("foo", IS_NOT, BLANK), is(true));
    }
}

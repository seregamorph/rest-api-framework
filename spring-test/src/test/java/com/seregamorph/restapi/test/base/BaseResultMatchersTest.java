package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.sort.SortDirection.ASC;
import static java.util.Comparator.naturalOrder;

import com.seregamorph.restapi.test.base.setup.BaseSetupSupport;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortField;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortFieldDirection;
import com.seregamorph.restapi.test.base.support.FieldMappingSupport;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.Arrays;
import java.util.Collections;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BaseResultMatchersTest extends AbstractUnitTest {

    @Before
    public void prepareCurrentInitTestMethod() throws Exception {
        BaseSetupSupport.setCurrentInitTestMethod(
                BaseResultMatchersTest.class.getMethod("prepareCurrentInitTestMethod"));
    }

    @After
    public void resetCurrentInitTestMethod() {
        BaseSetupSupport.clearCurrentInitTestMethod();
    }

    @Test
    public void shouldMatchEmptyRoot() {
        val content = ""
                + "{\n"
                + "  \"content\": []\n"
                + "}";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(true)),
                Collections.singletonList(new VerifiableSortFieldDirection("missing")));
    }

    @Test
    public void shouldNotMatchAllMissing() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("All elements at path `$[*].missing` are missing");

        val content = ""
                + "[{"
                + "\"id\": 1"
                + "}, {"
                + "\"id\": 2"
                + "}, {"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Collections.singletonList(new VerifiableSortFieldDirection(
                        new VerifiableSortField("missing", naturalOrder(), false), ASC)));
    }

    @Test
    public void shouldMatchMissingNullsLast() {
        val content = ""
                + "[{"
                + "\"id\": 1"
                + "}, {"
                + "\"id\": 2"
                + "}, {"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Collections.singletonList(new VerifiableSortFieldDirection(
                        new VerifiableSortField("id", naturalOrder(), false), ASC)));
    }

    @Test
    public void shouldMatchNullsFirst() {
        val content = ""
                + "[{"
                + "\"id\": null"
                + "}, {"
                + "\"id\": 1"
                + "}, {"
                + "\"id\": 2"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Collections.singletonList(new VerifiableSortFieldDirection("id")));
    }

    @Test
    public void shouldMatchAllNulls() {
        val content = ""
                + "[{"
                + "\"id\": null"
                + "}, {"
                + "\"id\": null"
                + "}, {"
                + "\"id\": null"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Collections.singletonList(new VerifiableSortFieldDirection("id")));
    }

    @Test
    public void shouldNotMatchNullsFirst() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found unordered elements {id=2} and {id=null}");

        val content = ""
                + "[{"
                + "\"id\": 1"
                + "}, {"
                + "\"id\": 2"
                + "}, {"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Collections.singletonList(new VerifiableSortFieldDirection("id")));
    }

    @Test
    public void shouldMatchMixedSortField() {
        val content = ""
                + "[{\n"
                + "  \"first\": 2,\n"
                + "  \"second\": 1\n"
                + "}, {\n"
                + "  \"first\": 11,\n"
                + "  \"second\": 2\n"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Arrays.asList(new VerifiableSortFieldDirection("first"), new VerifiableSortFieldDirection("second")));
    }

    @Test
    public void shouldMatchStringMixedSortField() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found unordered elements {first=2, second=1} and {first=11, second=2}");

        val content = ""
                + "[{\n"
                + "  \"first\": \"2\",\n"
                + "  \"second\": 1\n"
                + "}, {\n"
                + "  \"first\": \"11\",\n"
                + "  \"second\": 2\n"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Arrays.asList(new VerifiableSortFieldDirection("first"), new VerifiableSortFieldDirection("second")));
    }

    @Test
    public void pageShouldMatchMixedSortField() {
        val content = ""
                + "{\n"
                + "  \"content\": [{\n"
                + "    \"first\": 1,\n"
                + "    \"second\": 1\n"
                + "  }, {\n"
                + "    \"first\": 1,\n"
                + "    \"second\": 2\n"
                + "  }]\n"
                + "}";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(true)),
                Arrays.asList(new VerifiableSortFieldDirection("first"), new VerifiableSortFieldDirection("second")));
    }

    @Test
    public void shouldMatchMixedSortFieldWithMissingField() {
        val content = ""
                + "[{\n"
                + "  \"first\": 1,\n"
                + "  \"second\": 1\n"
                + "}, {\n"
                + "  \"first\": 1,\n"
                + "  \"second\": 2,\n"
                + "  \"missing\": {\"id\":1}"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Arrays.asList(new VerifiableSortFieldDirection("first"), new VerifiableSortFieldDirection("second"),
                        new VerifiableSortFieldDirection("missing.id")));
    }

    @Test
    public void shouldNotMatchMixedSortField() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found unordered elements {first=1, second=2} and {first=1, second=1}");

        val content = ""
                + "[{\n"
                + "  \"first\": 1,\n"
                + "  \"second\": 2\n"
                + "}, {\n"
                + "  \"first\": 1,\n"
                + "  \"second\": 1\n"
                + "}]";

        BaseResultMatchers.match(content, new FieldMappingSupport<>(new GetAllSetup().setPaginationSupported(false)),
                Arrays.asList(new VerifiableSortFieldDirection("first"), new VerifiableSortFieldDirection("second")));
    }

}

package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.base.FrameworkRunner.getRunnerName;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class FrameworkRunnerTest extends AbstractUnitTest {

    static final ThreadLocal<Boolean> simpleTestExecuted = new ThreadLocal<>();

    @Before
    @After
    public void resetSimpleTestExecuted() {
        simpleTestExecuted.remove();
    }

    @Test
    public void getRunnerNameShouldReturnNonEmptyString() {
        collector.checkThat(getRunnerName(""), equalTo("/"));
    }

    @Test
    public void getRunnerNameShouldReturnPlainEndpointAsIs() {
        collector.checkThat(getRunnerName("/api/v3/users"), equalTo("/api/v3/users"));
        collector.checkThat(getRunnerName("/api/v3/users/{id}"), equalTo("/api/v3/users/{id}"));
    }

    @Test
    public void getRunnerNameShouldErasePathVariableRegex() {
        collector.checkThat(getRunnerName("/api/v3/vcs-repositories/{vcsProvider:.*}"),
                equalTo("/api/v3/vcs-repositories/{vcsProvider}"));
        collector.checkThat(getRunnerName("/api/v3/vcs-repositories/{vcsProvider:.+}"),
                equalTo("/api/v3/vcs-repositories/{vcsProvider}"));
        collector.checkThat(getRunnerName("/api/v2/vcs/forks/{forkId}/{operation:branch-created|branch-deleted|branch-updated}/{id}"),
                equalTo("/api/v2/vcs/forks/{forkId}/{operation}/{id}"));
    }

    @Test
    public void getRunnerNameShouldReplaceDots() {
        collector.checkThat(getRunnerName("/api/v3/scm-repositories/{id}/revisions/{revision}/sources.zip"),
                equalTo("/api/v3/scm-repositories/{id}/revisions/{revision}/sources_zip"));
    }

    @Test
    public void shouldFilterInitTestExecutions() throws Exception {
        val filter = Filter.matchMethodDescription(
                Description.createTestDescription(FrameworkRunnerIT.class, "getAllSetup"));
        val executions = Arrays.asList(
                "getAllShouldManageProvidedHeadersAndParameters[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldManageSpecificPageNumber[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldManageSpecificPageSize[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldRepeatedManageProvidedHeadersAndParameters[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldReturn400WhenNegativePage[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldReturn400WhenNegativeSize[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldReturn400WhenNonNumericPage[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldReturn400WhenNonNumericSize[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldReturn400WhenZeroSize[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)",
                "getAllShouldReturn401WhenAnonymous[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)");

        validateFilter(filter, executions);
    }

    @Test
    public void shouldFilterParameterizedTestExecutions() throws Exception {
        val filter = Filter.matchMethodDescription(
                Description.createTestDescription(FrameworkRunnerIT.class, "getAllShouldManageProvidedHeadersAndParameters"));
        val executions = Arrays.asList(
                "getAllShouldManageProvidedHeadersAndParameters[/all](com.seregamorph.restapi.test.base.FrameworkRunnerIT)");

        validateFilter(filter, executions);
    }

    @Test
    public void shouldFilterTestExecutions() throws Exception {
        val filter = Filter.matchMethodDescription(
                Description.createTestDescription(FrameworkRunnerIT.class, "simpleTest"));
        val executions = Collections.singletonList(
                "simpleTest(com.seregamorph.restapi.test.base.FrameworkRunnerIT)");

        validateFilter(filter, executions);

        assertTrue("simpleTest() should be executed", simpleTestExecuted.get());
    }

    private void validateFilter(Filter filter, List<String> expectedExecutions) throws Exception {
        val frameworkRunner = new FrameworkRunner(FrameworkRunnerIT.class);

        val runNotifier = new RunNotifier();
        val testDescriptions = new ArrayList<String>();
        val runListener = new RunListener() {
            @Override
            public void testStarted(Description description) {
                testDescriptions.add(description.toString());
            }
        };
        runNotifier.addListener(runListener);

        frameworkRunner.filter(filter);
        frameworkRunner.run(runNotifier);

        assertEquals(String.join("\n", expectedExecutions), String.join("\n", testDescriptions));
    }

}

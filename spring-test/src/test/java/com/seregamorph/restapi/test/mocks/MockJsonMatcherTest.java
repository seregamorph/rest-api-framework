package com.seregamorph.restapi.test.mocks;

import static com.seregamorph.restapi.test.mocks.MockJsonMatcher.SEPARATOR;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.seregamorph.restapi.test.TestDescription;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import lombok.val;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MockJsonMatcherTest extends AbstractUnitTest {

    private static final String METHOD_GROUP = "method-group";
    private static final String TARGET_METHOD_GROUP = "target-method-group";
    private static final String EXECUTION_ID = "execution-id";
    private static final String BAD_VALUE = "bad-value";

    @Test
    public void isValidShouldReturnFalseIfClassNotSpecified() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher();

        matcher.validate(errors);

        assertEquals(singletonList("className is null"), errors);
    }

    @Test
    public void isValidShouldReturnFalseIfIncludesContainsBlank() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setIncludes(singletonList(" "));

        matcher.validate(errors);

        assertEquals(Arrays.asList("includes section has blank string: [ ]",
                "Included test method names [ ] not exist in com.seregamorph.restapi.test.mocks.TestClass"), errors);
    }

    @Test
    public void isValidShouldReturnFalseIfExcludesContainsBlank() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setExcludes(singletonList(" "));

        matcher.validate(errors);

        assertEquals(Arrays.asList("excludes section has blank string: [ ]",
                "Excluded test method names null not exist in com.seregamorph.restapi.test.mocks.TestClass"), errors);
    }

    @Test
    public void isValidShouldReturnFalseIfIncludesContainsInvalidMethod() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setIncludes(singletonList(BAD_VALUE));

        matcher.validate(errors);

        assertEquals(singletonList("Included test method names [bad-value] not exist in "
                + "com.seregamorph.restapi.test.mocks.TestClass"), errors);
    }

    @Test
    public void isValidShouldReturnTrueIfIncludesContainsValidMethodWithExecutionId() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setIncludes(singletonList(TestClass.DO_SOMETHING + SEPARATOR + EXECUTION_ID));

        matcher.validate(errors);

        assertEquals(Collections.emptyList(), errors);
    }

    @Test
    public void isValidShouldReturnFalseIfExcludesContainsInvalidMethod() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setExcludes(singletonList(BAD_VALUE));

        matcher.validate(errors);

        assertEquals(singletonList("Excluded test method names null not exist in "
                + "com.seregamorph.restapi.test.mocks.TestClass"), errors);
    }

    @Test
    public void isValidShouldReturnFalseIfExcludesContainsValidMethodWithExecutionId() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setExcludes(singletonList(TestClass.DO_SOMETHING + SEPARATOR + EXECUTION_ID));

        matcher.validate(errors);

        assertEquals(singletonList("Excluded test method names null not exist in "
                + "com.seregamorph.restapi.test.mocks.TestClass"), errors);
    }

    @Test
    public void isValidShouldReturnFalseIfIncludesAndExcludesOverlaps() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setIncludes(singletonList(TestClass.DO_SOMETHING))
                .setExcludes(singletonList(TestClass.DO_SOMETHING));

        matcher.validate(errors);

        assertEquals(singletonList("Includes [doSomething] and excludes [doSomething] have intersections"), errors);
    }

    @Test
    public void isValidShouldReturnFalseIfIncludesWithExecutionIdAndExcludesOverlaps() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setIncludes(singletonList(TestClass.DO_SOMETHING + SEPARATOR + EXECUTION_ID))
                .setExcludes(singletonList(TestClass.DO_SOMETHING));

        matcher.validate(errors);

        assertEquals(singletonList("Includes [doSomething] and excludes [doSomething] have intersections"), errors);
    }

    @Test
    public void isValidShouldReturnTrue() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setIncludes(singletonList(TestClass.DO_SOMETHING))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING));

        matcher.validate(errors);

        assertEquals(Collections.emptyList(), errors);
    }

    @Test
    public void isValidWithExecutionIdShouldReturnTrue() {
        val errors = new ArrayList<String>();
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setIncludes(singletonList(TestClass.DO_SOMETHING + SEPARATOR + EXECUTION_ID))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING));

        matcher.validate(errors);

        assertEquals(Collections.emptyList(), errors);
    }

    @Test
    public void matchesShouldReturnFalseIfMethodGroupDefinedInMatcherButNotInTestDescription() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP));
        TestDescription description = new TestDescription(sampleClassDescription());

        collector.checkThat(matcher.matches(description), is(false));
    }

    @Test
    public void matchesShouldReturnFalseIfMethodGroupMismatches() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(BAD_VALUE);

        collector.checkThat(matcher.matches(description), is(false));
    }

    @Test
    public void matchesShouldReturnFalseIfTargetMethodGroupDefinedInMatcherButNotInTestDescription() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP);

        collector.checkThat(matcher.matches(description), is(false));
    }

    @Test
    public void matchesShouldReturnFalseIfTargetMethodGroupMismatches() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(BAD_VALUE));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP);

        collector.checkThat(matcher.matches(description), is(false));
    }

    @Test
    public void matchesShouldReturnFalseIfMethodExcluded() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP))
                .setExcludes(singletonList(TestClass.DO_SOMETHING));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP);

        collector.checkThat(matcher.matches(description), is(false));
    }

    @Test
    public void matchesShouldReturnFalseIfExecutionIdDefinedInMatcherButNotInTestDescription() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING))
                .setIncludes(singletonList(TestClass.DO_SOMETHING + SEPARATOR + EXECUTION_ID));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP);

        collector.checkThat(matcher.matches(description), is(false));
    }

    @Test
    public void matchesShouldReturnFalseIfExecutionIdMismatches() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING))
                .setIncludes(singletonList(TestClass.DO_SOMETHING + SEPARATOR + EXECUTION_ID));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP)
                .setExecutionId(BAD_VALUE);

        collector.checkThat(matcher.matches(description), is(false));
    }

    @Test
    public void matchesShouldReturnTrueIfExecutionIdMatches() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING))
                .setIncludes(singletonList(TestClass.DO_SOMETHING + SEPARATOR + EXECUTION_ID));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP)
                .setExecutionId(EXECUTION_ID);

        collector.checkThat(matcher.matches(description), is(true));
    }

    @Test
    public void matchesShouldReturnTrueIfMethodWithoutExecutionIdInMatcherMatchesMethodWithExecutionIdInTestDescription() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING))
                .setIncludes(singletonList(TestClass.DO_SOMETHING));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP)
                .setExecutionId(EXECUTION_ID);

        collector.checkThat(matcher.matches(description), is(true));
    }

    @Test
    public void matchesShouldReturnTrueIfMethodWithoutExecutionIdMatches() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING))
                .setIncludes(singletonList(TestClass.DO_SOMETHING));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP);


        collector.checkThat(matcher.matches(description), is(true));
    }

    @Test
    public void matchesShouldReturnTrueIfMatcherIncludesIsEmpty() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(singletonList(METHOD_GROUP))
                .setTargetMethodGroups(singletonList(TARGET_METHOD_GROUP))
                .setExcludes(singletonList(TestParentClass.DO_NOTHING));
        TestDescription description = new TestDescription(sampleClassDescription())
                .setMethodGroup(METHOD_GROUP)
                .setTargetMethodGroup(TARGET_METHOD_GROUP)
                .setExecutionId(EXECUTION_ID);

        collector.checkThat(matcher.matches(description), is(true));
    }

    @Test
    public void toStringShouldReturnCorrectResultWithDefaultValues() {
        MockJsonMatcher matcher = new MockJsonMatcher().setTestClass(TestClass.class);

        String result = matcher.toString();

        collector.checkThat(result, equalTo(TestClass.class.getName() + ", methodGroups: (ALL)"
                + ", targetMethodGroups: (ALL), include: (ALL), exclude: (NONE)"));
    }

    @Test
    public void toStringShouldReturnCorrectResult() {
        MockJsonMatcher matcher = new MockJsonMatcher()
                .setTestClass(TestClass.class)
                .setMethodGroups(Arrays.asList("foo", "bar"))
                .setTargetMethodGroups(singletonList("baz"))
                .setIncludes(singletonList("what#ever"));

        String result = matcher.toString();

        collector.checkThat(result, equalTo(TestClass.class.getName()
                + ", methodGroups: (foo, bar), targetMethodGroups: (baz), include: (what#ever), exclude: (NONE)"));
    }

    @SuppressWarnings("unchecked")
    private Description sampleClassDescription() {
        Description description = mock(Description.class);
        when(description.getTestClass()).thenReturn((Class) TestClass.class);
        when(description.getClassName()).thenReturn(TestClass.class.getName());
        when(description.getMethodName()).thenReturn(TestClass.DO_SOMETHING);
        return description;
    }

}

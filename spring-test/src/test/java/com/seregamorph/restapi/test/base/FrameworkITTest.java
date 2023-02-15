package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.base.FrameworkIT.localhostUriMatcher;
import static com.seregamorph.restapi.test.base.FrameworkIT.locationHeaderMatcher;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;

public class FrameworkITTest extends AbstractUnitTest {

    @Test
    public void locationWithoutPortShouldMatch() {
        assertThat("http://localhost/api/users/1", locationHeaderMatcher("http://localhost", "/api/users/{id}"));
    }

    @Test
    public void locationWithoutPortShouldNotMatch() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: Location header starts with \"http://localhost\" and "
                + "path matches ant pattern \"/api/users/{id}\"");

        assertThat("http://localhost/api/users/", locationHeaderMatcher("http://localhost", "/api/users/{id}"));
    }

    @Test
    public void locationWithPortShouldMatch() {
        assertThat("http://localhost:8080/api/users/1", locationHeaderMatcher("http://localhost:8080", "/api/users/{id}"));
    }

    @Test
    public void locationWithPortShouldNotMatch() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: Location header starts with \"http://localhost:8080\" and "
                + "path matches ant pattern \"/api/users/{id}\"");

        assertThat("http://localhost/api/users/1", locationHeaderMatcher("http://localhost:8080", "/api/users/{id}"));
    }

    @Test
    public void localhostUriShouldMatch() {
        assertThat("http://localhost/api/groups/1", localhostUriMatcher(equalTo("/api/groups/1")));
    }

    @Test
    public void localhostUriShouldNotMatch() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: URI starts with \"http://localhost\" and path matches \"/api/users/1\"");

        assertThat("http://localhost/api/groups/1", localhostUriMatcher(equalTo("/api/users/1")));
    }

}

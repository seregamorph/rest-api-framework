package com.seregamorph.restapi.controllers;

import com.seregamorph.restapi.test.AbstractSpringTest;
import lombok.val;
import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;

public class AbstractBaseRestControllerTest extends AbstractSpringTest {

    private static final String HEADER_X_FORWARDED_HOST = "x-forwarded-host";
    private static final String HEADER_X_FORWARDED_PROTO = "x-forwarded-proto";

    private final TestController controller = new TestController();

    @After
    public void cleanup() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void locationShouldBeLocalhost() {
        val request = new MockHttpServletRequest(POST.name(), "/values");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        val resp = controller.created(1);

        collector.checkThat(resp.getStatusCode(), equalTo(CREATED));
        collector.checkThat(resp.getHeaders().getLocation(), hasToString("http://localhost/values/1"));
    }

    @Test
    public void locationShouldBeForwardedProtoHostPort() {
        val request = new MockHttpServletRequest(POST.name(), "/values");
        request.addHeader(HEADER_X_FORWARDED_HOST, "proxy:8443");
        request.addHeader(HEADER_X_FORWARDED_PROTO, "https");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        val resp = controller.created(1);

        collector.checkThat(resp.getStatusCode(), equalTo(CREATED));
        collector.checkThat(resp.getHeaders().getLocation(), hasToString("https://proxy:8443/values/1"));
    }

    @Test
    public void locationShouldFormatCustomTemplate() {
        val request = new MockHttpServletRequest(POST.name(), "/values");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        val location = controller.location("/custom/{id}", 1);

        collector.checkThat(location, hasToString("http://localhost/values/custom/1"));
    }

    @RequestMapping(path = "/values")
    private static class TestController extends AbstractBaseRestController {

    }

}

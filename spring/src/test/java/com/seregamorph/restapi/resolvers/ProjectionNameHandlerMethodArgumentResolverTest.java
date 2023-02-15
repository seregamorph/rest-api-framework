package com.seregamorph.restapi.resolvers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.common.Constants;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public class ProjectionNameHandlerMethodArgumentResolverTest extends AbstractUnitTest {

    @Test
    public void shouldSupportParameter() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();

        assertTrue(resolver.supportsParameter(createParameter()));
    }

    @Test
    public void shouldResolveDefaultProjection() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();
        val webRequest = mock(NativeWebRequest.class);

        val projection = resolver.resolveArgument(createParameter(), null, webRequest, null);

        assertEquals(Projection.DEFAULT, projection);
    }

    @Test
    public void shouldResolveHeaderProjectionLowercase() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();
        val webRequest = mock(NativeWebRequest.class);
        when(webRequest.getHeader(Constants.HEADER_ACCEPT_PROJECTION))
                .thenReturn(Projection.DETAIL.name().toLowerCase());

        val projection = resolver.resolveArgument(createParameter(), null, webRequest, null);

        assertEquals(Projection.DETAIL, projection);
    }

    @Test
    public void shouldResolveHeaderProjectionUppercase() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();
        val webRequest = mock(NativeWebRequest.class);
        when(webRequest.getHeader(Constants.HEADER_ACCEPT_PROJECTION))
                .thenReturn(Projection.DETAIL.name());

        val projection = resolver.resolveArgument(createParameter(), null, webRequest, null);

        assertEquals(Projection.DETAIL, projection);
    }

    @Test
    public void shouldResolveQueryParameterProjectionLowercase() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();
        val webRequest = mock(NativeWebRequest.class);
        when(webRequest.getParameter(Constants.PARAM_PROJECTION))
                .thenReturn(Projection.DETAIL.name().toLowerCase());

        val projection = resolver.resolveArgument(createParameter(), null, webRequest, null);

        assertEquals(Projection.DETAIL, projection);
    }

    @Test
    public void shouldResolveQueryParameterProjectionUppercase() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();
        val webRequest = mock(NativeWebRequest.class);
        when(webRequest.getParameter(Constants.PARAM_PROJECTION))
                .thenReturn(Projection.DETAIL.name());

        val projection = resolver.resolveArgument(createParameter(), null, webRequest, null);

        assertEquals(Projection.DETAIL, projection);
    }

    @Test
    public void shouldResolveBothHeaderAndParameterProjection() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();
        val webRequest = mock(NativeWebRequest.class);
        when(webRequest.getHeader(Constants.HEADER_ACCEPT_PROJECTION))
                .thenReturn(Projection.DETAIL.name());
        when(webRequest.getParameter(Constants.PARAM_PROJECTION))
                .thenReturn(Projection.DETAIL.name());

        val projection = resolver.resolveArgument(createParameter(), null, webRequest, null);

        assertEquals(Projection.DETAIL, projection);
    }

    @Test
    public void shouldFailResolveBothHeaderAndParameterProjectionWhenDiffers() {
        val resolver = new ProjectionNameHandlerMethodArgumentResolver();
        val webRequest = mock(NativeWebRequest.class);
        when(webRequest.getHeader(Constants.HEADER_ACCEPT_PROJECTION))
                .thenReturn(Projection.DETAIL.name());
        when(webRequest.getParameter(Constants.PARAM_PROJECTION))
                .thenReturn(Projection.DEFAULT.name());

        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage("IllegalArgumentException: Argument [DEFAULT] for "
                + "parameter [projection] is invalid: [Provided both header \"Accept-Projection\" and "
                + "query parameter \"projection\" and they have different values: [DETAIL]");

        resolver.resolveArgument(createParameter(), null, webRequest, null);
    }

    private enum Projection implements ProjectionName {
        DEFAULT,
        DETAIL;

        @Override
        public Class<? extends BaseProjection> getProjectionClass() {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static MethodParameter createParameter() {
        val parameter = mock(MethodParameter.class);
        when(parameter.getParameterType())
                .thenReturn((Class) Projection.class);
        return parameter;
    }

}

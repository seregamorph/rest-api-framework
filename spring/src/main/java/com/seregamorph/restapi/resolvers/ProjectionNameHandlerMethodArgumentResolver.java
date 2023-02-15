package com.seregamorph.restapi.resolvers;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.common.Constants;
import com.seregamorph.restapi.exceptions.TypeMismatchExceptions;
import java.util.Arrays;
import java.util.Locale;
import lombok.val;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Default resolver for controller method arguments of ProjectionName subtypes.
 * Either <pre>projection</pre> parameter is used (mostly in case of GET request)
 * or the <pre>Accept-Projection</pre> header in case of non-GET. The projection name
 * is converted to upper-case before resolution.
 * If no value provided, the <pre>DEFAULT</pre> enum constant is used, if it is not present, the first
 * projection enum constant is returned as a fallback.
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
public class ProjectionNameHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ProjectionName.class.isAssignableFrom(parameter.getParameterType())
                && Enum.class.isAssignableFrom(parameter.getParameterType());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum<?> resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Class<? extends Enum> enumType = parameter.getParameterType().asSubclass(Enum.class);
        val projectionHeader = trimToNull(webRequest.getHeader(Constants.HEADER_ACCEPT_PROJECTION));
        String parameterName;
        String projection = trimToNull(webRequest.getParameter(Constants.PARAM_PROJECTION));
        if (projection == null) {
            projection = projectionHeader;
            parameterName = Constants.HEADER_ACCEPT_PROJECTION;
        } else {
            parameterName = Constants.PARAM_PROJECTION;
            if (projectionHeader != null && !projectionHeader.equalsIgnoreCase(projection)) {
                throw TypeMismatchExceptions.create(enumType, parameterName, projection,
                        "Provided both header \"" + Constants.HEADER_ACCEPT_PROJECTION + "\" "
                                + "and query parameter \"" + Constants.PARAM_PROJECTION + "\" and "
                                + "they have different values: [" + projectionHeader + "]");
            }
        }

        if (projection == null) {
            return ProjectionName.getDefaultProjection(enumType);
        }
        try {
            return Enum.valueOf(enumType, projection.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw TypeMismatchExceptions.create(enumType, parameterName, projection, "Possible values: "
                    + Arrays.toString(enumType.getEnumConstants()));
        }
    }

}

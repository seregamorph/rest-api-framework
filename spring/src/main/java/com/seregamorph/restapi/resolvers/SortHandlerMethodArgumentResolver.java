package com.seregamorph.restapi.resolvers;

import static com.seregamorph.restapi.common.Constants.PARAM_SORT;

import com.seregamorph.restapi.sort.Sort;
import com.seregamorph.restapi.sort.SortArgumentResolver;
import com.seregamorph.restapi.sort.SortParam;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
public class SortHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SortParam.class);
    }

    @Override
    public Sort resolveArgument(
            @NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        SortParam sortParam = parameter.getParameterAnnotation(SortParam.class);
        assert sortParam != null;
        // If parameter is repeated, we have an array with more than 1 element
        String[] values = webRequest.getParameterValues(PARAM_SORT);
        return SortArgumentResolver.resolveArgument(sortParam, values);
    }
}

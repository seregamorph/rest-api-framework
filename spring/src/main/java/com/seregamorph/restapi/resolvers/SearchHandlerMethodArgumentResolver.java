package com.seregamorph.restapi.resolvers;

import static com.seregamorph.restapi.common.Constants.PARAM_SEARCH;

import com.seregamorph.restapi.search.SearchArgumentResolver;
import com.seregamorph.restapi.search.SearchParam;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
public class SearchHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SearchParam.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        SearchParam searchParam = parameter.getParameterAnnotation(SearchParam.class);
        // If parameter is repeated, we have an array with more than 1 element
        String[] searchValues = webRequest.getParameterValues(PARAM_SEARCH);
        return SearchArgumentResolver.resolveArgument(searchParam, searchValues);
    }
}

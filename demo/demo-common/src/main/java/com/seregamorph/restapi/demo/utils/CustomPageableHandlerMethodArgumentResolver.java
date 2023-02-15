package com.seregamorph.restapi.demo.utils;

import static org.springframework.util.StringUtils.hasText;

import com.seregamorph.restapi.config.spi.FrameworkConfigHolder;
import com.seregamorph.restapi.exceptions.BadRequestException;
import javax.annotation.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {

    private static final int MIN_PAGE = 0;

    static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = FrameworkConfigHolder.getFrameworkConfig()
            .getDefaultPageSize();
    static final int MIN_SIZE = 1;
    static final String MIN_VALIDATION_MESSAGE = "Parameter '%s' must be an integer and not less than %s";

    @Nullable
    @Override
    public Pageable resolveArgument(
            MethodParameter methodParameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        String pageParam = getParameterNameToUse(getPageParameterName(), methodParameter);
        String sizeParam = getParameterNameToUse(getSizeParameterName(), methodParameter);
        String pageString = webRequest.getParameter(pageParam);
        String sizeString = webRequest.getParameter(sizeParam);

        int page = parseParam(pageParam, pageString, MIN_PAGE, DEFAULT_PAGE);
        int size = parseParam(sizeParam, sizeString, MIN_SIZE, DEFAULT_SIZE);

        return createPageRequest(page, size);
    }

    protected PageRequest createPageRequest(int page, int size) {
        return new PageRequest(page, size);
    }

    private static int parseParam(String param, String valueString, int min, int defaultValue) {
        int value = defaultValue;

        if (hasText(valueString)) {
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                throw new BadRequestException(String.format(MIN_VALIDATION_MESSAGE, param, min), e);
            }

            if (value < min) {
                throw new BadRequestException(String.format(MIN_VALIDATION_MESSAGE, param, min));
            }
        }

        return value;
    }

}

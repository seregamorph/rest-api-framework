package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.utils.DemoConstants.SAMPLE_STRING;
import static com.seregamorph.restapi.search.SearchOperator.EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.GREATER_THAN;
import static com.seregamorph.restapi.search.SearchOperator.GREATER_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.IS;
import static com.seregamorph.restapi.search.SearchOperator.IS_NOT;
import static com.seregamorph.restapi.search.SearchOperator.LESS_THAN;
import static com.seregamorph.restapi.search.SearchOperator.LESS_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.NOT_EQUAL;
import static com.seregamorph.restapi.test.base.JsonMatcher.path;
import static org.hamcrest.Matchers.hasSize;

import com.seregamorph.restapi.demo.resources.NestedSearchResource;
import com.seregamorph.restapi.demo.resources.SearchEnum;
import com.seregamorph.restapi.demo.resources.SearchResource;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.search.SearchValue;
import com.seregamorph.restapi.search.SingleSearchCondition;
import com.seregamorph.restapi.test.JsonConstants;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.ResultMatchers;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import org.springframework.test.web.servlet.ResultMatcher;

@InitTest(SearchController.class)
public abstract class SearchControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static GetAllSetup getAllSetup() {
        return new GetAllSetup()
                .setTotalElements(1) // Default search
                .setDefaultResultMatchers(defaultSearchParamMatcher())
                .supportSearchField(SearchResource.Fields.STRING_FIELD, EQUAL, SAMPLE_STRING,
                        simpleMatcher(SearchResource.Fields.STRING_FIELD, EQUAL, SAMPLE_STRING))
                .supportSearchField(SearchResource.Fields.STRING_FIELD, IS, SearchValue.NULL,
                        simpleMatcher(SearchResource.Fields.STRING_FIELD, IS, SearchValue.NULL))
                .supportSearchField(SearchResource.Fields.STRING_FIELD, IS_NOT, SearchValue.EMPTY,
                        simpleMatcher(SearchResource.Fields.STRING_FIELD, IS_NOT, SearchValue.EMPTY))
                .supportSearchField(SearchResource.Fields.STRING_FIELD, IS, SearchValue.BLANK,
                        simpleMatcher(SearchResource.Fields.STRING_FIELD, IS, SearchValue.BLANK))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_BOOLEAN_FIELD, NOT_EQUAL, false,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_BOOLEAN_FIELD, NOT_EQUAL, false))
                .supportSearchField(SearchResource.Fields.BOOLEAN_FIELD, EQUAL, true,
                        simpleMatcher(SearchResource.Fields.BOOLEAN_FIELD, EQUAL, true))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_INT_FIELD, GREATER_THAN, 1,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_INT_FIELD, GREATER_THAN, 1))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_INT_FIELD, GREATER_THAN_OR_EQUAL, 2,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_INT_FIELD, GREATER_THAN_OR_EQUAL, 2))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_INT_FIELD, LESS_THAN, 3,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_INT_FIELD, LESS_THAN, 3))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_INT_FIELD, LESS_THAN_OR_EQUAL, 4,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_INT_FIELD, LESS_THAN_OR_EQUAL, 4))
                .supportSearchField(SearchResource.Fields.INTEGER_FIELD, EQUAL, 5,
                        simpleMatcher(SearchResource.Fields.INTEGER_FIELD, EQUAL, 5))
                .supportSearchField(SearchResource.Fields.INTEGER_FIELD, NOT_EQUAL, 6,
                        simpleMatcher(SearchResource.Fields.INTEGER_FIELD, NOT_EQUAL, 6))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_LONG_FIELD, GREATER_THAN, 7,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_LONG_FIELD, GREATER_THAN, 7))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_LONG_FIELD, LESS_THAN, 8,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_LONG_FIELD, LESS_THAN, 8))
                .supportSearchField(SearchResource.Fields.LONG_FIELD, GREATER_THAN_OR_EQUAL, 9,
                        simpleMatcher(SearchResource.Fields.LONG_FIELD, GREATER_THAN_OR_EQUAL, 9))
                .supportSearchField(SearchResource.Fields.LONG_FIELD, LESS_THAN_OR_EQUAL, 10,
                        simpleMatcher(SearchResource.Fields.LONG_FIELD, LESS_THAN_OR_EQUAL, 10))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_DOUBLE_FIELD, GREATER_THAN, 11.11,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_DOUBLE_FIELD, GREATER_THAN, 11.11))
                .supportSearchField(SearchResource.Fields.PRIMITIVE_DOUBLE_FIELD, LESS_THAN, 12.12,
                        simpleMatcher(SearchResource.Fields.PRIMITIVE_DOUBLE_FIELD, LESS_THAN, 12.12))
                .supportSearchField(SearchResource.Fields.DOUBLE_FIELD, GREATER_THAN_OR_EQUAL, 13.13,
                        simpleMatcher(SearchResource.Fields.DOUBLE_FIELD, GREATER_THAN_OR_EQUAL, 13.13))
                .supportSearchField(SearchResource.Fields.DOUBLE_FIELD, LESS_THAN_OR_EQUAL, 14.14,
                        simpleMatcher(SearchResource.Fields.DOUBLE_FIELD, LESS_THAN_OR_EQUAL, 14.14))
                .supportSearchField(SearchResource.Fields.LOCAL_DATE_FIELD, EQUAL, "2020-07-15",
                        simpleMatcher(SearchResource.Fields.LOCAL_DATE_FIELD, EQUAL, "2020-07-15"))
                .supportSearchField(SearchResource.Fields.LOCAL_DATE_FIELD, NOT_EQUAL, "2020-07-15",
                        simpleMatcher(SearchResource.Fields.LOCAL_DATE_FIELD, NOT_EQUAL, "2020-07-15"))
                .supportSearchField(SearchResource.Fields.LOCAL_DATE_TIME_FIELD, EQUAL, "2020-07-15T12:09:30",
                        simpleMatcher(SearchResource.Fields.LOCAL_DATE_TIME_FIELD, EQUAL, "2020-07-15T12:09:30"))
                .supportSearchField(SearchResource.Fields.LOCAL_DATE_TIME_FIELD, NOT_EQUAL, "2020-07-15T12:09:30",
                        simpleMatcher(SearchResource.Fields.LOCAL_DATE_TIME_FIELD, NOT_EQUAL, "2020-07-15T12:09:30"))
                .supportSearchField(SearchResource.Fields.INSTANT_FIELD, EQUAL, "2020-07-15T12:09:31Z",
                        simpleMatcher(SearchResource.Fields.INSTANT_FIELD, EQUAL, "2020-07-15T12:09:31Z"))
                .supportSearchField(SearchResource.Fields.INSTANT_FIELD, NOT_EQUAL, "2020-07-15T12:09:31Z",
                        simpleMatcher(SearchResource.Fields.INSTANT_FIELD, NOT_EQUAL, "2020-07-15T12:09:31Z"))
                .supportSearchField(SearchResource.Fields.OFFSET_DATE_TIME_FIELD, EQUAL, "2020-07-15T12:09:32+07:00",
                        simpleMatcher(SearchResource.Fields.OFFSET_DATE_TIME_FIELD, EQUAL, "2020-07-15T12:09:32+07:00"))
                .supportSearchField(SearchResource.Fields.OFFSET_DATE_TIME_FIELD, NOT_EQUAL, "2020-07-15T12:09:33+07:00",
                        simpleMatcher(SearchResource.Fields.OFFSET_DATE_TIME_FIELD, NOT_EQUAL, "2020-07-15T12:09:33+07:00"))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.SAMPLE,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.SAMPLE))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.ORANGE,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.ORANGE))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.ERROR,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.ERROR))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.FORWARD,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.FORWARD))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.OR,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.OR))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.ANDROID,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.ANDROID))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.WONDERLAND,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.WONDERLAND))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.RANDOM,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.RANDOM))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.AND,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, EQUAL, SearchEnum.AND))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.SAMPLE,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.SAMPLE))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.ORANGE,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.ORANGE))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.ERROR,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.ERROR))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.FORWARD,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.FORWARD))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.OR,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.OR))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.ANDROID,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.ANDROID))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.WONDERLAND,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.WONDERLAND))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.RANDOM,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.RANDOM))
                .supportSearchField(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.AND,
                        simpleMatcher(SearchResource.Fields.ENUM_FIELD, NOT_EQUAL, SearchEnum.AND))
                .supportSearchField(SearchResource.Fields.NESTED_SEARCH_FIELD, EQUAL, SAMPLE_STRING,
                        simpleMatcher(SearchResource.Fields.NESTED_SEARCH_FIELD + "." + NestedSearchResource.Fields.NESTED_STRING_FIELD, EQUAL, SAMPLE_STRING))
                .supportSearchField(SearchResource.Fields.NESTED_SEARCH_FIELD, NOT_EQUAL, SAMPLE_STRING,
                        simpleMatcher(SearchResource.Fields.NESTED_SEARCH_FIELD + "." + NestedSearchResource.Fields.NESTED_STRING_FIELD, NOT_EQUAL, SAMPLE_STRING))
                .supportSearch("primitiveIntField>=18;primitiveIntField<=60;offsetDateTimeField=2020-02-12T03:30:45-09:30", searchParamMatcher())
                .mapField(SearchResource.Fields.NESTED_SEARCH_FIELD, SearchResource.Fields.NESTED_SEARCH_FIELD, NestedSearchResource.Fields.NESTED_STRING_FIELD);
    }

    private static ResultMatcher simpleMatcher(String field, SearchOperator operator, Object value) {
        Object standardValue = value instanceof SearchValue ? ((SearchValue) value).name() : value;
        return ResultMatchers.of(
                path(JsonConstants.ROOT, hasSize(1)),
                path(JsonConstants.ROOT, 0,
                        path(SingleSearchCondition.Fields.FIELD, field),
                        path(SingleSearchCondition.Fields.OPERATOR, operator),
                        path(SingleSearchCondition.Fields.VALUE, standardValue))
        );
    }

    private static ResultMatcher searchParamMatcher() {
        return ResultMatchers.of(
                path(JsonConstants.ROOT, hasSize(3)),
                path(JsonConstants.ROOT, 0,
                        path(SingleSearchCondition.Fields.FIELD, "primitiveIntField"),
                        path(SingleSearchCondition.Fields.OPERATOR, GREATER_THAN_OR_EQUAL),
                        path(SingleSearchCondition.Fields.VALUE, 18)),
                path(JsonConstants.ROOT, 1,
                        path(SingleSearchCondition.Fields.FIELD, "primitiveIntField"),
                        path(SingleSearchCondition.Fields.OPERATOR, LESS_THAN_OR_EQUAL),
                        path(SingleSearchCondition.Fields.VALUE, 60)),
                path(JsonConstants.ROOT, 2,
                        path(SingleSearchCondition.Fields.FIELD, "offsetDateTimeField"),
                        path(SingleSearchCondition.Fields.OPERATOR, EQUAL),
                        path(SingleSearchCondition.Fields.VALUE, "2020-02-12T03:30:45-09:30"))
        );
    }

    private static ResultMatcher defaultSearchParamMatcher() {
        return ResultMatchers.of(
                path(JsonConstants.ROOT, 0,
                        path(SingleSearchCondition.Fields.FIELD, "offsetDateTimeField"),
                        path(SingleSearchCondition.Fields.OPERATOR, EQUAL),
                        path(SingleSearchCondition.Fields.VALUE, "2020-02-02T10:10:10+05:30"))
        );
    }
}

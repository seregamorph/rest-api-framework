package com.seregamorph.restapi.demo.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.demo.resources.SearchEnum;
import com.seregamorph.restapi.demo.resources.SearchResource;
import com.seregamorph.restapi.demo.services.SearchService;
import com.seregamorph.restapi.search.Search;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.search.SearchParam;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "search")
@RestController
@RequestMapping(path = "/api/search", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

//    @ApiOperation(value = "Search demo.", response = SearchCondition.class, responseContainer = RESPONSE_CONTAINER_LIST)
    @GetMapping
    public Search get(
            @SearchParam(
                    value = {
                            @SearchParam.Field(name = SearchResource.Fields.STRING_FIELD),
                            @SearchParam.Field(name = SearchResource.Fields.PRIMITIVE_BOOLEAN_FIELD, type = boolean.class),
                            @SearchParam.Field(name = SearchResource.Fields.BOOLEAN_FIELD, type = Boolean.class),
                            @SearchParam.Field(name = SearchResource.Fields.PRIMITIVE_INT_FIELD, type = int.class),
                            @SearchParam.Field(name = SearchResource.Fields.INTEGER_FIELD, type = Integer.class),
                            @SearchParam.Field(name = SearchResource.Fields.PRIMITIVE_LONG_FIELD, type = long.class),
                            @SearchParam.Field(name = SearchResource.Fields.LONG_FIELD, type = Long.class),
                            @SearchParam.Field(name = SearchResource.Fields.PRIMITIVE_DOUBLE_FIELD, type = double.class),
                            @SearchParam.Field(name = SearchResource.Fields.DOUBLE_FIELD, type = Double.class),
                            @SearchParam.Field(name = SearchResource.Fields.LOCAL_DATE_FIELD, type = LocalDate.class),
                            @SearchParam.Field(name = SearchResource.Fields.LOCAL_DATE_TIME_FIELD, type = LocalDateTime.class),
                            @SearchParam.Field(name = SearchResource.Fields.INSTANT_FIELD, type = Instant.class),
                            @SearchParam.Field(name = SearchResource.Fields.OFFSET_DATE_TIME_FIELD, type = OffsetDateTime.class),
                            @SearchParam.Field(name = SearchResource.Fields.ENUM_FIELD, type = SearchEnum.class),
                            @SearchParam.Field(name = SearchResource.Fields.NESTED_SEARCH_FIELD)
                    },
                    defaultSearch = {
                            @SearchParam.DefaultField(name = SearchResource.Fields.OFFSET_DATE_TIME_FIELD, operator = SearchOperator.EQUAL, value = "2020-02-02T10:10:10+05:30")
                    }
            )
            final Search search
    ) {
        Search result = new Search(search);
        searchService.updateMappedField(result);
        return result;
    }
}

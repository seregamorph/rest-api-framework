package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.RESPONSE_CONTAINER_LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.services.SortService;
import com.seregamorph.restapi.sort.Sort;
import com.seregamorph.restapi.sort.SortDirection;
import com.seregamorph.restapi.sort.SortParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "sort")
@RestController
@RequestMapping(path = "/api/sort", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SortController {

    private final SortService sortService;

//    @ApiOperation(value = "Sort demo.", response = UserResource.class, responseContainer = RESPONSE_CONTAINER_LIST)
    @GetMapping
    public List<UserResource> get(
            @SortParam(
                    value = {
                            UserResource.FIELD_ID,
                            UserResource.Fields.NAME,
                            UserResource.Fields.AGE,
                            UserResource.Fields.ADDRESS,
                            UserResource.Fields.STATUS,
                            UserResource.Fields.GROUP_ID,
                            UserResource.Fields.GROUP_NAME,
                            UserResource.Fields.GROUP_DESC
                    },
                    defaultSort = {
                            @SortParam.DefaultField(value = UserResource.FIELD_ID, direction = SortDirection.ASC)
                    }
            )
            final Sort sort
    ) {
        return sortService.sort(sort);
    }
}

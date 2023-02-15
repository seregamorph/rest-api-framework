package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.RESPONSE_CONTAINER_LIST;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.FIRST;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.FOURTH;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.SECOND;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_ENUM_ARRAY;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_ENUM_LIST;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_SINGLE_ENUM;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_SINGLE_STRING;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_STRING_ARRAY;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_STRING_LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.demo.resources.AcceptEnum;
import com.seregamorph.restapi.demo.resources.AcceptResource;
import com.seregamorph.restapi.validators.Accept;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "accept")
@RestController
@RequestMapping(path = "/api/accept", produces = APPLICATION_JSON_VALUE)
public class AcceptController {

//    @ApiOperation(value = "Accept Demo (GET)", response = AcceptResource.class, responseContainer = RESPONSE_CONTAINER_LIST)
    @GetMapping
    public List<? extends BaseProjection> list(
            @RequestParam(value = PARAM_SINGLE_STRING, required = false)
            @Accept({FIRST, SECOND, FOURTH})
            final String singleString,

            @RequestParam(value = PARAM_STRING_ARRAY, required = false)
            @Accept({FIRST, SECOND, FOURTH})
            final String[] stringArray,

            @RequestParam(value = PARAM_STRING_LIST, required = false)
            @Accept({FIRST, SECOND, FOURTH})
            final List<String> stringList,

            @RequestParam(value = PARAM_SINGLE_ENUM, required = false)
            @Accept({FIRST, SECOND, FOURTH})
            final AcceptEnum singleEnum,

            @RequestParam(value = PARAM_ENUM_ARRAY, required = false)
            @Accept({FIRST, SECOND, FOURTH})
            final AcceptEnum[] enumArray,

            @RequestParam(value = PARAM_ENUM_LIST, required = false)
            @Accept({FIRST, SECOND, FOURTH})
            final List<AcceptEnum> enumList
    ) {
        return Collections.emptyList();
    }

//    @ApiOperation(value = "Accept Demo (POST)", response = AcceptResource.class)
    @PostMapping
    public AcceptResource post(
            @RequestBody
            @Valid
            final AcceptResource resource
    ) {
        return resource;
    }
}

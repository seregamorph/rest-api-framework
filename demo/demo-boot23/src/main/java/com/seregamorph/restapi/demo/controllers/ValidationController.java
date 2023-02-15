package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.resources.ValidationResource.MAX;
import static com.seregamorph.restapi.demo.resources.ValidationResource.MIN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.controllers.AbstractBaseRestController;
import com.seregamorph.restapi.demo.resources.ValidationResource;
import java.time.Instant;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "validation")
@RestController
@RequestMapping(path = "/api/validation", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ValidationController extends AbstractBaseRestController {

    static final String QUERY_PARAM_NUMBER = "number";
    static final String HEADER_NUMBER = "header-number";
    static final String METHOD_PARAM_HEADER_NUMBER = "headerNumber";
    static final String PARAM_TIMESTAMP = "timestamp";

//    @ApiOperation("Get something")
    @GetMapping
    public ValidationResource get(
            @Min(MIN)
            @Max(MAX)
            @RequestParam(QUERY_PARAM_NUMBER) int number,

            @Min(MIN)
            @Max(MAX)
            @RequestHeader(HEADER_NUMBER) int headerNumber,

            @RequestParam(PARAM_TIMESTAMP) Instant timestamp
    ) {
        return new ValidationResource()
                .setNumber(number)
                .setHeaderNumber(headerNumber)
                .setTimestamp(timestamp);
    }

//    @ApiOperation("Do something")
    @PostMapping
    @ResponseStatus(NO_CONTENT)
    public void create(
            @Valid
            @RequestBody ValidationResource resource
    ) {
        // Intentionally left blank
    }

}

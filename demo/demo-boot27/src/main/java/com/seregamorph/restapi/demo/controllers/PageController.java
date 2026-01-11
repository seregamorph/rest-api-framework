package com.seregamorph.restapi.demo.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.seregamorph.restapi.demo.resources.GroupResource;
import java.util.ArrayList;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
//@Api(tags = "page")
@RestController
@RequestMapping(path = "/api/pages", produces = APPLICATION_JSON_VALUE)
public class PageController {

    private static final int TOTAL_COUNT = 4;

//    @ApiOperation("Get paged resources")
    @GetMapping
    public Page<GroupResource> list(Pageable page) {
        int count = Math.min(TOTAL_COUNT, page.getPageSize());

        val content = new ArrayList<GroupResource>();
        for (int i = 0; i < count; i++) {
            content.add(new GroupResource()
                    .setId(page.getOffset() + i + 1));
        }
        return new PageImpl<>(content, page, TOTAL_COUNT);
    }

}

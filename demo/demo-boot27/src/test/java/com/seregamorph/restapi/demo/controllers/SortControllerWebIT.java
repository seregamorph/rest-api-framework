package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.utils.ResourceFactory.MAX_USER_ID;
import static com.seregamorph.restapi.demo.utils.ResourceFactory.MIN_USER_ID;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.ResultType.LIST;

import com.seregamorph.restapi.demo.resources.GroupResource;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.utils.ResourceFactory;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import org.springframework.test.web.servlet.ResultMatcher;

@InitTest(SortController.class)
public abstract class SortControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static GetAllSetup getAllSetup() {
        return new GetAllSetup()
                .setTotalElements((int) (MAX_USER_ID - MIN_USER_ID + 1))
                .setDefaultResultMatchers(defaultMatcher())
                .supportSortFields(UserResource.FIELD_ID,
                        UserResource.Fields.NAME,
                        UserResource.Fields.AGE,
                        UserResource.Fields.ADDRESS,
                        UserResource.Fields.STATUS,
                        UserResource.Fields.GROUP_ID,
                        UserResource.Fields.GROUP_NAME,
                        UserResource.Fields.GROUP_DESC)
                .setDefaultSortFields(UserResource.FIELD_ID)
                .mapField(UserResource.Fields.GROUP_ID, UserResource.Fields.GROUP, UserResource.FIELD_ID)
                .mapField(UserResource.Fields.GROUP_NAME, UserResource.Fields.GROUP, GroupResource.Fields.NAME)
                .mapField(UserResource.Fields.GROUP_DESC, UserResource.Fields.GROUP, GroupResource.Fields.DESC);
    }

    private static ResultMatcher defaultMatcher() {
        UserResource user = ResourceFactory.user(MIN_USER_ID);
        return LIST.matcherOf(jsonMatching(UserResource.class)
                .setId(user.getId())
                .setName(user.getName())
                .setAge(user.getAge())
                .setAddress(user.getAddress())
                .setStatus(user.getStatus())
                .setGroup(jsonMatching(GroupResource.class)
                        .setId(user.getGroup().getId())
                        .setName(user.getGroup().getName())
                        .setDesc(user.getGroup().getDesc())));
    }

}

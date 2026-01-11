package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.utils.ResourceFactory.MIN_USER_ID;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;

import com.seregamorph.restapi.demo.resources.GroupResource;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.utils.ResourceFactory;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;

@InitTest(ProjectionController.class)
public abstract class ProjectionControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static GetOneSetup getOneSetup() {
        return new GetOneSetup("")
                .setDefaultResultMatchers(defaultMatcher())
                .supportProjection(UserResource.Projection.DEFAULT, defaultMatcher())
                .supportProjection(UserResource.Projection.DETAIL, detailMatcher());
    }

    private static UserResource defaultMatcher() {
        UserResource user = ResourceFactory.user(MIN_USER_ID);
        return jsonMatching(UserResource.class)
                .setId(user.getId())
                .setName(user.getName())
                .setGroup(jsonMatching(GroupResource.class)
                        .setId(user.getGroup().getId())
                        .setName(user.getGroup().getName()));
    }

    private static UserResource detailMatcher() {
        UserResource user = ResourceFactory.user(MIN_USER_ID);
        return jsonMatching(UserResource.class)
                .setId(user.getId())
                .setName(user.getName())
                .setAge(user.getAge())
                .setAddress(user.getAddress())
                .setStatus(user.getStatus())
                .setGroup(jsonMatching(GroupResource.class)
                        .setId(user.getGroup().getId())
                        .setName(user.getGroup().getName())
                        .setDesc(user.getGroup().getDesc()));
    }
}

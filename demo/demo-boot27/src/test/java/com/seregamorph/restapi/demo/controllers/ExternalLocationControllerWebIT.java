package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.demo.controllers.PartialControllerWebIT.createdUser;
import static com.seregamorph.restapi.demo.controllers.PartialControllerWebIT.userMatcher;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.generic;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.required;

import com.seregamorph.restapi.demo.resources.GroupResource;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import lombok.val;

@InitTest(ExternalLocationController.class)
public abstract class ExternalLocationControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static PostSetup postSetup() {
        return new PostSetup()
                .setRequestPayload(genericPostPayload())
                .setDefaultResultMatchers(userMatcher(createdUser()))
                .setLocationHeaderAntPattern(PartialController.ENDPOINT + ENDPOINT_ID);
    }

    private static UserResource genericPostPayload() {
        val createdUser = createdUser();
        return generic(UserResource.class)
                .setName(required(createdUser.getName()))
                .setAge(createdUser.getAge())
                .setAddress(createdUser.getAddress())
                .setGroup(required(generic(GroupResource.class)
                        .setId(required(createdUser.getGroup().getId()))));
    }

}

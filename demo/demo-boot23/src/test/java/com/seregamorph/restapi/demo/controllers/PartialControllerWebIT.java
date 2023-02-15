package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.utils.ResourceFactory.MAX_GROUP_ID;
import static com.seregamorph.restapi.demo.utils.ResourceFactory.MAX_USER_ID;
import static com.seregamorph.restapi.demo.utils.ResourceFactory.MIN_USER_ID;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.generic;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.required;
import static com.seregamorph.restapi.test.base.support.RequestType.RETRIEVAL;

import com.seregamorph.restapi.demo.resources.GroupResource;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.resources.UserStatus;
import com.seregamorph.restapi.demo.utils.ResourceFactory;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.PatchSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;

@InitTest(PartialController.class)
public abstract class PartialControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static PostSetup postSetup() {
        return new PostSetup()
                .setRequestPayload(genericPostPayload())
                .setDefaultResultMatchers(userMatcher(createdUser()));
    }

    @InitTest
    public static PatchSetup patchSetup() {
        return new PatchSetup("")
                .setRequestType(RETRIEVAL)
                .setRequestPayload(genericPatchPayload())
                .setDefaultResultMatchers(userMatcher(updatedUser()));
    }

    static UserResource createdUser() {
        return new UserResource()
                .setId(MAX_USER_ID + 1)
                .setName("AnotherUser")
                .setAge(90)
                .setAddress("AnotherAddress")
                .setStatus(UserStatus.PENDING)
                .setGroup(ResourceFactory.group(MAX_GROUP_ID));
    }

    private static UserResource genericPostPayload() {
        UserResource createdUser = createdUser();
        return generic(UserResource.class)
                .setName(required(createdUser.getName()))
                .setAge(createdUser.getAge())
                .setAddress(createdUser.getAddress())
                .setGroup(required(generic(GroupResource.class)
                        .setId(required(createdUser.getGroup().getId()))));
    }

    private static UserResource updatedUser() {
        return new UserResource()
                .setId(MIN_USER_ID)
                .setName("AnotherUserEdited")
                .setAge(70)
                .setAddress("AnotherAddressEdited")
                .setStatus(UserStatus.ACTIVE)
                .setGroup(ResourceFactory.group(1));
    }

    private static UserResource genericPatchPayload() {
        UserResource updatedUser = updatedUser();
        return generic(UserResource.class)
                .setName(updatedUser.getName())
                .setAge(updatedUser.getAge())
                .setAddress(updatedUser.getAddress())
                .setStatus(updatedUser.getStatus())
                .setGroup(generic(GroupResource.class)
                        .setId(required(updatedUser.getGroup().getId())));
    }

    static UserResource userMatcher(UserResource user) {
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

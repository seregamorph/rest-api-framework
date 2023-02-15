package com.seregamorph.restapi.demo.utils;

import com.seregamorph.restapi.demo.resources.GroupResource;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.resources.UserStatus;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.Validate;

@UtilityClass
public class ResourceFactory {

    public static final long MIN_USER_ID = 1;
    public static final long MAX_USER_ID = 10;

    public static final long MIN_GROUP_ID = 1;
    public static final long MAX_GROUP_ID = 3;

    public static UserResource user(long id) {
        Validate.isTrue(id >= MIN_USER_ID);
        Validate.isTrue(id <= MAX_USER_ID + 1);
        return new UserResource()
                .setId(id)
                .setName("UserName" + id)
                .setAge((int) (50 + id))
                .setAddress("UserAddress" + id)
                .setStatus(UserStatus.ACTIVE)
                .setGroup(group(id % MAX_GROUP_ID + MIN_GROUP_ID));
    }

    public static GroupResource group(long id) {
        Validate.isTrue(id >= MIN_GROUP_ID);
        Validate.isTrue(id <= MAX_GROUP_ID);
        return new GroupResource()
                .setId(id)
                .setName("GroupName" + id)
                .setDesc("GroupDesc" + id);
    }
}

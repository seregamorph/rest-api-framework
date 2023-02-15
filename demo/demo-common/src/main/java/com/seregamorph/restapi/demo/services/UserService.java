package com.seregamorph.restapi.demo.services;

import static com.seregamorph.restapi.demo.utils.ResourceFactory.MAX_GROUP_ID;
import static com.seregamorph.restapi.demo.utils.ResourceFactory.MAX_USER_ID;
import static com.seregamorph.restapi.demo.utils.ResourceFactory.MIN_GROUP_ID;
import static com.seregamorph.restapi.demo.utils.ResourceFactory.MIN_USER_ID;

import com.seregamorph.restapi.demo.resources.GroupResource;
import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.resources.UserStatus;
import com.seregamorph.restapi.demo.utils.ResourceFactory;
import com.seregamorph.restapi.exceptions.ConflictException;
import com.seregamorph.restapi.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserResource create(UserResource user) {
        for (long i = MIN_USER_ID; i <= MAX_USER_ID; ++i) {
            UserResource existingUser = ResourceFactory.user(i);
            if (existingUser.getName().equalsIgnoreCase(user.getName())) {
                throw new ConflictException(UserResource.class, user.getName());
            }
        }
        UserResource newUser = new UserResource()
                .setId(MAX_USER_ID + 1)
                .setName(user.getName())
                .setStatus(UserStatus.PENDING)
                .setAge(user.getAge())
                .setAddress(user.getAddress());
        updateGroup(newUser, user.getGroup().getId());
        return newUser;
    }

    public UserResource update(UserResource user) {
        UserResource existingUser = ResourceFactory.user(MIN_USER_ID);
        if (user.hasPartialProperty(UserResource.Fields.NAME)) {
            existingUser.setName(user.getName());
        }
        if (user.hasPartialProperty(UserResource.Fields.AGE)) {
            existingUser.setAge(user.getAge());
        }
        if (user.hasPartialProperty(UserResource.Fields.ADDRESS)) {
            existingUser.setAddress(user.getAddress());
        }
        if (user.hasPartialProperty(UserResource.Fields.STATUS)) {
            existingUser.setStatus(user.getStatus());
        }
        if (user.hasPartialProperty(UserResource.Fields.GROUP)) {
            updateGroup(existingUser, user.getGroup().getId());
        }
        return existingUser;
    }

    private static void updateGroup(UserResource user, long groupId) {
        for (long i = MIN_GROUP_ID; i <= MAX_GROUP_ID; ++i) {
            if (ResourceFactory.group(i).getId() == groupId) {
                user.setGroup(ResourceFactory.group(i));
                return;
            }
        }
        throw new NotFoundException(GroupResource.class, groupId);
    }
}

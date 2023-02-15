package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.IdProjection;
import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.demo.resources.partial.UserPatchPartial;
import com.seregamorph.restapi.demo.resources.partial.UserPostPartial;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.UtilityClass;

@Data
@FieldNameConstants
public class UserResource extends IdResource<Long, UserResource> implements UserPostPartial, UserPatchPartial {

    @UtilityClass
    public static class Fields {

        public static final String GROUP_ID = "groupId";
        public static final String GROUP_NAME = "groupName";
        public static final String GROUP_DESC = "groupDesc";
    }

    private String name;
    private int age;
    private String address;
    private UserStatus status;
    private GroupResource group;

    @RequiredArgsConstructor
    @Getter
    public enum Projection implements ProjectionName {
        DEFAULT(NameProjection.class),
        DETAIL(DetailProjection.class);

        private final Class<? extends BaseProjection> projectionClass;
    }

    public interface NameProjection extends IdProjection {

        String getName();

        GroupResource.NameProjection getGroup();
    }

    public interface DetailProjection extends IdProjection {

        String getName();

        int getAge();

        String getAddress();

        UserStatus getStatus();

        GroupResource.DetailProjection getGroup();
    }
}

package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.IdProjection;
import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.base.ProjectionName;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class GroupResource extends IdResource<Long, GroupResource> {

    private String name;

    private String desc;

    @RequiredArgsConstructor
    @Getter
    public enum Projection implements ProjectionName {
        DEFAULT(DetailProjection.class),
        NAME(NameProjection.class);

        private final Class<? extends BaseProjection> projectionClass;
    }

    public interface DetailProjection extends IdProjection {

        String getName();

        String getDesc();
    }

    public interface NameProjection extends IdProjection {

        String getName();
    }
}

package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.IdProjection;
import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.base.ProjectionName;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class TeamResource extends IdResource<Long, TeamResource> {

    @NotEmpty
    private String name;

    private String description;

    @NotNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private PersonResource manager;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<PersonResource> members = new ArrayList<>();

    private Instant createdDate;

    private Instant lastModifiedDate;

    @RequiredArgsConstructor
    @Getter
    public enum Projection implements ProjectionName {
        DEFAULT(IdProjection.class),
        COMPACT(WithoutNestedProjection.class),
        FULL(WithNestedProjection.class);

        private final Class<? extends BaseProjection> projectionClass;
    }

    public interface WithoutNestedProjection extends IdProjection {

        String getName();

        String getDescription();
    }

    public interface WithNestedProjection extends IdProjection {

        String getName();

        String getDescription();

        PersonResource.WithoutNestedProjection getManager();

        List<PersonResource.WithoutNestedProjection> getMembers();

        Instant getCreatedDate();

        Instant getLastModifiedDate();
    }
}

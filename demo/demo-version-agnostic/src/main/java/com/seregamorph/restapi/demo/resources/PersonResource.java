package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.IdProjection;
import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.demo.resources.partial.PersonPatchPartial;
import com.seregamorph.restapi.demo.resources.partial.PersonPostPartial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class PersonResource extends IdResource<Long, PersonResource> implements PersonPostPartial, PersonPatchPartial {

    private String name;

    @Min(1900)
    private Integer yearOfBirth;

    @Email
    private String emailAddress;

    private Instant activationDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private TeamResource team;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<TeamResource> managedTeams = new ArrayList<>();

    private Instant createdDate;

    private Instant lastModifiedDate;

    @RequiredArgsConstructor
    @Getter
    public enum Projection implements ProjectionName {
        DEFAULT(IdProjection.class),
        COMPACT(WithoutNestedProjection.class),
        DETAIL(WithNestedProjection.class);

        private final Class<? extends IdProjection> projectionClass;
    }

    public interface WithoutNestedProjection extends IdProjection {

        String getName();

        Integer getYearOfBirth();

        String getEmailAddress();

        Instant getActivationDate();

        Instant getCreatedDate();

        Instant getLastModifiedDate();
    }

    public interface WithNestedProjection extends IdProjection {

        String getName();

        Integer getYearOfBirth();

        String getEmailAddress();

        Instant getActivationDate();

        TeamResource.WithoutNestedProjection getTeam();

        List<TeamResource.WithoutNestedProjection> getManagedTeams();

        Instant getCreatedDate();

        Instant getLastModifiedDate();
    }
}

package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.demo.resources.partial.CheckinPutPartial;
import com.seregamorph.restapi.partial.PartialResource;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class CheckinResource extends PartialResource implements CheckinPutPartial {

    private PersonResource person;

    private CheckinIdResource id;

    private String message;

    private Instant createdDate;

    private Instant lastModifiedDate;
}

package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.partial.PartialResource;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class CheckinIdResource extends PartialResource {

    private long personId;

    private LocalDate checkinDate;
}

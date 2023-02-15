package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.base.IdResource;
import java.time.Instant;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class ValidationResource extends IdResource<Integer, ValidationResource> {

    public static final int MIN = 13;
    public static final int MAX = 65;

    @Min(MIN)
    @Max(MAX)
    private int number;

    private int headerNumber;

    private Instant timestamp;
}

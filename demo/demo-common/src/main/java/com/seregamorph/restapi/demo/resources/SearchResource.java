package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.partial.PartialResource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class SearchResource extends PartialResource {

    private String stringField;
    private boolean primitiveBooleanField;
    private Boolean booleanField;
    private int primitiveIntField;
    private Integer integerField;
    private long primitiveLongField;
    private Long longField;
    private double primitiveDoubleField;
    private Double doubleField;
    private LocalDate localDateField;
    private LocalDateTime localDateTimeField;
    private Instant instantField;
    private OffsetDateTime offsetDateTimeField;
    private SearchEnum enumField;
    private NestedSearchResource nestedSearchField;
}

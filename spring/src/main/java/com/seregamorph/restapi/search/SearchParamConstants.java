package com.seregamorph.restapi.search;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SearchParamConstants {

    public static final List<Class<?>> DATA_TYPE_CLASSES = Collections.unmodifiableList(Arrays.asList(
            String.class,
            boolean.class,
            Boolean.class,
            int.class,
            Integer.class,
            long.class,
            Long.class,
            double.class,
            Double.class,
            LocalDate.class,
            LocalDateTime.class,
            Instant.class,
            OffsetDateTime.class,
            Enum.class
    ));
}

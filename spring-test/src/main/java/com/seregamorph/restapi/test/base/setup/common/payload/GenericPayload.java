package com.seregamorph.restapi.test.base.setup.common.payload;

import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import java.util.Iterator;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class GenericPayload extends AbstractStackTraceHolder {

    @Getter
    private final Class<?> resourceClass;

    public boolean hasRequiredFields() {
        return hasFields(FieldType.REQUIRED);
    }

    public boolean hasOptionalFields() {
        return hasFields(FieldType.OPTIONAL);
    }

    public boolean hasRedundantFields() {
        return hasFields(FieldType.REDUNDANT);
    }

    public abstract Object getDefaultPayload();

    public abstract Object getMinimalPayload();

    public abstract Iterable<GeneratedPayload> iterateRequiredFields();

    public abstract Iterable<GeneratedPayload> iterateOptionalFields();

    public abstract Iterable<GeneratedPayload> iterateRedundantFields();

    protected abstract boolean hasFields(FieldType fieldType);

    static <T> Iterable<T> iterable(Supplier<Iterator<T>> iteratorSupplier) {
        return iteratorSupplier::get;
    }
}

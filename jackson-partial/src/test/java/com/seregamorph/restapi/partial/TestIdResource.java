package com.seregamorph.restapi.partial;

import lombok.Data;

import java.io.Serializable;

@Data
@PayloadId(TestIdResource.FIELD_ID)
public abstract class TestIdResource<K extends Serializable, T extends TestIdResource<K, ?>>
        extends PartialResource implements TestIdPartial<K> {

    public static final String FIELD_ID = "id";

    private K id;

    @SuppressWarnings("unchecked")
    public T setId(K id) {
        this.id = id;
        return (T) this;
    }
}

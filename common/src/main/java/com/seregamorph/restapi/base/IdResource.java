package com.seregamorph.restapi.base;

import com.seregamorph.restapi.partial.PartialResource;
import com.seregamorph.restapi.partial.PayloadId;
import java.io.Serializable;
import lombok.Data;

@Data
@PayloadId(IdResource.FIELD_ID)
public abstract class IdResource<K extends Serializable, T extends IdResource<K, ?>>
        extends PartialResource implements IdPartial<K> {

    public static final String FIELD_ID = "id";

    private K id;

    @SuppressWarnings("unchecked")
    public T setId(K id) {
        this.id = id;
        return (T) this;
    }
}

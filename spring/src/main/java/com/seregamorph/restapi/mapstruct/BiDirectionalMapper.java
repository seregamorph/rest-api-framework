package com.seregamorph.restapi.mapstruct;

import com.seregamorph.restapi.base.BaseResource;

@SuppressWarnings("unused")
public interface BiDirectionalMapper<E, R extends BaseResource>
        extends EntityToResourceMapper<E, R>, ResourceToEntityMapper<R, E> {

}

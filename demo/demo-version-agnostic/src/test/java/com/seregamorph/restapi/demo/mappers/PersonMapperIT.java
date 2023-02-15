package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.mapstruct.CachedMappingContext;
import com.seregamorph.restapi.utils.RecursionPruner;

public class PersonMapperIT extends AbstractMapperIT {

    public PersonMapperIT() {
        super(PersonMapper.class);
    }

    @Override
    protected RecursionPruner getRecursionPruner() {
        return CachedMappingContext::defaultRecursionPruner;
    }
}

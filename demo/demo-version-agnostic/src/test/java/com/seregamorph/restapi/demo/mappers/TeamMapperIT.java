package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.mapstruct.CachedMappingContext;
import com.seregamorph.restapi.utils.RecursionPruner;

public class TeamMapperIT extends AbstractMapperIT {

    public TeamMapperIT() {
        super(TeamMapper.class);
    }

    @Override
    protected RecursionPruner getRecursionPruner() {
        return CachedMappingContext::defaultRecursionPruner;
    }
}

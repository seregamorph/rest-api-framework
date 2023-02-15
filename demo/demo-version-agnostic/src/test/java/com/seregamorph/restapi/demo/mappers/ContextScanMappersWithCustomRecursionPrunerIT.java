package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.mapstruct.CachedMappingContext;
import com.seregamorph.restapi.utils.RecursionPruner;

public class ContextScanMappersWithCustomRecursionPrunerIT extends AbstractMappersIT {

    @Override
    protected RecursionPruner getRecursionPruner() {
        return CachedMappingContext::defaultRecursionPruner;
    }
}

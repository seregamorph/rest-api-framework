package com.seregamorph.restapi.base;

import java.util.List;
import lombok.Data;

@Data
public class WrapperResource extends IdResource<Long, WrapperResource> {

    private List<NestedResource> nested;
}

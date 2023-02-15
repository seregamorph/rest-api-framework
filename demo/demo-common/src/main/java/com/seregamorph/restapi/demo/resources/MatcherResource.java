package com.seregamorph.restapi.demo.resources;

import com.google.common.collect.ImmutableMap;
import com.seregamorph.restapi.base.IdResource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class MatcherResource extends IdResource<Long, MatcherResource> {

    private int primitiveInt = 1;
    private Integer integer = 2;
    private int[] primitiveIntArray = {3};
    private Integer[] integerArray = {4};
    private List<Integer> integerList = Collections.singletonList(5);
    private Map<Integer, Integer> integerMap = ImmutableMap.of(6, 7);
    private byte[] bytes = MatcherResource.class.getSimpleName().getBytes();
}

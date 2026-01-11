package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;

import com.seregamorph.restapi.demo.resources.MatcherResource;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;

@InitTest(MatcherController.class)
public abstract class MatcherControllerWebIT extends AbstractBaseWebIT {

    @InitTest
    public static GetOneSetup getOneSetup() {
        return new GetOneSetup("")
                .setDefaultResultMatchers(defaultMatcher());
    }

    private static MatcherResource defaultMatcher() {
        MatcherResource resource = new MatcherResource();
        return jsonMatching(MatcherResource.class)
                .setId(resource.getId())
                .setPrimitiveInt(resource.getPrimitiveInt())
                .setInteger(resource.getInteger())
                .setPrimitiveIntArray(resource.getPrimitiveIntArray())
                .setIntegerArray(resource.getIntegerArray())
                .setIntegerList(resource.getIntegerList())
                .setIntegerMap(resource.getIntegerMap())
                .setBytes(resource.getBytes());
    }
}

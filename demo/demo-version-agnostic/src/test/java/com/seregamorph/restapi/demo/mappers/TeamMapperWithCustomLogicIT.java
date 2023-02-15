package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.demo.entities.Team;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamMapperWithCustomLogicIT extends AbstractMapperWithCustomLogicIT {

    @Autowired
    private TeamMapper teamMapper;

    @Test
    public void mapShouldHitInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(Team.class, teamMapper::map);
    }

    @Test
    public void lazyMapShouldHitInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(Team.class, teamMapper::lazyMap);
    }

    @Test
    public void pruningMapShouldNotHitInfiniteRecursion() throws Exception {
        infiniteRecursionDetector.detect(Team.class, teamMapper::pruningMap);
    }
}

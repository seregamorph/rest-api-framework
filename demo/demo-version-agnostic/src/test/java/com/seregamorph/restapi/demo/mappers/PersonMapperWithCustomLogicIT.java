package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.demo.entities.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonMapperWithCustomLogicIT extends AbstractMapperWithCustomLogicIT {

    @Autowired
    private PersonMapper personMapper;

    @Test
    public void mapShouldHitInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(Person.class, personMapper::map);
    }

    @Test
    public void lazyMapShouldHitInfiniteRecursion() throws Exception {
        shouldHitInfiniteRecursion(Person.class, personMapper::lazyMap);
    }

    @Test
    public void pruningMapShouldNotHitInfiniteRecursion() throws Exception {
        infiniteRecursionDetector.detect(Person.class, personMapper::pruningMap);
    }
}

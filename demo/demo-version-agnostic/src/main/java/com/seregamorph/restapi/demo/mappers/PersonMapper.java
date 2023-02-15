package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.demo.entities.Person;
import com.seregamorph.restapi.demo.resources.PersonResource;
import com.seregamorph.restapi.mapstruct.BiDirectionalMapper;
import org.mapstruct.Mapper;

@Mapper(uses = TeamMapper.class)
public interface PersonMapper extends BiDirectionalMapper<Person, PersonResource> {
}

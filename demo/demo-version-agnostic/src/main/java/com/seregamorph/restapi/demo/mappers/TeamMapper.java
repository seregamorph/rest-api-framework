package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.demo.entities.Team;
import com.seregamorph.restapi.demo.resources.TeamResource;
import com.seregamorph.restapi.mapstruct.BiDirectionalMapper;
import org.mapstruct.Mapper;

@Mapper(uses = PersonMapper.class)
public interface TeamMapper extends BiDirectionalMapper<Team, TeamResource> {
}

package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.demo.entities.Checkin;
import com.seregamorph.restapi.demo.resources.CheckinResource;
import com.seregamorph.restapi.mapstruct.BiDirectionalMapper;
import org.mapstruct.Mapper;

@Mapper(uses = {PersonMapper.class, CheckinIdMapper.class})
public interface CheckinMapper extends BiDirectionalMapper<Checkin, CheckinResource> {
}

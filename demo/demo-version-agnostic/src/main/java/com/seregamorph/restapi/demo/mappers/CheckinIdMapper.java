package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.demo.entities.CheckinId;
import com.seregamorph.restapi.demo.resources.CheckinIdResource;
import com.seregamorph.restapi.mapstruct.BiDirectionalMapper;
import org.mapstruct.Mapper;

@Mapper
public interface CheckinIdMapper extends BiDirectionalMapper<CheckinId, CheckinIdResource> {
}

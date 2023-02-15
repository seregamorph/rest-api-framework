package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.demo.entities.CheckinId;
import com.seregamorph.restapi.demo.resources.CheckinIdResource;
import com.seregamorph.restapi.mapstruct.BiDirectionalMapper;
import org.mapstruct.Mapper;

// There's no componentModel = 'none' (https://mapstruct.org/documentation/stable/reference/html/).
// This is a trick to get Mapstruct to generate mappers that can be retrieved using Mappers.getMapper(class)
// when the default componentModel has been configured to be non-default (e.g. spring).
@Mapper(componentModel = "none")
public interface CheckinIdWithDefaultComponentModelMapper extends BiDirectionalMapper<CheckinId, CheckinIdResource> {
}

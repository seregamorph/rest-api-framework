package com.seregamorph.restapi.base;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.seregamorph.restapi.partial.PartialPayloadUtils;

/**
 * Parent for any Partial interface.
 * We mark also {@link BasePartial} with {@link JsonFilter}, because we need it for feign clients that
 * define request entity as partial payload object.
 */
@JsonFilter(PartialPayloadUtils.FILTER_NAME)
public interface BasePartial {

}

package com.seregamorph.restapi.test.base;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Expected HTTP response status for invalid payload. Mostly it's 400 (by default), but in special cases
 * like missing referenced entities, may be 404.
 */
@RequiredArgsConstructor
public enum InvalidPayloadStatus {

    BAD_REQUEST(status().isBadRequest()),
    NOT_FOUND(status().isNotFound());

    @Getter
    private final ResultMatcher statusMatcher;

}

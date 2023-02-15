package com.seregamorph.restapi.test.base;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = PROTECTED)
@Getter
@ToString
public class MockMvcTestSetup {

    private final Class<?> controllerClass;
    private final String endpoint;
}

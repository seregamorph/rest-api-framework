package com.seregamorph.restapi.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class AtomicSearchClause {

    private final String field;
    private final SearchOperator operator;
    private final String value;
}

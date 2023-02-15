package com.seregamorph.restapi.test.base.setup.common;

import com.seregamorph.restapi.sort.SortDirection;
import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import lombok.Getter;

@Getter
public class VerifiableSortFieldDirection extends AbstractStackTraceHolder implements NamedExecution {

    private final VerifiableSortField sortField;
    private final SortDirection direction;

    public VerifiableSortFieldDirection(VerifiableSortField sortField, SortDirection direction) {
        this.sortField = sortField;
        this.direction = direction;
        setTrace(sortField.getTrace());
    }

    public VerifiableSortFieldDirection(String fieldName) {
        this(fieldName, SortDirection.ASC);
    }

    public VerifiableSortFieldDirection(String fieldName, SortDirection direction) {
        this(new VerifiableSortField(fieldName), direction);
    }

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return toString();
    }

    @Override
    public String toString() {
        return sortField.getFieldName() + ":" + direction;
    }

}

package com.seregamorph.restapi.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Search extends ArrayList<SearchCondition> {

    public Search(Collection<SearchCondition> conditions) {
        super(conditions);
    }

    public Search(SearchCondition... conditions) {
        this(Arrays.asList(conditions));
    }

    public Search(String field, SearchOperator operator, Object value) {
        this(new SingleSearchCondition(field, operator, value));
    }

    @SuppressWarnings("unused")
    public Search addSearchCondition(SearchCondition searchCondition) {
        super.add(searchCondition);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Search addSearchCondition(String field, SearchOperator operator, Object value) {
        super.add(new SingleSearchCondition(field, operator, value));
        return this;
    }

    @Override
    public String toString() {
        return this.stream()
                .map(Object::toString)
                .map(ArgumentWrappingHelper::wrapGroup)
                .collect(Collectors.joining(LogicalOperator.AND.getSecondaryOperator()));
    }
}

package com.seregamorph.restapi.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class SearchConditionGroup implements SearchCondition {

    private final List<SearchCondition> searchConditions = new ArrayList<>();

    private final LogicalOperator logicalOperator;

    public SearchConditionGroup addSearchCondition(SearchCondition searchCondition) {
        this.searchConditions.add(searchCondition);
        return this;
    }

    public SearchConditionGroup addSearchCondition(String field, SearchOperator operator, Object value) {
        this.searchConditions.add(new SingleSearchCondition(field, operator, value));
        return this;
    }

    @Override
    public String toString() {
        if (searchConditions.size() == 1) {
            return searchConditions.get(0).toString();
        }

        String separator = logicalOperator.getPrimaryOperator();

        return searchConditions.stream()
                .map(Object::toString)
                .map(ArgumentWrappingHelper::wrapGroup)
                .collect(Collectors.joining(separator));
    }
}

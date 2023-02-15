package com.seregamorph.restapi.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class SearchClause {

    // A search clause may be atomic or composite.
    // If it's atomic, there's only 1 element and the logical operator doesn't matter.
    // If it's composite, there may be more than 1 element and the logical operator decides the relationship among them.

    private final List<Object> clauses = new ArrayList<>();

    private final LogicalOperator logicalOperator;

    public SearchClause(AtomicSearchClause atomicClause) {
        this(LogicalOperator.OR);
        add(atomicClause);
    }

    public SearchClause(String field, SearchOperator operator, String value) {
        this(new AtomicSearchClause(field, operator, value));
    }

    public SearchClause(LogicalOperator logicalOperator, AtomicSearchClause... atomicClauses) {
        this(logicalOperator);
        add(atomicClauses);
    }

    public SearchClause add(AtomicSearchClause... atomicClauses) {
        this.clauses.addAll(Arrays.asList(atomicClauses));
        return this;
    }

    public SearchClause add(String field, SearchOperator operator, String value) {
        return add(new AtomicSearchClause(field, operator, value));
    }

    public SearchClause add(SearchClause... subCompositeClauses) {
        this.clauses.addAll(Arrays.asList(subCompositeClauses));
        return this;
    }

    public SearchClause cleanup() {
        SearchClause result = new SearchClause(logicalOperator);

        for (Object clause : clauses) {
            if (clause instanceof SearchClause) {
                SearchClause subCompositeClause = ((SearchClause) clause).cleanup();
                if (logicalOperator == subCompositeClause.logicalOperator || subCompositeClause.clauses.size() == 1) {
                    result.clauses.addAll(subCompositeClause.clauses);
                } else if (!subCompositeClause.clauses.isEmpty()) {
                    result.clauses.add(subCompositeClause);
                }
            } else {
                result.clauses.add(clause);
            }
        }

        if (result.clauses.size() == 1 && result.clauses.get(0) instanceof SearchClause) {
            return (SearchClause) result.clauses.get(0);
        }

        return result;
    }
}

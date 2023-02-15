package com.seregamorph.restapi.test.base.setup.common;

import static com.seregamorph.restapi.sort.SortDirection.DESC;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.nullsLast;

import com.seregamorph.restapi.sort.SortDirection;
import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import java.util.Comparator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class VerifiableSortField extends AbstractStackTraceHolder {

    @Getter
    private final String fieldName;
    /**
     * Ascending comparator. In case of descending order it is reversed.
     */
    @SuppressWarnings("rawtypes")
    private final Comparator ascComparator;
    /**
     * Position for nulls in ascending order:
     * true - nulls go first (last for descending), false - vice versa
     */
    private final boolean ascNullsFirst;

    public VerifiableSortField(String fieldName, Comparator<?> ascComparator) {
        this(fieldName, ascComparator, true);
    }

    public VerifiableSortField(String fieldName) {
        this(fieldName, naturalOrder());
    }

    public VerifiableSortFieldDirection with(SortDirection direction) {
        return new VerifiableSortFieldDirection(this, direction);
    }

    @SuppressWarnings("unchecked")
    public Comparator<Object> getComparator(SortDirection direction) {
        val comparator = ascNullsFirst ? nullsFirst(this.ascComparator) : nullsLast(this.ascComparator);
        return direction == DESC ? comparator.reversed() : comparator;
    }

    @Override
    public String toString() {
        return "(\"" + fieldName + "\", "
                + (ascNullsFirst ? "ascNullsFirst" : "ascNullsLast") + ")";
    }
}

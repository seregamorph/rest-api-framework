package com.seregamorph.restapi.utils;

import java.util.Date;
import java.util.List;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@SuppressWarnings("unused")
class TestClassWithDifferentDataTypes {

    static final String METHOD_RETURN_DATE = "methodReturnDate";
    static final String METHOD_RETURN_COLLECTION = "methodReturnCollection";
    static final String METHOD_RETURN_DATE_COLLECTION = "methodReturnDateCollection";
    static final String METHOD_RETURN_DATE_ARRAY = "methodReturnDateArray";
    static final String METHOD_RETURN_LOWER_BOUND_DATE_COLLECTION = "methodReturnLowerBoundDateCollection";
    static final String METHOD_WITH_PARAMS = "methodWithParams";

    private Date dateField;
    private List<?> collectionField;
    private List<Date> dateCollectionField;
    private Date[] dateArrayField;

    public Date methodReturnDate() {
        return null;
    }

    public List<?> methodReturnCollection() {
        return null;
    }

    public List<Date> methodReturnDateCollection() {
        return null;
    }

    public Date[] methodReturnDateArray() {
        return null;
    }

    public List<? extends Date> methodReturnLowerBoundDateCollection() {
        return null;
    }

    public void methodWithParams(Date dateParam, List<?> collectionParam, List<Date> dateCollectionParam, Date[] dateArrayParam) {
        // Intentionally left blank
    }
}

package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.common.Constants.PARAM_SEARCH;
import static com.seregamorph.restapi.search.SearchValueConverter.convertMultipleObjects;
import static com.seregamorph.restapi.search.SearchValueConverter.convertSingleObject;

import com.seregamorph.restapi.config.spi.FrameworkConfigHolder;
import com.seregamorph.restapi.exceptions.TypeMismatchExceptions;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

@UtilityClass
class SearchConditionConverter {

    static SearchCondition convertToSearchCondition(SearchParam param, String originalValue, SearchClause searchClause) {
        if (CollectionUtils.isEmpty(searchClause.getClauses())) {
            throw TypeMismatchExceptions.create(Search.class,
                    PARAM_SEARCH, originalValue, "Format: <fieldName><operator><value>");
        }

        SearchConditionGroup searchConditionGroup = new SearchConditionGroup(searchClause.getLogicalOperator());

        for (Object element : searchClause.getClauses()) {
            if (element instanceof SearchClause) {
                searchConditionGroup.addSearchCondition(convertToSearchCondition(param, originalValue, (SearchClause) element));
            } else {
                searchConditionGroup.addSearchCondition(convertToSingleSearchCondition(param, originalValue, (AtomicSearchClause) element));
            }
        }

        return cleanup(searchConditionGroup);
    }

    private static SingleSearchCondition convertToSingleSearchCondition(SearchParam param, String originalValue, AtomicSearchClause atomicSearchClause) {
        SearchParam.Field field = SearchParamUtils.findField(param, atomicSearchClause.getField());

        if (field == null && param.value().length > 0) {
            throw TypeMismatchExceptions.create(Search.class,
                    PARAM_SEARCH, originalValue, String.format("Field [%s] is not searchable.", atomicSearchClause.getField()));
        }

        if (!FrameworkConfigHolder.getFrameworkConfig().getSupportedSearchOperators().contains(atomicSearchClause.getOperator())) {
            throw TypeMismatchExceptions.create(Search.class,
                    PARAM_SEARCH, originalValue, String.format("Operator [%s] is not supported", atomicSearchClause.getOperator().getOperator()));
        }

        Class<?> type = field == null ? String.class : field.type();
        Object value;

        try {
            value = atomicSearchClause.getOperator().isMultipleValueSupported()
                    ? convertMultipleObjects(type, atomicSearchClause.getOperator().isSpecialValueSupported(), atomicSearchClause.getValue())
                    : convertSingleObject(type, atomicSearchClause.getOperator().isSpecialValueSupported(), atomicSearchClause.getValue());
        } catch (Exception e) {
            // We intentionally catch all exceptions here
            throw TypeMismatchExceptions.create(Search.class,
                    PARAM_SEARCH, originalValue, String.format("Value conversion hit an error: [%s].", e.getMessage()), e);
        }

        if (value instanceof SearchValue
                && !FrameworkConfigHolder.getFrameworkConfig().getSupportedSpecialSearchValues().contains(value)) {
            throw TypeMismatchExceptions.create(Search.class,
                    PARAM_SEARCH, originalValue, String.format("Special value [%s] is not supported", atomicSearchClause.getValue()));
        }

        return new SingleSearchCondition(atomicSearchClause.getField(), atomicSearchClause.getOperator(), value);
    }

    private static SearchCondition cleanup(SearchConditionGroup searchConditionGroup) {
        if (searchConditionGroup.getSearchConditions().size() == 1) {
            SearchCondition searchCondition = searchConditionGroup.getSearchConditions().get(0);

            if (searchCondition instanceof SingleSearchCondition) {
                return searchCondition;
            }

            return cleanup((SearchConditionGroup) searchCondition);
        }

        SearchConditionGroup result = new SearchConditionGroup(searchConditionGroup.getLogicalOperator());

        for (Object searchCondition : searchConditionGroup.getSearchConditions()) {
            if (searchCondition instanceof SingleSearchCondition) {
                result.addSearchCondition((SingleSearchCondition) searchCondition);
            } else {
                result.addSearchCondition(cleanup((SearchConditionGroup) searchCondition));
            }
        }

        return result;
    }
}

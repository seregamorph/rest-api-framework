package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.SearchConditionConverter.convertToSearchCondition;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class SearchArgumentResolver {

    public static Search resolveArgument(SearchParam searchParam, String[] searchValues) {
        Search search = new Search();

        if (searchValues != null) {
            for (String searchValue : searchValues) {
                if (StringUtils.isBlank(searchValue)) {
                    continue;
                }

                SearchCondition searchCondition = convertToSearchCondition(searchParam, searchValue, SearchArgumentParser.parse(searchValue));

                if (searchCondition instanceof SearchConditionGroup) {
                    SearchConditionGroup searchConditionGroup = (SearchConditionGroup) searchCondition;

                    if (searchConditionGroup.getLogicalOperator() == LogicalOperator.AND) {
                        // Avoid AND nested in AND - bring them out!
                        search.addAll(searchConditionGroup.getSearchConditions());
                        continue;
                    }
                }

                search.add(searchCondition);
            }
        }

        if (search.isEmpty()) {
            for (SearchParam.DefaultField defaultField : searchParam.defaultSearch()) {
                SearchParam.Field field = SearchParamUtils.findField(searchParam, defaultField.name());

                if (field == null) {
                    throw new IllegalStateException(
                            String.format("Default field [%s] doesn't exist.", defaultField.name()));
                }

                Object value;

                if (defaultField.operator().isMultipleValueSupported()) {
                    value = SearchValueConverter.convertMultipleObjects(
                            field.type(),
                            defaultField.operator().isSpecialValueSupported(),
                            defaultField.value()
                    );
                } else {
                    value = SearchValueConverter.convertSingleObject(
                            field.type(),
                            defaultField.operator().isSpecialValueSupported(),
                            defaultField.value()[0]
                    );
                }

                search.addSearchCondition(defaultField.name(), defaultField.operator(), value);
            }
        }

        return search;
    }
}

package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.ArgumentWrappingHelper.wrapGroup;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
class SearchArgumentGenerator {

    static List<String> generate(SearchConditionGroup searchCondition) {
        List<List<String>> nestedArguments = new ArrayList<>();

        searchCondition.getSearchConditions().forEach(condition -> {
            if (condition instanceof SearchConditionGroup) {
                nestedArguments.add(generate((SearchConditionGroup) condition));
            } else {
                String conditionString = condition.toString();

                List<String> conditionStrings = new ArrayList<>();
                conditionStrings.add(" " + conditionString + " ");

                String wrappedClause = wrapGroup(conditionString);
                conditionStrings.add(wrappedClause);
                conditionStrings.add(" " + wrappedClause + " ");

                nestedArguments.add(conditionStrings);
            }
        });

        List<String> results = new ArrayList<>();

        if (searchCondition.getLogicalOperator() == LogicalOperator.AND) {
            results.addAll(generate(";", nestedArguments));
            results.addAll(generate("and", nestedArguments));
            results.addAll(generate("AND", nestedArguments));
        } else {
            results.addAll(generate("|", nestedArguments));
            results.addAll(generate("or", nestedArguments));
            results.addAll(generate("OR", nestedArguments));
        }

        return results;
    }

    private static List<String> generate(String separator, List<List<String>> nestedArguments) {
        List<String> results = new ArrayList<>();
        generate(results, "", separator, nestedArguments, 0);
        return results;
    }

    private static void generate(List<String> results, String inProgressResult, String separator, List<List<String>> nestedArguments, int currentRow) {
        if (currentRow == nestedArguments.size() - 1) {
            for (int currentColumn = 0; currentColumn < nestedArguments.get(currentRow).size(); ++currentColumn) {
                results.add(wrapGroup(inProgressResult + separator + nestedArguments.get(currentRow).get(currentColumn)));
            }
        } else {
            for (int currentColumn = 0; currentColumn < nestedArguments.get(currentRow).size(); ++currentColumn) {
                String nextInProgressResult = StringUtils.isEmpty(inProgressResult)
                        ? nestedArguments.get(currentRow).get(currentColumn)
                        : inProgressResult + separator + nestedArguments.get(currentRow).get(currentColumn);
                generate(results, nextInProgressResult, separator, nestedArguments, currentRow + 1);
            }
        }
    }
}

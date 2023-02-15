package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.MoreMatchers.softOrdered;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortFieldDirection;
import com.seregamorph.restapi.test.base.support.FieldMappingSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.junit.Assert;
import org.springframework.test.web.servlet.ResultMatcher;

@UtilityClass
class BaseResultMatchers {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Json matcher that evaluates missing json path as null (not skipping).
     */
    static ResultMatcher jsonPathNullMissing(FieldMappingSupport<?> fieldMappingSupport,
                                             List<VerifiableSortFieldDirection> verifiableSortFieldDirections) {
        return result -> {
            val content = result.getResponse().getContentAsString();
            BaseResultMatchers.match(content, fieldMappingSupport, verifiableSortFieldDirections);
        };
    }

    static void match(String content, FieldMappingSupport<?> fieldMappingSupport,
                      List<VerifiableSortFieldDirection> verifiableSortFieldDirections) {
        Map<Integer, Map<String, Object>> leafs = new TreeMap<>();
        Comparator<Map<String, Object>> comparator = null;
        val expressions = new ArrayList<String>();
        for (val sortField : verifiableSortFieldDirections) {
            val fieldName = sortField.getSortField().getFieldName();
            val expression = fieldMappingSupport.getJsonPath(fieldName);
            expressions.add(expression);
            val list = evaluateJsonPath(expression, content);

            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                leafs.computeIfAbsent(i, k -> new TreeMap<>())
                        .put(fieldName, element);
            }

            Comparator<Map<String, Object>> thenComparing = Comparator.comparing(
                    byFieldName -> byFieldName.get(fieldName),
                    sortField.getSortField().getComparator(sortField.getDirection())
            );

            comparator = comparator == null ? thenComparing : comparator.thenComparing(thenComparing);
        }

        leafs.forEach((idx, leaf) -> Assert.assertEquals("For record [" + idx + "] there is not enough "
                + "mapped paths, mapped only " + leaf, verifiableSortFieldDirections.size(), leaf.size()));

        if (comparator != null) {
            assertThat("JSON path " + expressions, leafs.values(),
                    softOrdered(comparator, verifiableSortFieldDirections.toString()));
        }
    }

    static List<?> evaluateJsonPath(String expression, String content) {
        assertThat(expression, startsWith("$"));
        String[] tokens = expression.substring(1).split("\\[\\*\\]");
        if (tokens.length != 2) {
            throw new AssertionError("Unexpected expression, should have format " +
                    "container[*]field, e.g. `$[*].sub.element.id`");
        }
        val prefix = expressionToPointer(tokens[0]);
        val suffix = expressionToPointer(tokens[1]);

        try {
            val root = objectMapper.readTree(content);
            val prefixNode = root.at(prefix);
            if (prefixNode.isMissingNode()) {
                throw new AssertionError("Missing container node `" + prefix + "`");
            }
            if (!prefixNode.isArray()) {
                throw new AssertionError("Node `" + prefix + "` is not an array node");
            }

            val list = new ArrayList<Object>();
            val allMissing = new AtomicBoolean(true);
            prefixNode.forEach(element -> {
                val leaf = element.at(suffix);
                if (leaf.isMissingNode()) {
                    list.add(null);
                } else {
                    allMissing.set(false);
                    if (!leaf.isValueNode()) {
                        throw new AssertionError("Unexpected node at `" + suffix + "` is not a value for " + element);
                    } else if (leaf instanceof NumericNode) {
                        list.add(leaf.numberValue());
                    } else {
                        list.add(leaf.textValue());
                    }
                }
            });
            if (!list.isEmpty() && allMissing.get()) {
                throw new AssertionError("All elements at path `" + expression + "` are missing");
            }
            return list;
        } catch (IOException e) {
            throw new AssertionError("No value at JSON path \"" + expression + "\", exception: " + e.getMessage(), e);
        }
    }

    private static JsonPointer expressionToPointer(String expression) {
        return JsonPointer.compile(expression.replace(".", "/"));
    }
}

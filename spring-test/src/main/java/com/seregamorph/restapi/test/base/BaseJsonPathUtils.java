package com.seregamorph.restapi.test.base;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.seregamorph.restapi.partial.PayloadId;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

@UtilityClass
class BaseJsonPathUtils {

    static void removeIgnoredNodes(JsonNode root, Collection<String> ignoredJsonPaths) {
        for (val jsonPath : ignoredJsonPaths) {
            assertThat(jsonPath, startsWith("$"));
            String[] tokens = jsonPath.substring(1).split("\\[\\*\\]");

            removeIgnoredNodes(root, tokens, 0);
        }
    }

    private static void removeIgnoredNodes(JsonNode node, String[] tokens, int level) {
        val jsonPtr = tokens[level].replace(".", "/");

        if (level < tokens.length - 1) {
            val container = node.at(jsonPtr);
            if (container instanceof ArrayNode) {
                container.forEach(child -> removeIgnoredNodes(child, tokens, level + 1));
            } else if (!container.isMissingNode()) {
                throw new IllegalStateException("Should be a missing or array node");
            }
        } else {
            int separator = StringUtils.lastIndexOf(jsonPtr, '/');
            val path = jsonPtr.substring(0, separator);
            val field = jsonPtr.substring(separator + 1);
            val container = node.at(path);
            if (container instanceof ObjectNode) {
                ((ObjectNode) container).remove(field);
            }
        }
    }

    /**
     * Merges updateNode onto targetNode replacing only existing fields. In case if updateNode has @PayloadId-marked
     * fields, all neighbour fields on the same objects are returned as ignored.
     *
     * @param targetNode existing node to update
     * @param updateNode PATCH data
     * @param payloadType resource type
     * @return list of ignored json paths
     */
    static List<String> mergeExistingFields(JsonNode targetNode, JsonNode updateNode, Class<?> payloadType) {
        val ignoredJsonPaths = new ArrayList<String>();
        mergeExistingFields(targetNode, updateNode, payloadType, ignoredJsonPaths, new Stack<>());
        return ignoredJsonPaths;
    }

    private static void mergeExistingFields(
            JsonNode targetNode, JsonNode updateNode, Class<?> payloadType,
            List<String> ignoredJsonPaths, Stack<String> fieldStack) {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode node = targetNode.path(fieldName);
            if (node.isMissingNode()) {
                continue;
            }
            val field = FieldUtils.getField(payloadType, fieldName, true);
            if (field == null) {
                throw new IllegalStateException("Missing field `" + fieldName + "` at path " + fieldStack
                        + " on " + payloadType);
            }
            // if field exists and is an embedded object
            if (node.isObject()) {
                fieldStack.push(fieldName);
                mergeExistingFields(node, updateNode.get(fieldName), field.getType(), ignoredJsonPaths, fieldStack);
                fieldStack.pop();
            } else if (targetNode instanceof ObjectNode) {
                // Overwrite field
                JsonNode value = updateNode.get(fieldName);
                ((ObjectNode) targetNode).set(fieldName, value);

                val typePayloadId = payloadType.getAnnotation(PayloadId.class);
                if (field.getAnnotation(PayloadId.class) != null
                        || typePayloadId != null && Arrays.asList(typePayloadId.value()).contains(field.getName())) {
                    // it's a PayloadId field - all other fields in this type should be ignored in comparison
                    for (val payloadField : FieldUtils.getAllFields(payloadType)) {
                        fieldStack.push(payloadField.getName());
                        if (!payloadField.getName().equals(field.getName())
                                && !Modifier.isStatic(payloadField.getModifiers())
                                && payloadField.getAnnotation(JsonIgnore.class) == null) {
                            val path = "$." + String.join(".", fieldStack);
                            ignoredJsonPaths.add(path);
                        }
                        fieldStack.pop();
                    }
                }
            }
        }
    }
}

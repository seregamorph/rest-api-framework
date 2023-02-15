package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.JsonConstants;
import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * To map from a field (search field, sort field) to its json path in the response payload.
 * If such mapping is not available for a specific field, then the system considers that
 * the field exists as-is in the response payload directly from the root node.
 * @param <S> parent.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor
public class FieldMappingSupport<S extends BaseSetup<S, ?>> {

    private final Map<String, String> fields = new HashMap<>();

    private final S parent;

    public S mapField(String field, String... jsonPathElements) {
        this.fields.put(field, String.join(".", jsonPathElements));
        return this.parent;
    }

    // Use this method to get the json path for a field based on the result type.
    // The json path is supposed to be used to extract *ALL* matching elements from the response payload.
    public String getJsonPath(String field) {
        val resultType = parent.getResultType();
        return getJsonPath(field, resultType);
    }

    private String getJsonPath(String field, ResultType resultType) {
        String jsonPath = fields.getOrDefault(field, field);

        switch (resultType) {
            case LIST:
                return JsonConstants.ROOT + "[*]." + jsonPath;
            case PAGE:
                return JsonConstants.ROOT_CONTENT + "[*]." + jsonPath;
            default:
                return JsonConstants.ROOT + "." + jsonPath;
        }
    }
}

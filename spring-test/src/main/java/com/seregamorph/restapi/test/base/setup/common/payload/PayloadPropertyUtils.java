package com.seregamorph.restapi.test.base.setup.common.payload;

import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
class PayloadPropertyUtils {

    /**
     * Checks if the property contains any descendant being the specified target property.
     */
    static boolean containsDescendant(PayloadProperty property, PayloadProperty targetProperty) {
        // Do NOT check the property itself
        if (property.getValue() instanceof GenericSinglePayload) {
            GenericSinglePayload nestedPayload = (GenericSinglePayload) property.getValue();
            if (contains(nestedPayload.getProperties(), targetProperty)) {
                return true;
            }
        }

        if (property.getValue() instanceof GenericArrayPayload) {
            GenericArrayPayload nestedPayloads = (GenericArrayPayload) property.getValue();

            for (List<PayloadProperty> nestedPayload : nestedPayloads.getAllProperties()) {
                if (contains(nestedPayload, targetProperty)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the payload directly or indirectly contains any property being the target property.
     */
    private static boolean contains(List<PayloadProperty> payload, PayloadProperty targetProperty) {
        for (PayloadProperty property : payload) {
            if (property == targetProperty) {
                return true;
            }

            if (property.getValue() instanceof GenericSinglePayload) {
                GenericSinglePayload nestedPayload = (GenericSinglePayload) property.getValue();
                if (contains(nestedPayload.getProperties(), targetProperty)) {
                    return true;
                }
            }

            if (property.getValue() instanceof GenericArrayPayload) {
                GenericArrayPayload nestedPayloads = (GenericArrayPayload) property.getValue();

                for (List<PayloadProperty> nestedPayload : nestedPayloads.getAllProperties()) {
                    if (contains(nestedPayload, targetProperty)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

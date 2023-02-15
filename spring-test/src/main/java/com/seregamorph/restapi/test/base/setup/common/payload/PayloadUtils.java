package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.utils.StandardValues.jsonObject;
import static com.seregamorph.restapi.test.utils.StandardValues.jsonObjects;

import com.seregamorph.restapi.utils.ObjectUtils;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class PayloadUtils {

    public static Object payload(Object value) {
        Object payloadValue = ObjectUtils.singleOrCollection(value);

        if (payloadValue instanceof Collection) {
            val collection = (Collection<?>) payloadValue;
            val objects = collection.stream()
                    .map(PayloadUtils::payload)
                    .collect(Collectors.toList());
            if (!objects.isEmpty() && objects.get(0) instanceof GenericSinglePayload) {
                val allProperties = objects.stream()
                        .map(object -> (GenericSinglePayload) object)
                        .map(GenericSinglePayload::getProperties)
                        .collect(Collectors.toList());
                return new GenericArrayPayload(((GenericSinglePayload) objects.get(0))
                        .getResourceClass(), allProperties);
            }
            return objects;
        }

        return payloadInternally(payloadValue);
    }

    private static Object payloadInternally(Object value) {
        if (GenericPayloads.isGenericPayloadProxy(value)) {
            return GenericPayloads.genericPayloadOf(value);
        } else if (value instanceof Object[]) {
            return jsonObjects((Object[]) value);
        } else {
            return jsonObject(value);
        }
    }
}

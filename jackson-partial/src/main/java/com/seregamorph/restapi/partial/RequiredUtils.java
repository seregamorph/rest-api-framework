package com.seregamorph.restapi.partial;

import com.seregamorph.restapi.utils.ObjectUtils;
import java.util.Collection;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
class RequiredUtils {

    static boolean isRequiredValueInvalid(Object value) {
        if (value == null) {
            return true;
        }

        Object requiredValue = ObjectUtils.singleOrCollection(value);

        if (requiredValue instanceof Collection) {
            val collection = (Collection<?>) requiredValue;

            if (collection.isEmpty()) {
                return true;
            }

            for (Object element : collection) {
                if (isRequiredValueInvalid(element)) {
                    return true;
                }
            }
        }

        return false;
    }
}

package com.seregamorph.restapi.sort;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
class SortParamUtils {

    static boolean hasField(SortParam param, String fieldName) {
        for (String field : param.value()) {
            if (StringUtils.equals(fieldName, field)) {
                return true;
            }
        }

        return false;
    }
}

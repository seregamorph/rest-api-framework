package com.seregamorph.restapi.search;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
class SearchParamUtils {

    static boolean isDataTypeSupported(Class<?> dataType) {
        if (SearchParamConstants.DATA_TYPE_CLASSES.contains(dataType)) {
            return true;
        }

        if (Enum.class.isAssignableFrom(dataType)) {
            for (Class<?> supportedDataType : SearchParamConstants.DATA_TYPE_CLASSES) {
                if (Enum.class.isAssignableFrom(supportedDataType)) {
                    return true;
                }
            }
        }

        return false;
    }

    static SearchParam.Field findField(SearchParam param, String fieldName) {
        for (SearchParam.Field field : param.value()) {
            if (StringUtils.equals(fieldName, field.name())) {
                return field;
            }
        }

        return null;
    }
}

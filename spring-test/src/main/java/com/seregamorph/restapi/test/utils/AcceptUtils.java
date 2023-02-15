package com.seregamorph.restapi.test.utils;

import static com.seregamorph.restapi.test.utils.StandardValues.strings;

import com.seregamorph.restapi.validators.Accept;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class AcceptUtils {

    public static List<String> extractIllegalValues(Accept accept, Class<? extends Enum> enumClass) {
        String[] names = strings(enumClass.getEnumConstants());

        if (names.length == 0) {
            return Collections.emptyList();
        }

        List<String> values = Arrays.asList(names);
        List<String> illegalValues = new ArrayList<>();

        for (String value : accept.value()) {
            if (values.stream().noneMatch(element -> StringUtils.equals(element, value))) {
                illegalValues.add(value);
            }
        }

        return illegalValues;
    }
}

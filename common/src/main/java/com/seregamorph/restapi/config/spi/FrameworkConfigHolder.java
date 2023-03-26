package com.seregamorph.restapi.config.spi;

import com.seregamorph.restapi.search.SearchOperator;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.CollectionUtils;

import java.util.ServiceLoader;

@UtilityClass
public class FrameworkConfigHolder {

    @Getter
    private static final FrameworkConfig frameworkConfig = loadFrameworkConfig();

    private static FrameworkConfig loadFrameworkConfig() {
        val loader = ServiceLoader.load(FrameworkConfig.class, FrameworkConfigHolder.class.getClassLoader());

        if (loader.iterator().hasNext()) {
            FrameworkConfig config = loader.iterator().next();
            validate(config);
            return config;
        } else {
            return new FrameworkConfig() {};
        }
    }

    private static void validate(FrameworkConfig config) {
        boolean hasOperatorSupportingSpecialValues = false;

        if (!CollectionUtils.isEmpty(config.getSupportedSearchOperators())) {
            for (SearchOperator operator : config.getSupportedSearchOperators()) {
                if (operator.isSpecialValueSupported()) {
                    hasOperatorSupportingSpecialValues = true;
                    break;
                }
            }
        }

        boolean hasSpecialValues = !CollectionUtils.isEmpty(config.getSupportedSpecialSearchValues());

        if (hasOperatorSupportingSpecialValues != hasSpecialValues) {
            throw new IllegalStateException("Bad Rest API Framework configuration: If special values are supported, "
                    + "there must be at least 1 operator supporting special values and vice-versa.");
        }
    }
}

package com.seregamorph.restapi.validators;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import javax.validation.ConstraintValidatorContext;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@UtilityClass
class AcceptValidationUtils {

    private static final String DELIMITER = ", ";
    private static final String MESSAGE_TEMPLATE = "Invalid value %s. Acceptable values: %s";

    static <T> boolean isValueValid(T value,
                                    Function<T, String> extractor,
                                    String[] acceptableValues,
                                    String defaultMessage,
                                    ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (value == null) {
            return true;
        }

        boolean isValid = validate(extractor.apply(value), acceptableValues);

        if (!isValid) {
            String errorMessage = StringUtils.isBlank(defaultMessage)
                    ? getErrorMessage(extractor.apply(value), acceptableValues)
                    : defaultMessage;
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
        }

        return isValid;
    }

    static <T> boolean isArrayValid(T[] values,
                                    Function<T, String> extractor,
                                    String[] acceptableValues,
                                    String defaultMessage,
                                    ConstraintValidatorContext context) {
        Collection<T> collection = values == null ? null : Arrays.asList(values);
        return isCollectionValid(collection, extractor, acceptableValues, defaultMessage, context);
    }

    static <T> boolean isCollectionValid(Collection<? extends T> values,
                                         Function<T, String> extractor,
                                         String[] acceptableValues,
                                         String defaultMessage,
                                         ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (CollectionUtils.isEmpty(values)) {
            return true;
        }

        boolean isValid = true;

        for (T value : values) {
            isValid = validate(extractor.apply(value), acceptableValues);

            if (!isValid) {
                String errorMessage = StringUtils.isBlank(defaultMessage)
                        ? getErrorMessage(extractor.apply(value), acceptableValues)
                        : defaultMessage;
                context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
                break;
            }
        }

        return isValid;
    }

    private static boolean validate(String value, String[] acceptableValues) {
        for (String acceptableValue : acceptableValues) {
            if (acceptableValue.equals(value)) {
                return true;
            }
        }

        return false;
    }

    private static String getErrorMessage(String value, String[] acceptableValues) {
        return String.format(MESSAGE_TEMPLATE, value, String.join(DELIMITER, acceptableValues));
    }
}

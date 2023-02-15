package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.partial.RequiredUtils.isRequiredValueInvalid;
import static com.seregamorph.restapi.utils.ClassUtils.getFieldValue;

import com.seregamorph.restapi.utils.ClassUtils;
import com.seregamorph.restapi.utils.ObjectUtils;
import com.seregamorph.restapi.utils.TypeUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.CollectionUtils;

@UtilityClass
public class PartialPayloadUtils {

    /**
     * see PartialPayloadPropertyFilter
     * see PartialPayloadModule
     */
    public static final String FILTER_NAME = "partialPayloadPropertyFilter";

    public static void validate(PartialPayload payload) {
        Validate.notNull(payload);

        validateNoRedundantFields(payload);
        validateNoMissingRequiredFields(payload);

        validateNested(payload);
    }

    private static void validateNested(PartialPayload partialPayload) {
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils.extractPropertyDescriptors(
                partialPayload.getPayloadClass());

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Class<?> fieldClass = TypeUtils.extractElementClass(propertyDescriptor.getReadMethod());
            if (!PartialPayload.class.isAssignableFrom(fieldClass)) {
                continue;
            }

            Object value = ObjectUtils.singleOrCollection(
                    getFieldValue(partialPayload, propertyDescriptor.getReadMethod()));

            if (value instanceof PartialPayload) {
                validate((PartialPayload) value);
            } else if (value instanceof Collection) {
                for (Object element : (Collection<?>) value) {
                    if (element instanceof PartialPayload) {
                        validate((PartialPayload) element);
                    }
                }
            }
        }
    }

    private static void validateNoRedundantFields(PartialPayload payload) {
        List<String> redundantFieldNames = new ArrayList<>();
        Set<Map.Entry<String, Object>> entries = payload.getPartialProperties().entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            Field field = FieldUtils.getField(payload.getPayloadClass(), entry.getKey(), true);

            if (field == null) {
                redundantFieldNames.add(entry.getKey());
            }
        }

        if (!CollectionUtils.isEmpty(redundantFieldNames)) {
            throw new RedundantFieldsException(payload.getPayloadClass(), null, redundantFieldNames);
        }
    }

    private static void validateNoMissingRequiredFields(PartialPayload payload) {
        List<String> requiredFieldNames = new ArrayList<>();

        FieldUtils.getFieldsListWithAnnotation(payload.getPayloadClass(), Required.class).forEach(field -> {
            if (isRequiredValueInvalid(payload.getPartialProperties().get(field.getName()))) {
                requiredFieldNames.add(field.getName());
            }
        });

        if (!CollectionUtils.isEmpty(requiredFieldNames)) {
            throw new RequiredFieldsException(payload.getPayloadClass(), null, requiredFieldNames);
        }
    }
}

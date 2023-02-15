package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.partial.RequiredUtils.isRequiredValueInvalid;
import static com.seregamorph.restapi.utils.ClassUtils.extractPropertyDescriptors;
import static com.seregamorph.restapi.utils.ClassUtils.getFieldValue;

import com.seregamorph.restapi.base.BasePartial;
import com.seregamorph.restapi.utils.ObjectUtils;
import com.seregamorph.restapi.utils.TypeUtils;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

@UtilityClass
@Slf4j
public class PartialResourceUtils {

    private static final String NOT_AN_INSTANCE = "[%s] is not an instance of [%s]";

    /**
     * Validate that the properties in the object are compliant with the interface.
     *
     * @param partialResource  the object
     * @param partialInterface the interface
     */
    public static void validate(PartialResource partialResource, Class<?> partialInterface) {
        Validate.notNull(partialResource);
        Validate.notNull(partialInterface);
        Validate.isTrue(partialInterface.isInterface(), "%s is not an interface", partialInterface.getName());
        Validate.isTrue(partialInterface.isInstance(partialResource),
                NOT_AN_INSTANCE, partialResource.getPayloadClass().getName(), partialInterface.getName());

        Collection<PropertyDescriptor> sourceDescriptors = extractPropertyDescriptors(partialInterface);
        validateNoRedundantFields(partialResource, partialInterface, sourceDescriptors);
        validateRequiredFields(partialResource, partialInterface, sourceDescriptors);

        validateNested(partialResource, partialInterface);
    }

    private static void validateNested(PartialResource partialResource, Class<?> partialInterface) {
        Collection<PropertyDescriptor> propertyDescriptors = extractPropertyDescriptors(partialInterface);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Class<?> fieldClass = TypeUtils.extractElementClass(propertyDescriptor.getReadMethod());
            if (!BasePartial.class.isAssignableFrom(fieldClass)) {
                continue;
            }

            Object value = ObjectUtils.singleOrCollection(
                    getFieldValue(partialResource, propertyDescriptor.getReadMethod()));

            if (value instanceof PartialResource) {
                validate((PartialResource) value, fieldClass);
            } else if (value instanceof Collection) {
                for (Object element : (Collection<?>) value) {
                    if (element instanceof PartialResource) {
                        validate((PartialResource) element, fieldClass);
                    }
                }
            }
        }
    }

    private static void validateNoRedundantFields(PartialResource partialResource, Class<?> partialInterface,
                                                  Collection<PropertyDescriptor> sourceDescriptors) {
        Set<String> updatedFields = new HashSet<>(partialResource.getPartialProperties().keySet());

        sourceDescriptors
                .stream()
                .map(PropertyDescriptor::getName)
                .forEach(updatedFields::remove);

        if (!updatedFields.isEmpty()) {
            throw new RedundantFieldsException(partialResource.getPayloadClass(), partialInterface, updatedFields);
        }
    }

    private static void validateRequiredFields(PartialResource partialResource, Class<?> partialInterface,
                                               Collection<PropertyDescriptor> sourceDescriptors) {
        List<String> invalidFields = new ArrayList<>();

        for (PropertyDescriptor sourceDescriptor : sourceDescriptors) {
            if (sourceDescriptor.getReadMethod().getAnnotation(Required.class) == null) {
                continue;
            }

            Object value = partialResource.getPartialProperties().get(sourceDescriptor.getName());

            if (isRequiredValueInvalid(value)) {
                invalidFields.add(sourceDescriptor.getName());
            }
        }

        if (!invalidFields.isEmpty()) {
            throw new RequiredFieldsException(partialResource.getPayloadClass(), partialInterface, invalidFields);
        }
    }
}

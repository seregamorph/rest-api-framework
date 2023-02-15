package com.seregamorph.restapi.test.base.setup.common.payload;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

@UtilityClass
public class GenericPayloadEnhancer {

    /**
     * Enhance existing payload to add redundant fields. Notice that the original payload is not affected. In the case
     * a payload (at root level or a nested one) already contains at least 1 redundant field, no enhancement is done.
     *
     * @param genericPayload the payload to enhance.
     * @return a copy of the payload with additional redundant fields, or an exact copy in the case no redundant
     * fields are added.
     */
    public static GenericPayload enhance(GenericPayload genericPayload) {
        if (genericPayload instanceof GenericSinglePayload) {
            List<PayloadProperty> properties = enhance(genericPayload.getResourceClass(),
                    ((GenericSinglePayload) genericPayload).getProperties());
            return new GenericSinglePayload(genericPayload.getResourceClass(), properties);
        }

        if (genericPayload instanceof GenericArrayPayload) {
            List<List<PayloadProperty>> allProperties = enhanceAll(genericPayload.getResourceClass(),
                    ((GenericArrayPayload) genericPayload).getAllProperties());
            return new GenericArrayPayload(genericPayload.getResourceClass(), allProperties);
        }

        return genericPayload;
    }

    private static List<PayloadProperty> enhance(Class<?> resourceClass, List<PayloadProperty> properties) {
        return enhanceInternally(resourceClass, BeanUtils.getPropertyDescriptors(resourceClass), properties);
    }

    private static List<List<PayloadProperty>> enhanceAll(
            Class<?> resourceClass, List<List<PayloadProperty>> allProperties) {
        List<List<PayloadProperty>> allEnhancedProperties = new ArrayList<>();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(resourceClass);

        for (List<PayloadProperty> properties : allProperties) {
            allEnhancedProperties.add(enhanceInternally(resourceClass, propertyDescriptors, properties));
        }

        return allEnhancedProperties;
    }

    private static List<PayloadProperty> enhanceInternally(
            Class<?> resourceClass, PropertyDescriptor[] propertyDescriptors, List<PayloadProperty> properties) {
        List<PayloadProperty> enhancedProperties = new ArrayList<>();
        boolean hasRedundantFields = false;

        for (PayloadProperty property : properties) {
            if (property.getValue() instanceof GenericPayload) {
                enhancedProperties.add(new PayloadProperty(property.getResourceClass(), property.getFieldName(),
                        property.getFieldType(), enhance((GenericPayload) property.getValue())));
            } else {
                enhancedProperties.add(property);
            }

            if (property.getFieldType() == FieldType.REDUNDANT) {
                hasRedundantFields = true;
            }
        }

        if (hasRedundantFields) {
            return enhancedProperties;
        }

        enhancedProperties.add(new PayloadProperty(
                resourceClass, "redundantFieldFor" + resourceClass.getSimpleName(), FieldType.REDUNDANT, null));

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getReadMethod() == null
                    || propertyDescriptor.getWriteMethod() == null
                    || hasField(properties, propertyDescriptor.getName())) {
                continue;
            }

            enhancedProperties.add(
                    new PayloadProperty(resourceClass, propertyDescriptor.getName(), FieldType.REDUNDANT, null));
        }

        return enhancedProperties;
    }

    private static boolean hasField(List<PayloadProperty> properties, String fieldName) {
        for (PayloadProperty property : properties) {
            if (StringUtils.equals(fieldName, property.getFieldName())) {
                return true;
            }
        }

        return false;
    }
}

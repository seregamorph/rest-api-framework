package com.seregamorph.restapi.utils;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.StringUtils;

@UtilityClass
public class ClassUtils {

    static final String FAILED_TO_LOOKUP_PROPERTY = "Failed to lookup %s for property [%s]";

    static final String MISSING_GETTER_FOR_PROPERTY = "Missing getter for property [%s] in class [%s]";
    static final String MISSING_SETTER_FOR_PROPERTY = "Missing setter for property [%s] in class [%s]";

    /**
     * Extracts property descriptors from the specified class and its ancestor classes / interfaces. A property is
     * extracted if (1) its read method is not defined in <code>Object.class</code> (2) there's no previously extracted
     * property having the same name (remember that we go from the specified class upwards). Example: If a public method
     * hasn't been overridden in the chain, then it's extracted, otherwise it's not.
     *
     * @param clazz The class to extract - could be a concrete class, an abstract class, or an interface.
     * @return a non empty collection of {@link PropertyDescriptor}.
     */
    public static Collection<PropertyDescriptor> extractPropertyDescriptors(Class<?> clazz) {
        Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
        extractPropertyDescriptorsInternally(propertyDescriptorMap, clazz);
        return propertyDescriptorMap.values();
    }

    public static Field extractField(Class<?> clazz, String fieldName) {
        // Do NOT use FieldUtils.getDeclaredField(entityInstance.getClass(), entityFieldName, true) here
        // The class we are dealing with may have been proxied by the framework, and FieldUtils wouldn't be able to
        // find the declared field, even with forceAccess = true.
        return getField(clazz, fieldName, true);
    }

    public static <T> T getFieldValue(Object object, String field) {
        val descriptor = notNull(getPropertyDescriptor(object.getClass(), field),
                FAILED_TO_LOOKUP_PROPERTY, object.getClass(), field);
        val reader = notNull(descriptor.getReadMethod(),
                MISSING_GETTER_FOR_PROPERTY, field, object.getClass());
        return getFieldValue(object, reader);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object, Method reader) {
        reader.setAccessible(true);
        return (T) reader.invoke(object);
    }

    @SneakyThrows
    public static void setFieldValue(Object object, String field, Object value) {
        val descriptor = notNull(getPropertyDescriptor(object.getClass(), field),
                FAILED_TO_LOOKUP_PROPERTY, object.getClass(), field);
        Method writer = descriptor.getWriteMethod();
        if (writer == null) {
            String withMethod = "with" + StringUtils.capitalize(descriptor.getName());
            writer = Arrays.stream(object.getClass().getMethods())
                    .filter(m -> m.getName().equals(withMethod)
                            && m.getParameterCount() == 1)
                    .findFirst()
                    .orElse(null);
        }
        isTrue(writer != null && writer.getParameterCount() == 1,
                MISSING_SETTER_FOR_PROPERTY, field, object.getClass());
        writer.setAccessible(true);
        Object result = writer.invoke(object, value);
        // This method is to alter the state of the object. Setter must either return void, or return the same instance.
        isTrue(writer.getReturnType() == void.class || result == object,
                "%s.%s setter method should either be void or return this object (chained)",
                object.getClass().getName(), field);
    }

    /**
     * Gets a static field value directly without using a getter.
     * @param clazz the class.
     * @param fieldName the field name.
     * @param <T> the expected type.
     * @return the field value.
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T getStaticFieldValueDirectly(Class<?> clazz, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(null);
    }

    /**
     * Sets a static field value directly without using a setter.
     * @param clazz the class.
     * @param fieldName the field name.
     * @param value the value.
     */
    @SneakyThrows
    public static void setStaticFieldValueDirectly(Class<?> clazz, String fieldName, Object value) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static void extractPropertyDescriptorsInternally(
            Map<String, PropertyDescriptor> propertyDescriptorMap, Class<?> clazz) {
        if (clazz == Object.class || clazz == null) {
            return;
        }

        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getReadMethod() != null
                    && propertyDescriptor.getReadMethod().getDeclaringClass() != Object.class
                    && !propertyDescriptorMap.containsKey(propertyDescriptor.getName())) {
                propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
        }

        extractPropertyDescriptorsInternally(propertyDescriptorMap, clazz.getSuperclass());

        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            extractPropertyDescriptorsInternally(propertyDescriptorMap, interfaceClass);
        }
    }
}

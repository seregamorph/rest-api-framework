package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.mapstruct.Renamed;
import com.seregamorph.restapi.utils.ClassUtils;
import com.seregamorph.restapi.utils.TypeUtils;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.*;

import static com.seregamorph.restapi.utils.ClassUtils.getFieldValue;
import static com.seregamorph.restapi.utils.ClassUtils.setFieldValue;
import static lombok.AccessLevel.PACKAGE;

/**
 * Base class to support partial Jackson nodes serialization and deserialization.
 *
 * See {@link com.seregamorph.restapi.partial.PartialPayloadFactory},
 * {@link com.seregamorph.restapi.partial.PartialPayloadDeserializer}
 */
// For lombok to work properly, use hard coded values instead of static final ones
@EqualsAndHashCode(doNotUseGetters = true, exclude = {"payloadClass", "partialProperties"})
@FieldNameConstants(level = PACKAGE)
@JsonFilter(PartialPayloadUtils.FILTER_NAME)
public abstract class PartialPayload implements BasePayload {

    @JsonIgnore
    @Setter(PACKAGE)
    private Class<?> payloadClass;

    @JsonIgnore
    private Map<String, Object> partialProperties;

    Class<?> getPayloadClass() {
        return payloadClass == null ? this.getClass() : payloadClass;
    }

    void setPartialProperties(Map<String, Object> properties) {
        partialProperties = new HashMap<>(properties);
    }

    void setPartialProperty(String property, Object value) {
        partialProperties.put(property, value);
    }

    Map<String, Object> getPartialProperties() {
        requirePartialProperties();
        return Collections.unmodifiableMap(partialProperties);
    }

    /**
     * Is this instance initialized via PartialPayloadFactory or parsed via prepared ObjectMapper
     * (see PartialPayloadDeserializer)
     *
     * @return true if partial properties set is defined for instance, otherwise false (e.g. if instance created
     * with default constructor)
     */
    @JsonIgnore
    public boolean isPartialPropertiesInitialized() {
        return partialProperties != null;
    }

    public boolean hasPartialProperty(String property) {
        requirePartialProperties();
        return partialProperties.containsKey(property);
    }

    /**
     * Extract the actual payload - an instance of the same type with only properties that exist in
     * {@link #partialProperties}.
     *
     * @return the actual payload.
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T extractPayload() {
        Object p = getPayloadClass().newInstance();
        copy(this, p);
        return (T) p;
    }

    public void copyTo(Object target) {
        copy(this, target);
    }

    private void requirePartialProperties() {
        if (partialProperties == null) {
            throw new IllegalStateException("Partial properties not defined for " + getClass().getSimpleName() + ". "
                    + "Please check if your ObjectMapper is configured correctly (either with JacksonBootConfig or "
                    + "PartialPayloadMapperUtils.configure), or create PartialPayload instance via "
                    + "PartialPayloadFactory.partial(Class), not with default constructor.");
        }
    }

    /**
     * Copy partial properties (both actual data and metadata) from the source to the target.
     *
     * @param source the source
     * @param target the target
     */
    @SuppressWarnings("unchecked")
    private static void copy(PartialPayload source, Object target) {
        Validate.notNull(source);
        Validate.notNull(target);

        Map<String, Object> properties = source.getPartialProperties();

        if (target instanceof PartialPayload) {
            ((PartialPayload) target).setPartialProperties(properties);
        }

        Set<Map.Entry<String, Object>> entries = properties.entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            Field sourceField = FieldUtils.getField(source.getPayloadClass(), entry.getKey(), true);

            if (sourceField == null) {
                continue;
            }

            PayloadId payloadId = sourceField.getAnnotation(PayloadId.class);

            if (payloadId == null) {
                payloadId = TypeUtils.extractElementClass(sourceField).getAnnotation(PayloadId.class);
            }

            String targetFieldName = entry.getKey();

            if (!(target instanceof PartialPayload)) {
                Renamed renamed = sourceField.getAnnotation(Renamed.class);

                if (renamed != null) {
                    targetFieldName = renamed.value();
                }
            }

            Object sourceValue = entry.getValue();
            Object targetValue = getFieldValue(target, targetFieldName);

            if (sourceValue instanceof PartialPayload) {
                // Only copy fields if the IDs are the same
                // If target value is null, or target value is not null but has a different ID, init new object instead
                if (targetValue != null && shouldCopy(payloadId, sourceValue, targetValue)) {
                    copy((PartialPayload) sourceValue, targetValue);
                } else {
                    Field targetField = FieldUtils.getField(target.getClass(), targetFieldName, true);
                    targetValue = BeanUtils.instantiateClass(targetField.getType());
                    copy((PartialPayload) sourceValue, targetValue);
                    setFieldValue(target, targetFieldName, targetValue);
                }
            } else if (sourceValue instanceof Collection && targetValue instanceof Collection) {
                copyCollection(payloadId, (Collection<Object>) sourceValue, (Collection<Object>) targetValue);
            } else {
                setFieldValue(target, targetFieldName, sourceValue);
            }
        }
    }

    private static void copyCollection(PayloadId payloadId, Collection<Object> source, Collection<Object> target) {
        // Replace the elements, but not the collection itself. This ensures that, if the target collection is a
        // JPA-managed collection, then after all elements have been replaced, it's still a managed collection.
        Collection<Object> backupCollection = new ArrayList<>(target);
        target.clear();

        for (Object sourceElement : source) {
            if (sourceElement instanceof PartialPayload) {
                Object targetElement = backupCollection.stream()
                        .filter(element -> shouldCopy(payloadId, sourceElement, element))
                        .findFirst()
                        .orElse(null);
                if (targetElement != null) {
                    target.add(targetElement);
                    copy((PartialPayload) sourceElement, targetElement);
                    continue;
                }
            }

            target.add(sourceElement);
        }
    }

    private static boolean shouldCopy(PayloadId payloadId, Object first, Object second) {
        if (payloadId == null || payloadId.value().length == 0) {
            return first instanceof PartialPayload;
        }

        for (String field : payloadId.value()) {
            Object firstValue = ClassUtils.getFieldValue(first, field);
            Object secondValue = ClassUtils.getFieldValue(second, field);

            if (!Objects.equals(firstValue, secondValue)) {
                return false;
            }
        }

        return true;
    }
}

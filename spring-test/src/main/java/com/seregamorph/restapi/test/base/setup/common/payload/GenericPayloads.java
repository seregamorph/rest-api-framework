package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.OPTIONAL;
import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.REDUNDANT;
import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.REQUIRED;

import com.seregamorph.restapi.partial.PartialPayload;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GenericPayloads {

    private static final GenericPayloadProxyFactory factory = new GenericPayloadProxyFactory();

    /**
     * Create an instance of payload class that intercepts setters calls to fill required/optional/redundant fields
     * for a payload.
     *
     * @see #required(Object)
     * @see #redundant(Object)
     */
    public static <T extends PartialPayload> T generic(Class<T> payloadType) {
        return factory.create(payloadType);
    }

    public static <V> V required(V value) {
        GenericPayloadProxyFactory.setCurrentField(REQUIRED, value);
        return value;
    }

    // note: optional by default

    public static <V> V redundant(V value) {
        GenericPayloadProxyFactory.setCurrentField(REDUNDANT, value);
        return value;
    }

    public static <V> V requiredString(String value) {
        GenericPayloadProxyFactory.setCurrentField(REQUIRED, value);
        return null;
    }

    public static <V> V optionalString(String value) {
        GenericPayloadProxyFactory.setCurrentField(OPTIONAL, value);
        return null;
    }

    public static <V> V redundantString(String value) {
        GenericPayloadProxyFactory.setCurrentField(REDUNDANT, value);
        return null;
    }

    public static boolean isGenericPayloadProxy(@Nullable Object object) {
        return GenericPayloadProxyFactory.isGenericPayloadProxy(object);
    }

    public static void clearCurrentField() {
        GenericPayloadProxyFactory.clearCurrentField();
    }

    public static GenericSinglePayload genericPayloadOf(Object proxy) {
        return GenericPayloadProxyFactory.getGenericPayload(proxy);
    }

    public static GenericSinglePayload object(Class<?> resourceClass) {
        return new GenericSinglePayload(resourceClass);
    }

    public static GenericArrayPayloadElement object() {
        return new GenericArrayPayloadElement();
    }

    public static GenericArrayPayload array(Class<?> resourceClass) {
        return new GenericArrayPayload(resourceClass);
    }
}

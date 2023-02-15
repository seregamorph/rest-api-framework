package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.OPTIONAL;

import com.seregamorph.restapi.utils.AbstractProxyFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;

class GenericPayloadProxyFactory extends AbstractProxyFactory {

    @RequiredArgsConstructor
    private static class CurrentField {

        /**
         * Keep stack trace information to make detailed diagnostics.
         */
        private final StackTraceElement[] trace = new Throwable().getStackTrace();

        private final FieldType type;
        private final Object value;
    }

    private static final ThreadLocal<CurrentField> currentField = new ThreadLocal<>();

    @Override
    public <T> T create(Class<T> superClass) {
        assertEmptyCurrentField();

        return super.create(superClass);
    }

    static void setCurrentField(FieldType type, Object value) {
        assertEmptyCurrentField();
        currentField.set(new CurrentField(type, value));
    }

    static void clearCurrentField() {
        try {
            assertEmptyCurrentField();
        } finally {
            currentField.remove();
        }
    }

    @Nonnull
    static GenericSinglePayload getGenericPayload(Object proxy) {
        if (!(isGenericPayloadProxy(proxy))) {
            throw new IllegalStateException("Object should be a proxy, created by GenericPayloads.generic(Class)");
        }
        return ((GenericPayloadProxyMethodHandler) ((ProxyObject) proxy).getHandler()).payload;
    }

    static boolean isGenericPayloadProxy(@Nullable Object object) {
        val methodHandler = tryGetMethodHandler(object);
        return methodHandler instanceof GenericPayloadProxyMethodHandler;
    }

    @Override
    protected MethodHandler getMethodHandler(Class<?> superClass) {
        return new GenericPayloadProxyMethodHandler(superClass);
    }

    @Override
    protected Object handleSetter(Object self, Method thisMethod, Method proceed, Object[] args)
            throws InvocationTargetException, IllegalAccessException {
        Object result = super.handleSetter(self, thisMethod, proceed, args);

        val methodHandler = (GenericPayloadProxyMethodHandler) ((ProxyObject) self).getHandler();
        val fieldName = extractFieldName(thisMethod);

        val field = currentField.get();
        if (field == null) {
            assert args.length == 1;
            methodHandler.payload.field(fieldName, OPTIONAL, args[0]);
        } else {
            currentField.remove();
            methodHandler.payload.field(fieldName, field.type, field.value);
        }

        return result;
    }

    @Override
    protected String handleToString(Object self) {
        val methodHandler = (GenericPayloadProxyMethodHandler) ((ProxyObject) self).getHandler();
        return "GenericPayloadProxy<" + self.getClass().getSuperclass().getName() + ">("
                + methodHandler.payload.getProperties().stream()
                .map(property -> property.getFieldType().name().toLowerCase() + " " + property.getFieldName())
                .collect(Collectors.joining(", "))
                + ")";
    }

    private static void assertEmptyCurrentField() {
        if (currentField.get() != null) {
            val exception = new IllegalStateException("Wrong GenericPayload call state: previous field "
                    + "was not consumed by setter (check attached suppressed exception)");
            val error = new AssertionError();
            error.setStackTrace(currentField.get().trace);
            exception.addSuppressed(error);
            throw exception;
        }
    }

    private class GenericPayloadProxyMethodHandler implements MethodHandler {

        private final GenericSinglePayload payload;

        private GenericPayloadProxyMethodHandler(Class<?> payloadType) {
            payload = new GenericSinglePayload(payloadType);
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
                throws InvocationTargetException, IllegalAccessException {
            return handle(self, thisMethod, proceed, args);
        }
    }
}

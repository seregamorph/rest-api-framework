package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.utils.AbstractProxyFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class PartialPayloadFactory {

    private static final PartialPayloadProxyFactory factory = new PartialPayloadProxyFactory();

    /**
     * Returns an instance of payload class, that intercepts each setter call to serialize only explicitly set
     * values (supports null values).
     *
     * @see PartialPayloadMapperUtils#configure(ObjectMapper)
     */
    public static <T extends PartialPayload> T partial(Class<T> payloadClass) {
        return factory.create(payloadClass);
    }

    private static class PartialPayloadProxyFactory extends AbstractProxyFactory {

        @Override
        protected <T> void initProxyInstance(Class<T> superClass, T proxyInstance) {
            val partial = (PartialPayload) proxyInstance;
            partial.setPayloadClass(superClass);
            partial.setPartialProperties(Collections.emptyMap());
        }

        @Override
        protected Object handleSetter(Object self, Method thisMethod, Method proceed, Object[] args)
                throws InvocationTargetException, IllegalAccessException {
            Object result = super.handleSetter(self, thisMethod, proceed, args);
            val partial = (PartialPayload) self;
            partial.setPartialProperty(extractFieldName(thisMethod), args[0]);
            return result;
        }

        @Override
        protected String handleToString(Object self) {
            val partial = (PartialPayload) self;
            return "Partial<" + partial.getPayloadClass().getSimpleName() + ">"
                    + partial.getPartialProperties();
        }
    }
}

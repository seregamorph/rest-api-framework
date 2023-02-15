package com.seregamorph.restapi.utils;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import javax.annotation.Nullable;
import lombok.SneakyThrows;
import lombok.val;

public abstract class AbstractProxyFactory {

    private static final String[] prefixes = {"set", "with"};

    private final ConcurrentMap<Class<?>, Class<?>> classCache = new ConcurrentHashMap<>();

    @SneakyThrows
    public <T> T create(Class<T> superClass) {
        val proxyClass = classCache.computeIfAbsent(superClass, this::createProxyClass);

        T proxyInstance = superClass.cast(proxyClass.newInstance());
        // note: call it before setHandler
        initProxyInstance(superClass, proxyInstance);
        ((ProxyObject) proxyInstance).setHandler(getMethodHandler(superClass));
        return proxyInstance;
    }

    protected MethodHandler getMethodHandler(Class<?> superClass) {
        return this::handle;
    }

    protected Object handle(Object self, Method thisMethod, Method proceed, Object[] args)
            throws IllegalAccessException, InvocationTargetException {
        if (isSetter(thisMethod)) {
            return handleSetter(self, thisMethod, proceed, args);
        }
        assert isToString(thisMethod);
        return handleToString(self);
    }

    protected Object handleSetter(Object self, Method thisMethod, Method proceed, Object[] args)
            throws InvocationTargetException, IllegalAccessException {
        Object result = proceed.invoke(self, args);
        assert self == result || thisMethod.getReturnType() == void.class : "Setter should be void or return self object";
        return result;
    }

    protected abstract String handleToString(Object self);

    @Nullable
    protected static MethodHandler tryGetMethodHandler(@Nullable Object object) {
        if (!(object instanceof ProxyObject)) {
            return null;
        }
        return ((ProxyObject) object).getHandler();
    }

    protected <T> void initProxyInstance(Class<T> superClass, T proxyInstance) {
        // no op by default
    }

    protected static String extractFieldName(Method method) {
        for (String prefix : prefixes) {
            if (method.getName().startsWith(prefix)) {
                assert Character.isUpperCase(method.getName().charAt(prefix.length()));
                return Introspector.decapitalize(method.getName().substring(prefix.length()));
            }
        }

        return method.getName();
    }

    private Class<?> createProxyClass(Class<?> superClass) {
        val proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(superClass);
        proxyFactory.setFilter(method -> isSetter(method) || isToString(method));
        return proxyFactory.createClass();
    }

    private static boolean isSetter(Method m) {
        if (m.getParameterCount() != 1) {
            return false;
        }

        for (String prefix : prefixes) {
            if (m.getName().startsWith(prefix)
                    && m.getName().length() > prefix.length()
                    && Character.isUpperCase(m.getName().charAt(prefix.length()))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isToString(Method method) {
        return method.getParameterCount() == 0
                && "toString".equals(method.getName());
    }

}

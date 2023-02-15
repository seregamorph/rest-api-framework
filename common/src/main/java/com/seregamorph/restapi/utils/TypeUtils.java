package com.seregamorph.restapi.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import lombok.experimental.UtilityClass;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

@UtilityClass
public class TypeUtils {

    /**
     * Extract element class from return type of the method. See the tests for this class for more details. A few
     * example:
     * <ul>
     * <li><code>Date doSomething()</code> - <code>Date</code></li>
     * <li><code>List&lt;Date&gt; doSomething()</code> - <code>Date</code></li>
     * <li><code>List&lt;? extends Date&gt; doSomething()</code> - <code>Date</code></li>
     * </ul>
     *
     * @param readMethod getter method
     * @param implementationClass the implementation class
     * @return The element class, or <code>Object.class</code> by default.
     */
    public static Class<?> extractElementClass(Method readMethod, Class<?> implementationClass) {
        // Support for other cases will be added when necessary
        Class<?> clazz = Collection.class.isAssignableFrom(readMethod.getReturnType())
                ? ResolvableType.forMethodReturnType(readMethod, implementationClass).asCollection().resolveGeneric()
                : ResolvableType.forMethodReturnType(readMethod, implementationClass).resolve(readMethod.getReturnType());
        return extractElementClass(clazz, readMethod.getReturnType());
    }

    /**
     * Extract element class from return type of the method. See the tests for this class for more details. A few
     * example:
     * <ul>
     * <li><code>Date doSomething()</code> - <code>Date</code></li>
     * <li><code>List&lt;Date&gt; doSomething()</code> - <code>Date</code></li>
     * <li><code>List&lt;? extends Date&gt; doSomething()</code> - <code>Date</code></li>
     * </ul>
     *
     * @param readMethod getter method
     * @return The element class, or <code>Object.class</code> by default.
     */
    public static Class<?> extractElementClass(Method readMethod) {
        // Support for other cases will be added when necessary
        Class<?> clazz = Collection.class.isAssignableFrom(readMethod.getReturnType())
                ? ResolvableType.forMethodReturnType(readMethod).asCollection().resolveGeneric()
                : ResolvableType.forMethodReturnType(readMethod).resolve(readMethod.getReturnType());
        return extractElementClass(clazz, readMethod.getReturnType());
    }

    /**
     * Extract element class from data type of the field. See the tests for this class for more details. A few example:
     * <ul>
     * <li><code>Date field;</code> - <code>Date</code></li>
     * <li><code>List&lt;Date&gt; field;</code> - <code>Date</code></li>
     * </ul>
     *
     * @param field field
     * @param implementationClass the implementation class
     * @return The element class, or <code>Object.class</code> by default.
     */
    public static Class<?> extractElementClass(Field field, Class<?> implementationClass) {
        // Support for other cases will be added when necessary
        Class<?> clazz = Collection.class.isAssignableFrom(field.getType())
                ? ResolvableType.forField(field).asCollection().resolveGeneric()
                : ResolvableType.forField(field, implementationClass).resolve(field.getType());
        return extractElementClass(clazz, field.getType());
    }

    /**
     * Extract element class from data type of the field. See the tests for this class for more details. A few example:
     * <ul>
     * <li><code>Date field;</code> - <code>Date</code></li>
     * <li><code>List&lt;Date&gt; field;</code> - <code>Date</code></li>
     * </ul>
     *
     * @param field element
     * @return The element class, or <code>Object.class</code> by default.
     */
    public static Class<?> extractElementClass(Field field) {
        // Support for other cases will be added when necessary
        Class<?> clazz = Collection.class.isAssignableFrom(field.getType())
                ? ResolvableType.forField(field).asCollection().resolveGeneric()
                : ResolvableType.forField(field).resolve(field.getType());
        return extractElementClass(clazz, field.getType());
    }

    /**
     * Extract element class from data type of the parameter. See the tests for this class for more details.
     * A few example:
     * <ul>
     * <li><code>Date field;</code> - <code>Date</code></li>
     * <li><code>List&lt;Date&gt; field;</code> - <code>Date</code></li>
     * </ul>
     *
     * @param parameter parameter
     * @param implementationClass the implementation class
     * @return The element class, or <code>Object.class</code> by default.
     */
    public static Class<?> extractElementClass(MethodParameter parameter, Class<?> implementationClass) {
        // Support for other cases will be added when necessary
        Class<?> clazz = Collection.class.isAssignableFrom(parameter.getParameterType())
                ? ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric()
                : ResolvableType.forMethodParameter(parameter, implementationClass).resolve(parameter.getParameterType());
        return extractElementClass(clazz, parameter.getParameterType());
    }

    /**
     * Extract element class from data type of the parameter. See the tests for this class for more details.
     * A few example:
     * <ul>
     * <li><code>Date field;</code> - <code>Date</code></li>
     * <li><code>List&lt;Date&gt; field;</code> - <code>Date</code></li>
     * </ul>
     *
     * @param parameter element
     * @return The element class, or <code>Object.class</code> by default.
     */
    public static Class<?> extractElementClass(MethodParameter parameter) {
        // Support for other cases will be added when necessary
        Class<?> clazz = Collection.class.isAssignableFrom(parameter.getParameterType())
                ? ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric()
                : ResolvableType.forMethodParameter(parameter).resolve(parameter.getParameterType());
        return extractElementClass(clazz, parameter.getParameterType());
    }

    private static Class<?> extractElementClass(Class<?> elementClass, Class<?> returnType) {
        if (elementClass == null) {
            return Object.class;
        }
        if (returnType.isArray()) {
            return elementClass.getComponentType();
        }
        return elementClass;
    }
}

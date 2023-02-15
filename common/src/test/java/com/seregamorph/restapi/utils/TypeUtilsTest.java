package com.seregamorph.restapi.utils;

import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;

@RunWith(MockitoJUnitRunner.class)
public class TypeUtilsTest extends AbstractUnitTest {

    @Test
    public void extractElementClassShouldReturnCorrectValue() {
        checkElementClassForField(TestClassWithDifferentDataTypes.Fields.DATE_FIELD, Date.class);
        checkElementClassForField(TestClassWithDifferentDataTypes.Fields.COLLECTION_FIELD, Object.class);
        checkElementClassForField(TestClassWithDifferentDataTypes.Fields.DATE_COLLECTION_FIELD, Date.class);
        checkElementClassForField(TestClassWithDifferentDataTypes.Fields.DATE_ARRAY_FIELD, Date.class);
        checkElementClassForMethod(TestClassWithDifferentDataTypes.METHOD_RETURN_DATE, Date.class);
        checkElementClassForMethod(TestClassWithDifferentDataTypes.METHOD_RETURN_COLLECTION, Object.class);
        checkElementClassForMethod(TestClassWithDifferentDataTypes.METHOD_RETURN_DATE_COLLECTION, Date.class);
        checkElementClassForMethod(TestClassWithDifferentDataTypes.METHOD_RETURN_DATE_ARRAY, Date.class);
        checkElementClassForMethod(TestClassWithDifferentDataTypes.METHOD_RETURN_LOWER_BOUND_DATE_COLLECTION, Date.class);
        checkElementClassForMethodParameter(0, Date.class);
        checkElementClassForMethodParameter(1, Object.class);
        checkElementClassForMethodParameter(2, Date.class);
        checkElementClassForMethodParameter(3, Date.class);
        checkElementClassForFieldWithGenericType();
        checkElementClassForMethodWithGenericType();
        checkElementClassForMethodParameterWithGenericType();
    }

    private void checkElementClassForField(String fieldName, Class<?> expectedClass) {
        Field field = FieldUtils.getField(TestClassWithDifferentDataTypes.class, fieldName, true);

        Class<?> clazz = TypeUtils.extractElementClass(field);

        collector.checkThat(clazz, equalTo(expectedClass));
    }

    private void checkElementClassForMethod(String methodName, Class<?> expectedClass) {
        Method method = MethodUtils.getMatchingMethod(TestClassWithDifferentDataTypes.class, methodName);

        Class<?> clazz = TypeUtils.extractElementClass(method);

        collector.checkThat(clazz, equalTo(expectedClass));
    }

    private void checkElementClassForMethodParameter(int paramIndex, Class<?> expectedClass) {
        Method method = MethodUtils.getMatchingMethod(TestClassWithDifferentDataTypes.class,
                TestClassWithDifferentDataTypes.METHOD_WITH_PARAMS, Date.class, List.class, List.class, Date[].class);

        Class<?> clazz = TypeUtils.extractElementClass(new MethodParameter(method, paramIndex));

        collector.checkThat(clazz, equalTo(expectedClass));
    }

    private void checkElementClassForFieldWithGenericType() {
        Field field = FieldUtils.getField(
                TestConcreteImplementationClass.class, TestConcreteImplementationClass.Fields.ID, true);

        Class<?> clazz = TypeUtils.extractElementClass(field, TestConcreteImplementationClass.class);

        collector.checkThat(clazz, equalTo(Long.class));
    }

    private void checkElementClassForMethodWithGenericType() {
        Method method = MethodUtils.getMatchingMethod(
                TestConcreteImplementationClass.class, TestConcreteImplementationClass.GET_ID);

        Class<?> clazz = TypeUtils.extractElementClass(method, TestConcreteImplementationClass.class);

        collector.checkThat(clazz, equalTo(Long.class));
    }

    private void checkElementClassForMethodParameterWithGenericType() {
        Method method = MethodUtils.getMatchingMethod(TestGenericBaseClass.class,
                TestGenericBaseClass.SET_ID, Serializable.class);

        Class<?> clazz = TypeUtils.extractElementClass(new MethodParameter(method, 0),
                Long.class);

        collector.checkThat(clazz, equalTo(Long.class));
    }
}

package com.seregamorph.restapi.utils;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import org.junit.Test;

public class ClassUtilsTest extends AbstractUnitTest {

    private static final String SAMPLE_STRING = "foo";
    private static final String SAMPLE_STRING_2 = "bar";

    private static final String NAME = "name";

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_ALIAS = "alias";
    private static final String PROPERTY_TITLE = "title";

    @Test
    public void extractPropertyDescriptorsShouldReturnEmptyCollection() {
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils.extractPropertyDescriptors(Object.class);
        collector.checkThat(propertyDescriptors, empty());
    }

    @Test
    public void extractPropertyDescriptorsShouldProcessClassWithDifferentAccessors() {
        // A concrete class having different accessors
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils
                .extractPropertyDescriptors(TestClassWithDifferentAccessors.class);
        collector.checkThat(propertyDescriptors, hasSize(3));
        collector.checkThat(propertyDescriptors,
                hasItem(hasProperty(NAME, is(TestClassWithDifferentAccessors.Fields.PRIVATE_SETTER))));
        collector.checkThat(propertyDescriptors,
                hasItem(hasProperty(NAME, is(TestClassWithDifferentAccessors.Fields.PROTECTED_SETTER))));
        collector.checkThat(propertyDescriptors,
                hasItem(hasProperty(NAME, is(TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_SETTER))));
    }

    @Test
    public void extractPropertyDescriptorsShouldProcessChildClass() {
        // A concrete class having a super class which could then have super abstract class or interfaces
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils.extractPropertyDescriptors(TestChildClass.class);
        collector.checkThat(propertyDescriptors, hasSize(5));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestChildClass.Fields.DESCRIPTION))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestParentClass.Fields.NAME))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestParentClass.Fields.TITLE))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestAbstractClass.Fields.SUMMARY))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_ALIAS))));
    }

    @Test
    public void extractPropertyDescriptorsShouldProcessParentClass() {
        // A concrete class having a super abstract class which could then have interfaces
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils.extractPropertyDescriptors(TestParentClass.class);
        collector.checkThat(propertyDescriptors, hasSize(4));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestParentClass.Fields.NAME))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestParentClass.Fields.TITLE))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestAbstractClass.Fields.SUMMARY))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_ALIAS))));
    }

    @Test
    public void extractPropertyDescriptorsShouldProcessAbstractClass() {
        // An abstract class having interfaces
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils.extractPropertyDescriptors(TestAbstractClass.class);
        collector.checkThat(propertyDescriptors, hasSize(4));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(TestAbstractClass.Fields.SUMMARY))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_ALIAS))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_NAME))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_TITLE))));
    }

    @Test
    public void extractPropertyDescriptorsShouldProcessChildInterface() {
        // An interface having other interfaces
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils
                .extractPropertyDescriptors(TestChildInterface.class);
        collector.checkThat(propertyDescriptors, hasSize(3));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_NAME))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_ALIAS))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_TITLE))));
    }

    @Test
    public void extractPropertyDescriptorsShouldProcessParentInterface() {
        // An interface having no other interfaces
        Collection<PropertyDescriptor> propertyDescriptors = ClassUtils
                .extractPropertyDescriptors(TestParentInterface.class);
        collector.checkThat(propertyDescriptors, hasSize(2));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_NAME))));
        collector.checkThat(propertyDescriptors, hasItem(hasProperty(NAME, is(PROPERTY_ALIAS))));
    }

    @Test
    public void getSetValueShouldWork() {
        TestChildClass child = new TestChildClass();

        ClassUtils.setFieldValue(child, TestParentClass.Fields.NAME, SAMPLE_STRING);
        ClassUtils.setFieldValue(child, TestChildClass.Fields.DESCRIPTION, SAMPLE_STRING_2);

        collector.checkThat(ClassUtils.getFieldValue(child, TestParentClass.Fields.NAME), is(SAMPLE_STRING));
        collector.checkThat(ClassUtils.getFieldValue(child, TestChildClass.Fields.DESCRIPTION), is(SAMPLE_STRING_2));
    }

    @Test
    public void getFieldValueShouldThrowExceptionForIncorrectMethod() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("object is not an instance of declaring class");

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.getFieldValue(object, TestChildClass.class.getDeclaredMethods()[0]);
    }

    @Test
    public void getFieldValueShouldThrowExceptionForPrivateProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY,
                TestClassWithDifferentAccessors.class,
                TestClassWithDifferentAccessors.Fields.PRIVATE_PROPERTY));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.getFieldValue(object, TestClassWithDifferentAccessors.Fields.PRIVATE_PROPERTY);
    }

    @Test
    public void getFieldValueShouldThrowExceptionForProtectedProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY,
                TestClassWithDifferentAccessors.class,
                TestClassWithDifferentAccessors.Fields.PROTECTED_PROPERTY));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.getFieldValue(object, TestClassWithDifferentAccessors.Fields.PROTECTED_PROPERTY);
    }

    @Test
    public void getFieldValueShouldThrowExceptionForPackageProtectedProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY,
                TestClassWithDifferentAccessors.class,
                TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_PROPERTY));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.getFieldValue(object, TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_PROPERTY);
    }

    @Test
    public void getFieldValueShouldThrowExceptionForPrivateGetter() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.MISSING_GETTER_FOR_PROPERTY,
                TestClassWithDifferentAccessors.Fields.PRIVATE_GETTER,
                TestClassWithDifferentAccessors.class));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.getFieldValue(object, TestClassWithDifferentAccessors.Fields.PRIVATE_GETTER);
    }

    @Test
    public void getFieldValueShouldThrowExceptionForProtectedGetter() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.MISSING_GETTER_FOR_PROPERTY,
                TestClassWithDifferentAccessors.Fields.PROTECTED_GETTER,
                TestClassWithDifferentAccessors.class));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.getFieldValue(object, TestClassWithDifferentAccessors.Fields.PROTECTED_GETTER);
    }

    @Test
    public void getFieldValueShouldThrowExceptionForPackageProtectedGetter() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.MISSING_GETTER_FOR_PROPERTY,
                TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_GETTER,
                TestClassWithDifferentAccessors.class));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.getFieldValue(object, TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_GETTER);
    }

    @Test
    public void getFieldValueShouldThrowExceptionForInvalidProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY, TestChildClass.class, SAMPLE_STRING));

        TestChildClass child = new TestChildClass();

        ClassUtils.getFieldValue(child, SAMPLE_STRING);
    }

    @Test
    public void setFieldValueShouldThrowExceptionForPrivateProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY,
                TestClassWithDifferentAccessors.class,
                TestClassWithDifferentAccessors.Fields.PRIVATE_PROPERTY));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.setFieldValue(object, TestClassWithDifferentAccessors.Fields.PRIVATE_PROPERTY, SAMPLE_STRING);
    }

    @Test
    public void setFieldValueShouldThrowExceptionForProtectedProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY,
                TestClassWithDifferentAccessors.class,
                TestClassWithDifferentAccessors.Fields.PROTECTED_PROPERTY));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.setFieldValue(object, TestClassWithDifferentAccessors.Fields.PROTECTED_PROPERTY, SAMPLE_STRING);
    }

    @Test
    public void setFieldValueShouldThrowExceptionForPackageProtectedProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY,
                TestClassWithDifferentAccessors.class,
                TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_PROPERTY));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.setFieldValue(object, TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_PROPERTY, SAMPLE_STRING);
    }

    @Test
    public void setFieldValueShouldThrowExceptionForPrivateSetter() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.MISSING_SETTER_FOR_PROPERTY,
                TestClassWithDifferentAccessors.Fields.PRIVATE_SETTER,
                TestClassWithDifferentAccessors.class));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.setFieldValue(object, TestClassWithDifferentAccessors.Fields.PRIVATE_SETTER, SAMPLE_STRING);
    }

    @Test
    public void setFieldValueShouldThrowExceptionForProtectedSetter() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.MISSING_SETTER_FOR_PROPERTY,
                TestClassWithDifferentAccessors.Fields.PROTECTED_SETTER,
                TestClassWithDifferentAccessors.class));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.setFieldValue(object, TestClassWithDifferentAccessors.Fields.PROTECTED_SETTER, SAMPLE_STRING);
    }

    @Test
    public void setFieldValueShouldThrowExceptionForPackageProtectedSetter() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.MISSING_SETTER_FOR_PROPERTY,
                TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_SETTER,
                TestClassWithDifferentAccessors.class));

        TestClassWithDifferentAccessors object = new TestClassWithDifferentAccessors();

        ClassUtils.setFieldValue(object, TestClassWithDifferentAccessors.Fields.PACKAGE_PROTECTED_SETTER, SAMPLE_STRING);
    }

    @Test
    public void setFieldValueShouldThrowExceptionForInvalidProperty() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(String.format(
                ClassUtils.FAILED_TO_LOOKUP_PROPERTY, TestChildClass.class, SAMPLE_STRING));

        TestChildClass child = new TestChildClass();

        ClassUtils.setFieldValue(child, SAMPLE_STRING, SAMPLE_STRING_2);
    }

    @Test
    public void getSetStaticFieldDirectlyShouldWork() {
        String defaultValue = ClassUtils.getStaticFieldValueDirectly(
                TestClassWithStaticFields.class, TestClassWithStaticFields.STATIC_FIELD);
        collector.checkThat(defaultValue, equalTo("staticFieldValue"));

        ClassUtils.setStaticFieldValueDirectly(
                TestClassWithStaticFields.class, TestClassWithStaticFields.STATIC_FIELD, SAMPLE_STRING);
        String updatedValue = ClassUtils.getStaticFieldValueDirectly(
                TestClassWithStaticFields.class, TestClassWithStaticFields.STATIC_FIELD);
        collector.checkThat(updatedValue, equalTo(SAMPLE_STRING));
    }
}


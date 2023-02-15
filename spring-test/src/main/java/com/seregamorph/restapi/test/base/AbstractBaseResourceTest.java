package com.seregamorph.restapi.test.base;

import static com.google.common.base.Preconditions.checkState;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import com.google.common.collect.ImmutableSet;
import com.seregamorph.restapi.base.BasePartial;
import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.BaseResource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

public abstract class AbstractBaseResourceTest extends AbstractBasePOJOTest {

    private static final Set<String> FORBIDDEN_FIELD_CLASS_NAMES = ImmutableSet.of(
            URL.class.getName(),
            URI.class.getName()
    );

    private static final Set<String> FORBIDDEN_FIELD_ANNOTATION_CLASS_NAMES = ImmutableSet.of(
            "javax.persistence.Embeddable",
            "javax.persistence.Entity",
            "javax.persistence.Table",
            "org.hibernate.annotations.Entity",
            "org.hibernate.annotations.Table"
    );

    private static final String SUFFIX_RESOURCE = "Resource";
    private static final String SUFFIX_PARTIAL = "Partial";

    public void validateProjections(Class<? extends BaseResource> clazz) {
        val projectionEnumClass = TestProjectionUtils.tryGetProjectionType(clazz);

        if (projectionEnumClass == null) {
            // no Projection enum for resource
            return;
        }

        collector.checkThat(clazz + " has projections, hence should be public",
                Modifier.isPublic(clazz.getModifiers()), is(true));
        for (val projection : projectionEnumClass.getEnumConstants()) {
            val projectionClass = projection.getProjectionClass();
            validateProjectionType(projectionClass, clazz);
        }
    }

    /**
     * Please note that these enforcements are related to DescriptionModelPropertyBuilderPlugin in springfox module.
     */
    @Deprecated
    public void validatePartials(Class<? extends BaseResource> clazz) {
        val className = clazz.getSimpleName();
        if (!className.endsWith(SUFFIX_RESOURCE)) {
            throw new IllegalStateException(clazz.getSimpleName() + " name does not end with " + SUFFIX_RESOURCE);
        }
        val resource = className.substring(0, className.length() - SUFFIX_RESOURCE.length());
        for (Class<?> iface : clazz.getInterfaces()) {
            if (BasePartial.class.isAssignableFrom(iface)) {
                collector.checkThat("Partial interface " + iface + " name does not start with resource name",
                        iface.getSimpleName(), startsWith(resource));
                val partialPackage = iface.getPackage().getName();
                val resourcePackage = clazz.getPackage().getName();
                if (!partialPackage.equals(resourcePackage)) {
                    val lastDot = StringUtils.lastIndexOf(partialPackage, '.');
                    val parentPartialPackage = partialPackage.substring(0, lastDot);
                    if (!parentPartialPackage.equals(resourcePackage)) {
                        collector.addError(new AssertionError("Partial interface " + iface + " should be placed "
                                + "either in the same package or a subpackage of " + clazz));
                    }
                }
                collector.checkThat("BasePartial interface name should end with `Partial`",
                        iface.getSimpleName(), endsWith(SUFFIX_PARTIAL));
            } else {
                collector.checkThat("Non-BasePartial interface name should not end with `Partial`",
                        iface.getSimpleName(), not(endsWith(SUFFIX_PARTIAL)));
            }
        }
    }

    public void validatePayloadFields(Class<? extends BasePayload> clazz) {
        Class<?> type = clazz;
        while (type != Object.class && type != null) {
            for (val field : type.getDeclaredFields()) {
                if (!fieldAllowed(field)) {
                    collector.addError(new AssertionError("Field not allowed in resource class: " + field));
                }
            }
            type = type.getSuperclass();
        }
    }

    protected boolean fieldAllowed(Field field) {
        val fieldType = field.getType();
        for (val annotation : fieldType.getAnnotations()) {
            if (FORBIDDEN_FIELD_ANNOTATION_CLASS_NAMES.contains(annotation.annotationType().getName())) {
                return false;
            }
        }
        // e.g. java.net.URI is not correctly supported in springfox swagger, hence should be avoided
        if (FORBIDDEN_FIELD_CLASS_NAMES.contains(fieldType.getName())) {
            return false;
        }
        return true;
    }

    private void validateProjectionType(Class<?> projectionClass, Class<?> resourceType) {
        collector.checkThat("Projection class " + projectionClass + " should be public",
                Modifier.isPublic(projectionClass.getModifiers()), is(true));
        collector.checkThat("Projection class " + projectionClass + " should be interface",
                projectionClass.isInterface(), is(true));

        for (Method projectionMethod : projectionClass.getMethods()) {
            if (projectionMethod.getDeclaringClass() == Object.class) {
                // equals, hashCode, toString, etc.
                continue;
            }

            if (projectionMethod.isDefault()) {
                // interface method with implementation
                continue;
            }

            collector.checkThat(projectionMethod + " should have zero parameters",
                    projectionMethod.getParameterCount(), is(0));

            if (AnnotationUtils.findAnnotation(projectionMethod, Value.class) != null) {
                // Skip the method with SpeL expression
                continue;
            }

            try {
                Method resourceMethod = resourceType.getMethod(projectionMethod.getName());

                // please note, that these checks can be incomplete, feel free to enrich the logic
                if (projectionMethod.getReturnType().isAssignableFrom(resourceMethod.getReturnType())) {
                    if (List.class.isAssignableFrom(projectionMethod.getReturnType())
                            && List.class.isAssignableFrom(resourceMethod.getReturnType())) {
                        Class<?> projectionListType = getGenericType(projectionMethod.getGenericReturnType(), 0);
                        Class<?> resourceListType = getGenericType(resourceMethod.getGenericReturnType(), 0);

                        if (projectionListType != resourceListType) {
                            validateProjectionType(projectionListType, resourceListType);
                        }
                    }
                } else if (projectionMethod.getReturnType().isInterface()) {
                    validateProjectionType(projectionMethod.getReturnType(), resourceMethod.getReturnType());
                } else {
                    collector.addError(new IllegalStateException("Incompatible projection " + projectionMethod +
                            " and resource " + resourceMethod));
                }
            } catch (NoSuchMethodException e) {
                collector.addError(new IllegalStateException("Error while validating " + projectionClass.getSimpleName()
                        + " method " + projectionMethod.getName() + " for the resource " + resourceType.getSimpleName(), e));
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static Class<?> getGenericType(Type type, int index) {
        checkState(type instanceof ParameterizedType, "Unexpected type " + type);
        Type genericType = ((ParameterizedType) type).getActualTypeArguments()[index];
        checkState(genericType instanceof Class, "Generic argument is not of a class for " + type);
        return (Class<?>) genericType;
    }

}

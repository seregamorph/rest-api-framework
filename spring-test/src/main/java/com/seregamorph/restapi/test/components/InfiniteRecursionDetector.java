package com.seregamorph.restapi.test.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.utils.ClassUtils;
import com.seregamorph.restapi.utils.TypeUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class InfiniteRecursionDetector {

    private final ApplicationContext applicationContext;

    // Implementation note: The term 'infinite recursion' is borrowed from ObjectMapper's error messages.

    // The error may happen when we serialize an object with circular references to other objects. That object may be
    // a resource (e.g. we map from an entity to a resource and serialize that resource directly) or a projection
    // of a resource (e.g. from a resource having circular references to other resources, we create a projection
    // having circular references to other projections).

    public <F, T> void detect(Class<F> fromClass, Function<F, T> mapper) throws JsonProcessingException {
        // We assume that all these classes can be initialized using Class.newInstance by default
        detect(fromClass, mapper, Collections.emptySet());
    }

    public <R, P> void detect(Class<R> resourceClass, Class<P> projectionClass) throws JsonProcessingException {
        // We assume that all these classes can be initialized using Class.newInstance by default
        detect(resourceClass, projectionClass, Collections.emptySet());
    }

    public <F, T> void detect(Class<F> fromClass,
                              Function<F, T> mapper,
                              Collection<Object> preInitializedInstances) throws JsonProcessingException {
        // We only initialize classes belong directly or indirectly to the package of the source class by default
        detect(fromClass, mapper, preInitializedInstances, Collections.singleton(fromClass));
    }

    public <R, P> void detect(Class<R> resourceClass,
                              Class<P> projectionClass,
                              Collection<Object> preInitializedInstances) throws JsonProcessingException {
        // We only initialize classes belong directly or indirectly to the package of the source class by default
        detect(resourceClass, projectionClass, preInitializedInstances, Collections.singleton(resourceClass));
    }

    public <F, T> void detect(Class<F> fromClass,
                              Function<F, T> mapper,
                              Collection<Object> preInitializedInstances,
                              Collection<Class<?>> acceptedPackageClasses) throws JsonProcessingException {
        F from = instance(fromClass, preInitializedInstances, new HashSet<>(), acceptedPackageClasses);
        T to = mapper.apply(from);
        detect(to);
    }

    public <R, P> void detect(Class<R> resourceClass,
                              Class<P> projectionClass,
                              Collection<Object> preInitializedInstances,
                              Collection<Class<?>> acceptedPackageClasses) throws JsonProcessingException {
        R resource = instance(resourceClass, preInitializedInstances, new HashSet<>(), acceptedPackageClasses);
        val projectionFactory = applicationContext.getBean(ProjectionFactory.class);
        P projection = projectionFactory.createProjection(projectionClass, resource);
        detect(projection);
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private void detect(Object object) throws JsonProcessingException {
        // We rely on serialization (which works on instances), not extraction of metadata via reflection
        // (which works on classes) to detect infinite recursion because:
        // - Infinite recursion happens at instance level, not class level.
        // - Even if there is a circular reference at class level, infinite recursion may not happen if the instances
        // are constructed / mapped properly.
        // - If we use reflection to detect circular reference, then it won't be able to tell the difference
        // after the initialization / mapping logic has been fixed to avoid infinite recursion.
        // We use com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString instead of String.valueOf because:
        // - The standard serialization mechanism in Spring
        // is com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString
        // - At resource level, dev can declare @ToString.Exclude, but it doesn't eliminate the infinite recursion
        // - If infinite recursion happens, com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString gives us
        // a message stating clearly what is the reference chain.
        try {
            objectMapper().writeValueAsString(object);
        } catch (JsonMappingException e) {
            // to avoid CR finding
            log.trace("cause", e);
            // We are only interested in the error message
            // E.g. Direct self-reference leading to cycle
            // (through reference chain: com.company.project.resources.YourResource["self"])
            // Or Infinite recursion (StackOverflowError)
            // (through reference chain: com.company.project.resources.FirstResource["seconds"]->java.util.ArrayList[0]
            // ->com.company.project.resources.SecondResource["first"]->...)
            throw new IllegalStateException(e.getMessage());
        }
    }

    private ObjectMapper objectMapper() {
        // While we can construct an ObjectMapper instance directly, we should use the same instance being used
        // in the application.
        // E.g. If the ObjectMapper instance has been configured to support PartialPayload, infinite recursion errors
        // may no longer happen.
        return applicationContext.getBean(ObjectMapper.class);
    }

    private static <T> T instance(Class<T> clazz,
                                   Collection<Object> preinitializedInstances,
                                   Collection<Object> instances,
                                   Collection<Class<?>> acceptedPackageClasses) {
        for (Object instance : instances) {
            if (clazz.isInstance(instance)) {
                return clazz.cast(instance);
            }
        }

        T instance = null;

        for (Object preinitializedInstance : preinitializedInstances) {
            if (clazz.isInstance(preinitializedInstance)) {
                instance = clazz.cast(preinitializedInstance);
                break;
            }
        }

        if (instance == null) {
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(String.format(
                        "Unable to create a new instance for %s. Please specify an instance manually.",
                        clazz.getName()), e);
            }
        }

        // Add to the pool immediately - we need it right below for the recursive calls!
        instances.add(instance);

        Field[] allFields = FieldUtils.getAllFields(clazz);

        for (Field field : allFields) {
            if (!Modifier.isFinal(field.getModifiers())) {
                process(instance, field, preinitializedInstances, instances, acceptedPackageClasses);
            }
        }

        return instance;
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private static void process(Object instance,
                                Field field,
                                Collection<Object> preinitializedInstances,
                                Collection<Object> instances,
                                Collection<Class<?>> acceptedPackageClasses) {
        Class<?> fieldElementClass = TypeUtils.extractElementClass(field);

        // class.getPackage() may be null (e.g. for primitive class)
        if (fieldElementClass.getPackage() == null
                || !belong(fieldElementClass, acceptedPackageClasses)
                || fieldElementClass.isEnum()) {
            return;
        }

        Class<?> fieldClass = field.getType();
        Object fieldElementInstance = instance(fieldElementClass, preinitializedInstances, instances, acceptedPackageClasses);

        if (fieldClass == fieldElementClass) {
            ClassUtils.setFieldValue(instance, field.getName(), fieldElementInstance);
        } else if (List.class.isAssignableFrom(fieldClass)) {
            ClassUtils.setFieldValue(instance, field.getName(), Collections.singletonList(fieldElementInstance));
        } else if (Set.class.isAssignableFrom(fieldClass)) {
            ClassUtils.setFieldValue(instance, field.getName(), Collections.singleton(fieldElementInstance));
        } else if (fieldClass.isArray()) {
            // We may hit java.lang.ArrayStoreException: java.lang.Class
            // if we use (Object[]) Array.newInstance(fieldElementClass, 1) instead of new Object[1]
            ClassUtils.setFieldValue(instance, field.getName(), new Object[] {fieldElementClass});
        }
        // Other types (including maps) are NOT supported!
    }

    private static boolean belong(Class<?> clazz, Collection<Class<?>> acceptedPackageClasses) {
        for (Class<?> acceptedPackageClass : acceptedPackageClasses) {
            if (belong(clazz, acceptedPackageClass)) {
                return true;
            }
        }

        return false;
    }

    private static boolean belong(Class<?> clazz, Class<?> acceptedPackageClass) {
        String acceptedPackageName = acceptedPackageClass.getPackage().getName();
        String packageName = clazz.getPackage().getName();
        return StringUtils.equals(packageName, acceptedPackageName)
                || packageName.startsWith(acceptedPackageName + ".");
    }
}

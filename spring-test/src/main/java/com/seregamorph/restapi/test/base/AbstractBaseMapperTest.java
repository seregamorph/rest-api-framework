package com.seregamorph.restapi.test.base;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.base.BaseMapper;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.mapstruct.Renamed;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import com.seregamorph.restapi.utils.TypeUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;

@Slf4j
public abstract class AbstractBaseMapperTest extends AbstractUnitTest {

    protected abstract <T extends BaseMapper> T getMapper(Class<T> mapperClass);

    // Pay attention to naming convention. If we have entity User and resource UserResource, the mapper could be
    // either UserMapper and UserResourceMapper. Furthermore, UserMapper may already exist in legacy code.
    // Notice: sometimes entity is Foo and resource is BarResource (because Foo is a bad name and we want to fix it).

    /**
     * Verifies that, if the mapper being tested is an XMapper, then all mappers being wired into it must also
     * be XMappers.
     * E.g. if the mapper being tested is an EntityToResourceMapper, then all used mappers must also be
     * EntityToResourceMapper. Meaning, all used mappers must either be EntityToResourceMapper or BiDirectionalMapper.
     * This is to make sure that, if a mapper is declared to be 'used', then the mapping methods necessary will also
     * be there to be 'used'.
     * Notice that it is a strict test. If A uses B and A is a BiDirectionalMapper and B is both EntityToResourceMapper
     * and ResourceToEntityMapper, then technically nothing is wrong (they are not the same but equivalent). But system
     * still reports an error any way. We accept it as EntityToResourceMapper and ResourceToEntityMapper, when used
     * together, should be replaced with BiDirectionalMapper.
     */
    protected void fieldMappersMustHaveTheSameMappingInterfaces(
            Class<? extends BaseMapper> mapperClass) throws IllegalAccessException {
        BaseMapper mapper = getMapper(mapperClass);
        Set<Class<?>> fieldMappers = extractFieldMappers(mapperClass, mapper);
        Class<?>[] interfaces = mapperClass.getInterfaces();

        for (Class<?> fieldMapper : fieldMappers) {
            for (Class<?> iface : interfaces) {
                boolean check = iface.isAssignableFrom(fieldMapper) || fieldMapper.getDeclaredMethods().length > 0;
                String reason = String.format(
                        "Mapper %s is used in %s; "
                                + "therefore, it must either be assignable to %s or have its own mapping methods",
                        fieldMapper.getName(), mapperClass.getName(), iface.getName());
                collector.checkThat(reason, check, is(true));
            }
        }
    }

    protected void allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic(
            Class<? extends BaseMapper> mapperClass) throws IllegalAccessException {
        BaseMapper mapper = getMapper(mapperClass);
        val resourceClass = extractResourceClass(mapperClass).orElse(null);
        val entityClass = extractEntityClass(mapperClass).orElse(null);
        log.trace("Resource class: {}", resourceClass);
        log.trace("Entity class: {}", entityClass);
        if (resourceClass == null || entityClass == null) {
            log.info("Skipping {}", mapperClass);
            return;
        }

        val nestedResourceClasses = extractNestedResourceClasses(resourceClass, entityClass);
        val mappedResourceClasses = extractFieldMappers(mapperClass, mapper).stream()
                .map(AbstractBaseMapperTest::extractResourceClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        nestedResourceClasses.removeAll(mappedResourceClasses);
        nestedResourceClasses.remove(resourceClass);

        String classesAsString = nestedResourceClasses.stream()
                .map(Class::getName)
                .collect(Collectors.joining(", "));
        String reason = String.format("No mappers for [%s] can be found in the declaration of %s",
                classesAsString, mapperClass);
        collector.checkThat(reason, nestedResourceClasses, empty());

        // No reverse check (to verify that there's no redundant mapper declared in @Mapper annotation) here.
        // Mapstruct doesn't use a mapper in another mapper unnecessarily. This means, even if you declare that
        // A should use B, the generated mapper for A may not contain any field with type B.
        // Because @Mapper is not retained at runtime, we are not able to check that B is really declared and therefore,
        // won't be able to report an error.

        // No further check for deeply nested resources - we only check the constraint between a resource and its
        // directly nested ones.
    }

    private static Set<Class<? extends BaseResource>> extractNestedResourceClasses(
            Class<? extends BaseResource> resourceClass,
            Class<?> entityClass
    ) {
        Field[] allFields = FieldUtils.getAllFields(resourceClass);
        Set<Class<? extends BaseResource>> results = new HashSet<>();

        for (Field field : allFields) {
            Renamed renamed = field.getAnnotation(Renamed.class);
            String fieldName = renamed == null ? field.getName() : renamed.value();

            // Verify that the field also exists in the entity class. This is to handle cases where we may add a new
            // nested resource that doesn't have any associated nested entity just to enhance the resource schema.
            if (FieldUtils.getField(entityClass, fieldName, true) == null) {
                continue;
            }

            Class<?> elementClass = TypeUtils.extractElementClass(field);

            if (BaseResource.class.isAssignableFrom(elementClass)) {
                results.add(elementClass.asSubclass(BaseResource.class));
            }
        }

        return results;
    }

    /**
     * Extracts the mapper classes that have been wired into the mapper instance.
     * @return a non null set of mapper classes that have been wired into the mapper instance.
     */
    private static Set<Class<?>> extractFieldMappers(Class<? extends BaseMapper> mapperClass, BaseMapper mapper)
            throws IllegalAccessException {
        Field[] allFields = FieldUtils.getAllFields(mapper.getClass());

        for (Field field : allFields) {
            // Handle the case of decorator. The delegate mapper may be wired in different ways depending on
            // the component model (see Mapstruct reference guide for details). Therefore, we use a simple check:
            // If the field type is the same as the mapper class (notice that this is the declaration mapper class,
            // not the generated one), then we consider it's a delegate mapper.
            if (field.getType() == mapperClass) {
                Object fieldValue = FieldUtils.readField(field, mapper, true);
                return extractDeclaredMappers(fieldValue.getClass());
            }
        }

        return extractDeclaredMappers(mapper.getClass());
    }

    /**
     * Extracts the mapper classes that have been declared in the specified mapper class.
     * @param mapperClass the mapper class.
     * @return a non null set of mapper classes being used as fields in the specified mapper class.
     */
    private static Set<Class<?>> extractDeclaredMappers(Class<?> mapperClass) {
        return FieldUtils.getAllFieldsList(mapperClass)
                .stream()
                .filter(AbstractBaseMapperTest::isMapStructField)
                .map(Field::getType)
                .collect(Collectors.toSet());
    }

    private static boolean isMapStructField(Field field) {
        // The generated mapper may have non - mapstruct fields, e.g. $jacocoData.
        return !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers());
    }

    private static Optional<Class<?>> extractEntityClass(Class<?> mapperClass) {
        return extractDataClass(mapperClass, false);
    }

    private static Optional<Class<? extends BaseResource>> extractResourceClass(Class<?> mapperClass) {
        return extractDataClass(mapperClass, true).map(cls -> cls.asSubclass(BaseResource.class));
    }

    private static Optional<Class<?>> extractDataClass(Class<?> mapperClass, boolean resource) {
        ClassTypeInformation<?> classTypeInfo = ClassTypeInformation.from(mapperClass);
        Class<?>[] interfaces = mapperClass.getInterfaces();
        Set<Class<?>> dataClasses = new HashSet<>();

        for (Class<?> iface : interfaces) {
            List<TypeInformation<?>> typeArguments = classTypeInfo.getSuperTypeInformation(iface).getTypeArguments();

            if (CollectionUtils.isEmpty(typeArguments)) {
                continue;
            }

            for (TypeInformation<?> typeArgument : typeArguments) {
                boolean resourceAssignable = BaseResource.class.isAssignableFrom(typeArgument.getType());

                if (resource == resourceAssignable) {
                    dataClasses.add(typeArgument.getType());
                }
            }
        }

        // To be sure, there should be only 1 class found
        if (dataClasses.size() == 1) {
            return Optional.of(dataClasses.iterator().next());
        }

        // Target class may not be found, e.g. in the case we map between Instant and Date. If resource = true then
        // no classes can be found. If resource = false then more than 1 class can be found. In both cases, we should
        // not accept the result.
        return Optional.empty();
    }
}

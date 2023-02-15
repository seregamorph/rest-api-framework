package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.ClassPathScanner.scan;

import com.seregamorph.restapi.base.BaseMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
public abstract class BaseMappersTest extends AbstractBaseMapperTest {

    private final Set<Class<? extends BaseMapper>> mapperClasses;

    /**
     * Resolves classes based on this class package.
     */
    protected BaseMappersTest() {
        mapperClasses = getMapperClasses(getClass().getPackage().getName());
    }

    protected BaseMappersTest(String packageName, String... otherPackageNames) {
        val packageNames = new ArrayList<String>();
        packageNames.add(packageName);
        Collections.addAll(packageNames, otherPackageNames);

        mapperClasses = new LinkedHashSet<>();
        for (val pkg : packageNames) {
            mapperClasses.addAll(getMapperClasses(pkg));
        }
    }

    private static Set<Class<? extends BaseMapper>> getMapperClasses(String packageName) {
        Set<Class<? extends BaseMapper>> classes = scan(BaseMapper.class, packageName);
        Set<Class<? extends BaseMapper>> componentInterfaces = new HashSet<>();
        for (Class<? extends BaseMapper> clazz : classes) {
            if (!clazz.isInterface() && clazz.getAnnotation(Component.class) != null) {
                addInterfaces(componentInterfaces, clazz);
            }
        }
        classes.removeAll(componentInterfaces);
        val mappers = classes.stream()
                .filter(Class::isInterface)
                .collect(Collectors.toSet());
        Assert.isTrue(!mappers.isEmpty(), "No mappers found in package " + packageName);
        return mappers;
    }

    @Override
    protected <T extends BaseMapper> T getMapper(Class<T> mapperClass) {
        return Mappers.getMapper(mapperClass);
    }

    @Test
    public void fieldMappersMustHaveTheSameMappingInterfaces() throws Exception {
        for (val mapperClass : mapperClasses) {
            log.info("Testing {}...", mapperClass);
            fieldMappersMustHaveTheSameMappingInterfaces(mapperClass);
        }
    }

    @Test
    public void allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic() throws Exception {
        for (val mapperClass : mapperClasses) {
            log.info("Testing {}...", mapperClass);
            allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic(mapperClass);
        }
    }

    private static void addInterfaces(Set<Class<? extends BaseMapper>> interfaces, Class<?> clazz) {
        for (Class<?> classInterface : clazz.getInterfaces()) {
            if (BaseMapper.class.isAssignableFrom(classInterface)) {
                interfaces.add(classInterface.asSubclass(BaseMapper.class));
                addInterfaces(interfaces, classInterface);
            }
        }
    }
}

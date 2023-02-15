package com.seregamorph.restapi.test.utils;

import com.seregamorph.restapi.utils.MoreReflectionUtils;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

@UtilityClass
public class ClassPathScanner {

    public static <T> Set<Class<? extends T>> scan(Class<T> superInterface, String basePackage) {
        val beanDefinitions = scanDefinitions(superInterface, basePackage);

        return beanDefinitions.stream()
                .map(definition -> (Class<? extends T>) MoreReflectionUtils.tryClassForName(
                        definition.getBeanClassName(), superInterface))
                .collect(Collectors.toSet());
    }

    private static Set<BeanDefinition> scanDefinitions(Class<?> superInterface, String basePackage) {
        val scanner = new InterfaceAwareScanner();
        scanner.addIncludeFilter(new AssignableTypeFilter(superInterface));
        return scanner.findCandidateComponents(basePackage);
    }

    /**
     * @see org.springframework.data.util.AnnotatedTypeScanner.InterfaceAwareScanner
     */
    private static class InterfaceAwareScanner extends ClassPathScanningCandidateComponentProvider {

        private InterfaceAwareScanner() {
            super(false);
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return super.isCandidateComponent(beanDefinition) || beanDefinition.getMetadata().isInterface();
        }
    }

}

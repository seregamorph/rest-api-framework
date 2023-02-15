package com.seregamorph.restapi.mapstruct.extensions.spi;

import java.beans.Introspector;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;

/**
 * An accessor naming strategy that supports not only traditional accessors (get-/is-/set-)
 * but also custom ones (with-).
 */
public class ExtendedAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    private static final String PREFIX_GET = "get";
    private static final String PREFIX_IS = "is";

    private static final String PREFIX_SET = "set";
    private static final String PREFIX_WITH = "with";

    private static final String[] PREFIXES_GETTER = {PREFIX_GET, PREFIX_IS};
    private static final String[] PREFIXES_SETTER = {PREFIX_SET, PREFIX_WITH};
    private static final String[] PREFIXES = {PREFIX_GET, PREFIX_IS, PREFIX_SET, PREFIX_WITH};

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();

        for (String prefix : PREFIXES_GETTER) {
            if (methodName.startsWith(prefix) && method.getReturnType().getKind() != TypeKind.VOID) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSetterMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();

        for (String prefix : PREFIXES_SETTER) {
            if (methodName.startsWith(prefix) && methodName.length() > prefix.length()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getPropertyName(ExecutableElement getterOrSetterMethod) {
        String methodName = getterOrSetterMethod.getSimpleName().toString();

        for (String prefix : PREFIXES) {
            if (methodName.startsWith(prefix)) {
                return Introspector.decapitalize(methodName.substring(prefix.length()));
            }
        }

        return methodName;
    }
}

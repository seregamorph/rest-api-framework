package com.seregamorph.restapi.partial;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class RequiredFieldsException extends RuntimeException {

    private static final long serialVersionUID = 1571815950005L;

    private final Class<?> targetClass;
    private final Class<?> targetPartialClass;
    private final List<String> requiredFieldNames;

    @SuppressWarnings("WeakerAccess")
    public RequiredFieldsException(Class<?> targetClass, @Nullable Class<?> targetPartialClass,
                                   Collection<String> requiredFieldNames) {
        Validate.notNull(targetClass);
        Validate.isTrue(requiredFieldNames != null && requiredFieldNames.size() > 0);
        this.targetClass = targetClass;
        this.targetPartialClass = targetPartialClass;
        this.requiredFieldNames = Collections.unmodifiableList(new ArrayList<>(requiredFieldNames));
    }

    @SuppressWarnings("unused")
    public Class<?> getTargetClass() {
        return targetClass;
    }

    @SuppressWarnings("unused")
    public List<String> getRequiredFieldNames() {
        return requiredFieldNames;
    }

    @Override
    public String getMessage() {
        String fields = String.join(", ", requiredFieldNames);
        return targetPartialClass == null ?
                String.format("Fields [%s] are required for resource [%s].", fields, targetClass.getSimpleName())
                : String.format("Fields [%s] are required for resource [%s] as [%s].", fields,
                targetClass.getSimpleName(), targetPartialClass.getSimpleName());
    }
}

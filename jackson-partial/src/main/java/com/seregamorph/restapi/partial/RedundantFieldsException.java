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
public class RedundantFieldsException extends RuntimeException {

    private static final long serialVersionUID = 1571804987728L;

    private final Class<?> targetClass;
    private final Class<?> targetPartialClass;
    private final List<String> redundantFieldNames;

    @SuppressWarnings("WeakerAccess")
    public RedundantFieldsException(Class<?> targetClass, @Nullable Class<?> targetPartialClass,
                                    Collection<String> redundantFieldNames) {
        Validate.notNull(targetClass);
        Validate.isTrue(redundantFieldNames != null && redundantFieldNames.size() > 0);
        this.targetClass = targetClass;
        this.targetPartialClass = targetPartialClass;
        this.redundantFieldNames = Collections.unmodifiableList(new ArrayList<>(redundantFieldNames));
    }

    @SuppressWarnings("unused")
    public Class<?> getTargetClass() {
        return targetClass;
    }

    @SuppressWarnings("unused")
    public List<String> getRedundantFieldNames() {
        return redundantFieldNames;
    }

    @Override
    public String getMessage() {
        String fields = String.join(", ", redundantFieldNames);
        return targetPartialClass == null ?
                String.format("Fields [%s] are not allowed for resource [%s].", fields, targetClass.getSimpleName())
                : String.format("Fields [%s] are not allowed for resource [%s] as [%s].", fields,
                targetClass.getSimpleName(), targetPartialClass.getSimpleName());
    }
}

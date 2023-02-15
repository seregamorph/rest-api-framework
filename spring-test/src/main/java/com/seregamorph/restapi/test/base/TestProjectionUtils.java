package com.seregamorph.restapi.test.base;

import static com.google.common.base.Preconditions.checkState;

import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.utils.MoreReflectionUtils;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class TestProjectionUtils {

    @Nullable
    public static Class<? extends ProjectionName> tryGetProjectionType(Class<?> resourceClass) {
        val projectionClassName = resourceClass.getName() + "$Projection";
        try {
            val projectionEnumClass = MoreReflectionUtils.classForName(projectionClassName, Enum.class);
            checkState(ProjectionName.class.isAssignableFrom(projectionEnumClass),
                    "%s should implement " + ProjectionName.class, projectionEnumClass);
            return projectionEnumClass.asSubclass(ProjectionName.class);
        } catch (ClassNotFoundException e) {
            // no Projection enum for resource
            return null;
        }
    }
}

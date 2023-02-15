package com.seregamorph.restapi.mapstruct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field that it has been renamed, e.g. when mapping from <code>yourEntity.user</code> to
 * <code>yourName.owner</code>, annotate <code>yourName.owner</code> with <code>@Renamed("user")</code>.
 * The annotation is supposed to be used in {@link com.seregamorph.restapi.base.BaseResource} classes only.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Renamed {

    /**
     * The name of the other field that this field is mapped to.
     *
     * @return field name
     */
    String value();
}

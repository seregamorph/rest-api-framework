package com.seregamorph.restapi.test.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks that the database state should be reset after the test class or test method that it attaches to
 * has finished. Notice that, resetting database state is a time consuming operation, therefore you should only use it
 * when it's necessary.
 *
 * @see DatabaseStateResetTestExecutionListener
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("WeakerAccess")
public @interface DatabaseStateReset {

}

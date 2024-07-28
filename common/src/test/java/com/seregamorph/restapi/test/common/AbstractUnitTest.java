package com.seregamorph.restapi.test.common;

import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import static org.junit.rules.ExpectedException.none;

@Deprecated
public abstract class AbstractUnitTest {

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Rule
    public final ExpectedException expectedException = none();

    protected void expect(Throwable throwable) {
        expectedException.expect(throwable.getClass());
        expectedException.expectMessage(throwable.getMessage());
    }
}

package com.seregamorph.restapi.test.base;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractStackTraceHolder implements StackTraceHolder {

    /**
     * Keep stack trace information to make detailed diagnostics in case of mismatch.
     */
    @Setter
    @Getter
    private StackTraceElement[] trace;

    protected AbstractStackTraceHolder() {
        initTrace();
    }

    public final void initTrace() {
        setTrace(new Throwable().getStackTrace());
    }
}

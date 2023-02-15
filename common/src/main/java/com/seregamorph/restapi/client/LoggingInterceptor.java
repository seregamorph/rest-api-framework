package com.seregamorph.restapi.client;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class LoggingInterceptor extends AbstractLoggingInterceptor<LoggingInterceptor> {

    private final Logger log;

    public LoggingInterceptor() {
        this(LoggerFactory.getLogger(LoggingInterceptor.class));
    }

    @Override
    protected void log(String message, @Nullable Throwable exception) {
        log.info(message, exception);
    }

}

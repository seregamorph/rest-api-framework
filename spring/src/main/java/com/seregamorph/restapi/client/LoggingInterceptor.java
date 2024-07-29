package com.seregamorph.restapi.client;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class LoggingInterceptor extends AbstractLoggingInterceptor<LoggingInterceptor> {

    private final Logger log;

    public LoggingInterceptor() {
        this(LoggerFactory.getLogger(LoggingInterceptor.class));
    }

    @Override
    protected void logSuccess(String message) {
        log.info(message);
    }

    @Override
    protected void logFailure(String message, Throwable exception) {
        log.warn(message, exception);
    }
}

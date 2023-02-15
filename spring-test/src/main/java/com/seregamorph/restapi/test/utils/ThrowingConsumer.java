package com.seregamorph.restapi.test.utils;

@FunctionalInterface
public interface ThrowingConsumer<T> {

    void accept(T t) throws Exception;
}

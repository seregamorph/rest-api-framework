package com.seregamorph.restapi.test.utils;

import java.io.Serializable;

/**
 * Note: it is intended that this functional interface extends Serializable.
 *
 * @see TestLambdaUtils#unreferenceLambdaMethod(Serializable)
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R> extends Serializable {

    R apply(T arg1, U arg2) throws Exception;
}

package com.actoneye.util.sql.lambda;

/**
 * Functional interface for transforming instance of one type to another
 */
@FunctionalInterface
public interface TypeTransformer<T,K> {

    K transform(T instance);

}

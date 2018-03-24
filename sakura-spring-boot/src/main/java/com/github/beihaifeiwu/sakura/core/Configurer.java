package com.github.beihaifeiwu.sakura.core;

@FunctionalInterface
public interface Configurer<T> {

    void configure(T object) throws Exception;

}
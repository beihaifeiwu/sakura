package sakura.spring.core;

@FunctionalInterface
public interface Configurer<T> {

    void configure(T object) throws Exception;

}
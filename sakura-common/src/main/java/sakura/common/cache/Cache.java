package sakura.common.cache;

import sakura.common.annotation.Nullable;

import java.util.function.Function;

/**
 * Cache interface.
 */
public interface Cache<K, V> {

    /**
     * Adds an object to the cache.
     *
     * @see Cache#put(Object, Object, long)
     */
    void put(K key, V object);

    void put(K key, V object, long timeout);

    /**
     * Retrieves an object from the cache. Returns <code>null</code> if object
     * is not longer in cache or if it is expired.
     */
    @Nullable
    V get(K key);

    @Nullable
    V get(K key, Function<K, V> function);

    /**
     * Removes an object from the cache.
     */
    void remove(K key);

    /**
     * Clears current cache.
     */
    void clear();

    /**
     * Returns current cache size.
     */
    int size();

    /**
     * Returns <code>true</code> if cache is empty.
     */
    boolean isEmpty();

}
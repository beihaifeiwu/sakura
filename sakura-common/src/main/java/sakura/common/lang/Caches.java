package sakura.common.lang;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import jodd.cache.*;
import lombok.experimental.UtilityClass;

/**
 * <ul>
 * <li>FIFO: first in first out</li>
 * <li>LFU: least frequently used</li>
 * <li>LRU: least recently used</li>
 * <li>Timed: Not limited by size, objects are removed only when they are expired.</li>
 * <li>Type: Class to instance.</li>
 * </ul>
 * <p>
 * Created by haomu on 2018/4/28.
 */
@UtilityClass
public class Caches {

    private static final NoCache NO_CACHE = new NoCache();

    @SuppressWarnings("unchecked")
    public static <K, V> NoCache<K, V> noCache() {
        return NO_CACHE;
    }

    public static <K, V> FIFOCache<K, V> newFIFOCache(int cacheSize) {
        return new FIFOCache<>(cacheSize);
    }

    public static <K, V> FIFOCache<K, V> newFIFOCache(int cacheSize, long timeout) {
        return new FIFOCache<>(cacheSize, timeout);
    }

    public static <K, V> LFUCache<K, V> newLFUCache(int cacheSize) {
        return new LFUCache<>(cacheSize);
    }

    public static <K, V> LFUCache<K, V> newLFUCache(int cacheSize, long timeout) {
        return new LFUCache<>(cacheSize, timeout);
    }

    public static <K, V> LRUCache<K, V> newLRUCache(int cacheSize) {
        return new LRUCache<>(cacheSize);
    }

    public static <K, V> LRUCache<K, V> newLRUCache(int cacheSize, long timeout) {
        return new LRUCache<>(cacheSize, timeout);
    }

    public static FileLFUCache newFileLFUCache(int maxSize) {
        return new FileLFUCache(maxSize);
    }

    public static FileLFUCache newFileLFUCache(int maxSize, int maxFileSize) {
        return new FileLFUCache(maxSize, maxFileSize);
    }

    public static FileLRUCache newFileLRUCache(int maxSize) {
        return new FileLRUCache(maxSize);
    }

    public static FileLRUCache newFileLRUCache(int maxSize, int maxFileSize) {
        return new FileLRUCache(maxSize, maxFileSize);
    }

    public static <K, V> TimedCache<K, V> newTimedCache(long timeout) {
        return new TimedCache<>(timeout);
    }

    public static <T> TypeCache<T> newTypeCache(boolean weak, boolean sync) {
        TypeCache.Implementation impl;
        impl = weak
                ? (sync ? TypeCache.Implementation.SYNC_WEAK : TypeCache.Implementation.WEAK)
                : (sync ? TypeCache.Implementation.SYNC_MAP : TypeCache.Implementation.MAP);
        return new TypeCache<>(impl);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> CacheBuilder<K, V> newGuavaCacheBuilder(String cacheSpec) {
        CacheBuilderSpec spec = CacheBuilderSpec.parse(cacheSpec);
        return (CacheBuilder<K, V>) CacheBuilder.from(spec);
    }

    public static <K, V> Cache<K, V> newGuavaCache(String cacheSpec) {
        return newGuavaCacheBuilder(cacheSpec).build();
    }

}

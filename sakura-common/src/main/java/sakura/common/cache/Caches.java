package sakura.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import jodd.cache.*;
import lombok.experimental.UtilityClass;
import lombok.val;

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

    public static <K, V> Cache<K, V> newFIFOCache(int cacheSize) {
        return newFIFOCache(cacheSize, 0);
    }

    public static <K, V> Cache<K, V> newFIFOCache(int cacheSize, long timeout) {
        val cache = new FIFOCache<K, V>(cacheSize, timeout);
        return new JoddCacheWrapper<>(cache);
    }

    public static <K, V> Cache<K, V> newLFUCache(int cacheSize) {
        return newLFUCache(cacheSize, 0);
    }

    public static <K, V> Cache<K, V> newLFUCache(int cacheSize, long timeout) {
        val cache = new LFUCache<K, V>(cacheSize, timeout);
        return new JoddCacheWrapper<>(cache);
    }

    public static <K, V> Cache<K, V> newLRUCache(int cacheSize) {
        return newLRUCache(cacheSize, 0);
    }

    public static <K, V> Cache<K, V> newLRUCache(int cacheSize, long timeout) {
        val cache = new LRUCache<K, V>(cacheSize, timeout);
        return new JoddCacheWrapper<>(cache);
    }

    public static <K, V> Cache<K, V> newTimedCache(long timeout) {
        val cache = new TimedCache<K, V>(timeout);
        return new JoddCacheWrapper<>(cache);
    }

    public static <K, V> Cache<K, V> newNoCache() {
        val cache = new NoCache<K, V>();
        return new JoddCacheWrapper<>(cache);
    }

    public static <T> TypeCache<T> newTypeCache(boolean weak, boolean sync) {
        val impl = weak
                ? (sync ? TypeCache.Implementation.SYNC_WEAK : TypeCache.Implementation.WEAK)
                : (sync ? TypeCache.Implementation.SYNC_MAP : TypeCache.Implementation.MAP);
        return new TypeCache<>(impl);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Cache<K, V> newGuavaCache(String cacheSpec) {
        val spec = CacheBuilderSpec.parse(cacheSpec);
        val builder = (CacheBuilder<K, V>) CacheBuilder.from(spec);
        return new GuavaCacheWrapper<>(builder.build());
    }

}

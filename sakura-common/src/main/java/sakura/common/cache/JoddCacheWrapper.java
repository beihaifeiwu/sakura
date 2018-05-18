package sakura.common.cache;

import lombok.RequiredArgsConstructor;
import sakura.common.lang.annotation.Nullable;

import java.util.function.Function;

/**
 * Created by haomu on 2018/5/11.
 */
@RequiredArgsConstructor
public class JoddCacheWrapper<K, V> implements Cache<K, V> {

    private final jodd.cache.Cache<K, V> cache;

    @Override
    public void put(K key, V object) {
        cache.put(key, object);
    }

    @Override
    public void put(K key, V object, long timeout) {
        cache.put(key, object, timeout);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Nullable
    @Override
    public V get(K key, Function<K, V> function) {
        V v = cache.get(key);
        if (v == null) {
            synchronized (this) {
                v = cache.get(key);
                if (v == null) {
                    v = function.apply(key);
                    if (v != null) {
                        cache.put(key, v);
                    }
                }
            }
        }
        return v;
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        if (cache.size() > 0) {
            cache.prune();
        }
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        if (!cache.isEmpty()) {
            cache.prune();
        }
        return cache.isEmpty();
    }

}

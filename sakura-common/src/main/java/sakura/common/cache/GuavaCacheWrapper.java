package sakura.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sakura.common.lang.annotation.Nullable;

import java.util.function.Function;

/**
 * Created by haomu on 2018/5/11.
 */
@RequiredArgsConstructor
public class GuavaCacheWrapper<K, V> implements Cache<K, V> {

    private final com.google.common.cache.Cache<K, V> cache;

    @Override
    public void put(K key, V object) {
        cache.put(key, object);
    }

    @Override
    public void put(K key, V object, long timeout) {
        cache.put(key, object);
    }

    @Nullable
    @Override
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    @Nullable
    @SneakyThrows
    @Override
    public V get(K key, Function<K, V> function) {
        return cache.get(key, () -> function.apply(key));
    }

    @Override
    public void remove(K key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public int size() {
        return (int) cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.size() <= 0;
    }

}

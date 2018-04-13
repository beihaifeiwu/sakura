package sakura.spring.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.ReflectionUtils;
import sakura.spring.annotation.SpringCache;

import java.lang.reflect.Field;

/**
 * Created by liupin on 2017/6/19.
 */
@Slf4j
public class SpringCacheAnnotationProcessor extends AbstractAnnotationProcessor {

    private final CacheManager cacheManager;

    public SpringCacheAnnotationProcessor(CacheManager cacheManager) {
        super(Members.FIELDS, Phase.PRE_INIT,
                new AnnotationFilter(SpringCache.class, AnnotationFilter.INJECTABLE_FIELDS));
        this.cacheManager = cacheManager;
    }

    @Override
    protected void withField(Object bean, String beanName, Class<?> targetClass, Field field) {
        ReflectionUtils.makeAccessible(field);
        Object value = ReflectionUtils.getField(field, bean);
        if (value != null) {
            return;
        }

        SpringCache annotation = field.getAnnotation(SpringCache.class);
        Class<?> type = field.getType();
        Cache cache = cacheManager.getCache(annotation.value());
        if (cache == null) {
            log.warn("Cache {} cannot be retrieved, Please check your configuration", annotation.value());
            return;
        }

        if (type.isAssignableFrom(cache.getClass())) {
            ReflectionUtils.setField(field, bean, cache);
        } else if (type.isAssignableFrom(cache.getNativeCache().getClass())) {
            ReflectionUtils.setField(field, bean, cache.getNativeCache());
        } else {
            log.warn("Unsupported cache {} with type {}", annotation.value(), type);
        }
    }
}

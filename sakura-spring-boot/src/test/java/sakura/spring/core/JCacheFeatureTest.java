package sakura.spring.core;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import sakura.spring.test.SakuraSpringTest;
import sakura.spring.test.TestBean;
import sakura.spring.annotation.SpringCache;

import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by liupin on 2017/2/6.
 */
@TestPropertySource(properties = "spring.cache.cache-names=test")
public class JCacheFeatureTest extends SakuraSpringTest {

    @Autowired
    private JCacheTestDemo demo;

    @Autowired
    private CacheManager cacheManager;

    @SpringCache("test")
    private Cache cache;

    @Test
    public void testCommonUsage() {
        @Cleanup("stop") Stopwatch stopwatch = Stopwatch.createUnstarted();

        Cache test = cacheManager.getCache("test");
        assertThat(test).isNotNull().isEqualTo(cache);

        String cacheItem = "O(∩_∩)O哈哈哈~";
        stopwatch.reset().start();
        String result = demo.longTimeSearch(cacheItem);
        assertThat(result).isNotBlank().isEqualTo(cacheItem);
        assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS)).isGreaterThan(1000);

        stopwatch.reset().start();
        result = demo.longTimeSearch(cacheItem);
        assertThat(result).isNotBlank().isEqualTo(cacheItem);
        assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS)).isLessThan(1000);

        String cachedResult = test.get(cacheItem, String.class);
        assertThat(cachedResult).isNotBlank().isEqualTo(result);

        demo.deleteResult(cacheItem);
        cachedResult = test.get(cacheItem, String.class);
        assertThat(cachedResult).isNull();
    }

    @Test
    public void testCacheSpecificKey() {
        @Cleanup("stop") Stopwatch stopwatch = Stopwatch.createUnstarted();

        Cache test = cacheManager.getCache("test");
        assertThat(test).isNotNull().isEqualTo(cache);

        String key = "<。)#)))≦";
        stopwatch.reset().start();
        String result = demo.loadByInput(key, Double.MAX_EXPONENT);
        assertThat(result).isNotBlank().isEqualTo(key);
        assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS)).isGreaterThan(1000);

        stopwatch.reset().start();
        result = demo.loadByInput(key, Double.BYTES);
        assertThat(result).isNotBlank().isEqualTo(key);
        assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS)).isLessThan(1000);

        String cachedResult = test.get(key, String.class);
        assertThat(cachedResult).isNotBlank().isEqualTo(result);

        demo.deleteResult(key);
        cachedResult = test.get(key, String.class);
        assertThat(cachedResult).isNull();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo implements Serializable {
        private long id;
        private String username;
        private String password;
        private String address;
    }

    @TestBean
    public static class JCacheTestDemo {

        @CacheResult(cacheName = "test")
        String longTimeSearch(String input) {
            System.out.println("Start calculate for " + input);
            Uninterruptibles.sleepUninterruptibly(1100, TimeUnit.MILLISECONDS);
            return input;
        }

        @CacheRemove(cacheName = "test")
        void deleteResult(String input) {
            System.out.println("Delete result for " + input);
        }

        @CacheResult(cacheName = "test")
        String loadByInput(@CacheKey String key, Object noBigDeal) {
            System.out.println("Start load for " + key + " (" + noBigDeal + ")");
            Uninterruptibles.sleepUninterruptibly(1100, TimeUnit.MILLISECONDS);
            return key;
        }

    }
}

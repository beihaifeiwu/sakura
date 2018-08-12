package sakura.common.cache;

import lombok.val;
import org.junit.Test;
import sakura.common.AbstractTest;
import sakura.common.lang.Threads;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by haomu on 2018/5/11.
 */
public class JoddCacheTest extends AbstractTest {

    @Test
    public void testFIFOCache() {
        val cache = Caches.<String, String>newFIFOCache(3, 100);
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");
        cache.put("d", "D");

        assertNull(cache.get("a"));
        assertEquals("B", cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertEquals("D", cache.get("d"));

        Threads.sleepDeadly(101, TimeUnit.MILLISECONDS);
        assertTrue(cache.isEmpty());
        assertEquals(0, cache.size());
    }

    @Test
    public void testLFUCache() {
        val cache = Caches.<String, String>newLFUCache(3, 100);
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        cache.get("b");
        cache.get("b");
        cache.get("c");
        cache.get("c");
        cache.get("a");

        cache.put("d", "D");

        assertNull(cache.get("a"));
        assertEquals("B", cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertEquals("D", cache.get("d"));

        Threads.sleepDeadly(101, TimeUnit.MILLISECONDS);
        assertTrue(cache.isEmpty());
        assertEquals(0, cache.size());
    }

    @Test
    public void testLRUCache() {
        val cache = Caches.<String, String>newLRUCache(3, 100);
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        cache.get("a");
        cache.get("a");
        cache.get("b");
        cache.get("c");

        cache.put("d", "D");

        assertNull(cache.get("a"));
        assertEquals("B", cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertEquals("D", cache.get("d"));

        Threads.sleepDeadly(101, TimeUnit.MILLISECONDS);
        assertTrue(cache.isEmpty());
        assertEquals(0, cache.size());
    }

    @Test
    public void testTimedCache() {
        val cache = Caches.<String, String>newTimedCache(100);
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");
        assertEquals("A", cache.get("a"));
        assertEquals("B", cache.get("b"));
        assertEquals("C", cache.get("c"));
        Threads.sleepDeadly(101, TimeUnit.MILLISECONDS);
        assertTrue(cache.isEmpty());
        assertEquals(0, cache.size());
    }

}

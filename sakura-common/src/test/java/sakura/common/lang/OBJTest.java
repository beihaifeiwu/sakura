package sakura.common.lang;

import com.google.common.collect.Iterators;
import lombok.val;
import org.junit.Test;
import sakura.common.AbstractTest;

import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static sakura.common.lang.OBJ.*;

/**
 * Created by haomu on 2018/5/2.
 */
public class OBJTest extends AbstractTest {

    @Test
    public void testIsEmpty() {
        assertTrue(isEmpty(null));
        assertTrue(isEmpty(""));
        assertTrue(isEmpty(new byte[0]));
        assertTrue(isEmpty(new ArrayList<>()));
        assertTrue(isEmpty(new HashMap<>()));
        assertTrue(isEmpty(Collections.emptyIterator()));
    }

    @Test
    public void testIsNotEmpty() {
        assertTrue(isNotEmpty(" "));
        assertTrue(isNotEmpty(new byte[3]));
        assertTrue(isNotEmpty(Arrays.asList(1, 2)));
        assertTrue(isNotEmpty(Collections.singletonMap("key", true)));
        assertTrue(isNotEmpty(Iterators.singletonIterator("")));
    }

    @Test
    public void testToArray() {
        assertNotNull(toArray(null));
        assertTrue(isEmpty(toArray(null)));

        Integer[] objArray = new Integer[]{1, 2, 3, 4};
        assertSame(objArray, toArray(objArray));

        assertEquals(1, toArray(Collections.singleton(1))[0]);
        assertEquals(1, toArray(Iterators.singletonIterator(1))[0]);

        val entry = toArray(Collections.singletonMap("key", "value"))[0];
        assertThat(entry, instanceOf(Map.Entry.class));
        assertEquals("key", ((Map.Entry) entry).getKey());

        int[] ints = new int[]{1, 2, 3, 4};
        assertNotSame(ints, toArray(ints));
        assertEquals(toArray(ints)[0], 1);
        assertEquals(toArray(ints)[3], 4);
    }

    @Test
    public void testFirstNonNull() {
        assertNull(firstNonNull());
        assertNull(firstNonNull(null, null));
        assertNull(firstNonNull((Object) null));
        assertNull(firstNonNull((Object[]) null));

        assertEquals("", firstNonNull(null, ""));
        assertEquals("", firstNonNull(null, null, ""));
        assertEquals("zz", firstNonNull(null, "zz"));
        assertEquals("abc", firstNonNull("abc", "a", null));
        assertEquals("xyz", firstNonNull(null, "xyz", "a", null));

        assertSame(Boolean.TRUE, firstNonNull(Boolean.TRUE));
    }

    @Test
    public void testIdentityToString() {
        assertNull(identityToString(null));

        Integer i = 90;
        String expected = "java.lang.Integer@" + Integer.toHexString(System.identityHashCode(i));
        assertEquals(expected, identityToString(i));
    }

    @Test
    public void testMode() {
        assertNull(mode((Object[]) null));
        assertNull(mode());
        assertNull(mode("foo", "bar", "baz"));
        assertNull(mode("foo", "bar", "baz", "foo", "bar"));
        assertEquals("foo", mode("foo", "bar", "baz", "foo"));
        assertEquals(9, mode("foo", "bar", "baz", 9, 10, 9));
    }

}

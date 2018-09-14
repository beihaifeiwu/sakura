package sakura.common;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static sakura.common.Util.isBlank;
import static sakura.common.Util.isEmpty;

/**
 * Created by haomu on 2018/7/8.
 */
@SuppressWarnings("ConstantConditions")
public class UtilEmptyTest extends AbstractTest {

    @Test
    public void testString() {
        assertTrue(isEmpty(""));
        assertFalse(isEmpty(" "));
        assertFalse(isEmpty(" abc"));

        assertTrue(isBlank(""));
        assertTrue(isBlank(" "));
        assertFalse(isBlank("abc "));
    }

    @Test
    public void testArray() {
        assertTrue(isEmpty((long[]) null));
        assertTrue(isEmpty(new long[0]));
        assertFalse(isEmpty(new long[]{1}));

        assertTrue(isEmpty((int[]) null));
        assertTrue(isEmpty(new int[0]));
        assertFalse(isEmpty(new int[]{1}));

        assertTrue(isEmpty((short[]) null));
        assertTrue(isEmpty(new short[0]));
        assertFalse(isEmpty(new short[]{1}));

        assertTrue(isEmpty((char[]) null));
        assertTrue(isEmpty(new char[0]));
        assertFalse(isEmpty(new char[]{1}));

        assertTrue(isEmpty((byte[]) null));
        assertTrue(isEmpty(new byte[0]));
        assertFalse(isEmpty(new byte[]{1}));

        assertTrue(isEmpty((double[]) null));
        assertTrue(isEmpty(new double[0]));
        assertFalse(isEmpty(new double[]{1}));

        assertTrue(isEmpty((float[]) null));
        assertTrue(isEmpty(new float[0]));
        assertFalse(isEmpty(new float[]{1}));

        assertTrue(isEmpty((boolean[]) null));
        assertTrue(isEmpty(new boolean[0]));
        assertFalse(isEmpty(new boolean[]{false}));

        assertTrue(isEmpty((Object[]) null));
        assertTrue(isEmpty(new Object[0]));
        assertFalse(isEmpty(new String[]{"a"}));
    }

    @Test
    public void testCollection() {
        assertTrue(isEmpty((Collection) null));
        assertTrue(isEmpty(Collections.EMPTY_LIST));
        assertTrue(isEmpty(Collections.EMPTY_SET));
        assertFalse(isEmpty(Collections.singleton(1)));
        assertFalse(isEmpty(Collections.singletonList(1)));
    }

    @Test
    public void testMap() {
        assertTrue(isEmpty((Map) null));
        assertTrue(isEmpty(Collections.EMPTY_MAP));
        assertTrue(isEmpty(Maps.newHashMap()));
        assertFalse(isEmpty(Collections.singletonMap("a", "b")));
    }

    @Test
    public void testObject() {
        assertTrue(isEmpty((Object) null));
        assertTrue(isEmpty(Iterators.forArray()));
        assertFalse(isEmpty(Iterators.singletonIterator(1)));
    }

}

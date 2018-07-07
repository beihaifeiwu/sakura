package sakura.common.lang;

import com.google.common.collect.Iterators;
import lombok.val;
import org.junit.Test;
import sakura.common.AbstractTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static sakura.common.lang.Objects.*;


/**
 * Created by haomu on 2018/5/2.
 */
public class ObjectsTest extends AbstractTest {

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
        assertThat(toArray(null)).isNotNull().isEmpty();

        Integer[] objArray = new Integer[]{1, 2, 3, 4};
        assertThat(toArray(objArray)).isSameAs(objArray);

        assertThat(toArray(Collections.singleton(1))).containsExactly(1);
        assertThat(toArray(Iterators.singletonIterator(1))).containsExactly(1);

        val entry = toArray(Collections.singletonMap("key", "value"))[0];
        assertThat(entry).isInstanceOf(Map.Entry.class).hasFieldOrPropertyWithValue("key", "key");

        val ints = new int[]{1, 2, 3, 4};
        assertThat(toArray(ints)).isNotSameAs(ints).containsExactly(1, 2, 3, 4);

        assertThat(toArray(1)).isNotEmpty().containsExactly(1);
    }

    @Test
    public void testToIterable() {
        assertThat(toIterable(null)).isNotNull().isEmpty();

        val singleton = Collections.singleton(1);
        assertThat(toIterable(singleton)).isSameAs(singleton).containsExactly(1);

        val iterator = Iterators.singletonIterator(1);
        assertThat(toIterable(iterator)).containsExactly(1);

        val map = Collections.singletonMap("key", "value");
        assertThat(toIterable(map)).isSameAs(map.entrySet());

        val ints = new int[]{1, 2, 3, 4};
        assertThat(toIterable(ints)).isNotSameAs(ints).containsExactly(1, 2, 3, 4);

        assertThat(toIterable(1)).isNotEmpty().containsExactly(1);
    }

}

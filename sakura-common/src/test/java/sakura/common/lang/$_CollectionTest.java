package sakura.common.lang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by haomu on 2018/7/9.
 */
public class $_CollectionTest {

    @Test
    public void testList() {
        assertThat($.newList(2)).isNotNull().isInstanceOf(ArrayList.class);

        assertThat($.list()).isNotNull().isSameAs(Collections.EMPTY_LIST);
        assertThat($.list(1)).isInstanceOf(ImmutableList.class).containsExactly(1);
        assertThat($.list(1, 2, 3)).isInstanceOf(ImmutableList.class).containsExactly(1, 2, 3);

        assertThat($.list(Arrays.asList(1, 2, 3))).isInstanceOf(ImmutableList.class).containsExactly(1, 2, 3);
    }

    @Test
    public void testSet() {
        assertThat($.newSet(2)).isNotNull().isInstanceOf(HashSet.class);

        assertThat($.set()).isNotNull().isSameAs(Collections.EMPTY_SET);
        assertThat($.set(1)).isInstanceOf(ImmutableSet.class).containsExactly(1);
        assertThat($.set(1, 2, 3)).isInstanceOf(ImmutableSet.class).containsExactly(1, 2, 3);

        assertThat($.set(Arrays.asList(1, 2, 3))).isInstanceOf(ImmutableSet.class).containsExactly(1, 2, 3);
    }

    @Test
    public void testMap() {
        assertThat($.newMap(2)).isNotNull().isInstanceOf(HashMap.class);
    }

}

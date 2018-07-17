package sakura.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by haomu on 2018/7/9.
 */
public class $_CollectionTest extends AbstractTest {

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
        assertThat($.map("a", 1)).isInstanceOf(ImmutableMap.class).hasSize(1).containsEntry("a", 1);
        assertThat($.map("a", 1, "b", 2)).hasSize(2).containsEntry("a", 1).containsEntry("b", 2);
        assertThat($.map("a", 1, "b", 2, "c", 3)).hasSize(3).containsEntry("a", 1).containsEntry("b", 2).containsEntry("c", 3);
    }

}

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
public class UtilCollectionTest extends AbstractTest {

    @Test
    public void testList() {
        assertThat(Util.newList(2)).isNotNull().isInstanceOf(ArrayList.class);

        assertThat(Util.list()).isNotNull().isSameAs(Collections.EMPTY_LIST);
        assertThat(Util.list(1)).isInstanceOf(ImmutableList.class).containsExactly(1);
        assertThat(Util.list(1, 2, 3)).isInstanceOf(ImmutableList.class).containsExactly(1, 2, 3);

        assertThat(Util.list(Arrays.asList(1, 2, 3))).isInstanceOf(ImmutableList.class).containsExactly(1, 2, 3);
    }

    @Test
    public void testSet() {
        assertThat(Util.newSet(2)).isNotNull().isInstanceOf(HashSet.class);

        assertThat(Util.set()).isNotNull().isSameAs(Collections.EMPTY_SET);
        assertThat(Util.set(1)).isInstanceOf(ImmutableSet.class).containsExactly(1);
        assertThat(Util.set(1, 2, 3)).isInstanceOf(ImmutableSet.class).containsExactly(1, 2, 3);

        assertThat(Util.set(Arrays.asList(1, 2, 3))).isInstanceOf(ImmutableSet.class).containsExactly(1, 2, 3);
    }

    @Test
    public void testMap() {
        assertThat(Util.newMap(2)).isNotNull().isInstanceOf(HashMap.class);
        assertThat(Util.map("a", 1)).isInstanceOf(ImmutableMap.class).hasSize(1).containsEntry("a", 1);
        assertThat(Util.map("a", 1, "b", 2)).hasSize(2).containsEntry("a", 1).containsEntry("b", 2);
        assertThat(Util.map("a", 1, "b", 2, "c", 3)).hasSize(3).containsEntry("a", 1).containsEntry("b", 2).containsEntry("c", 3);
    }

}

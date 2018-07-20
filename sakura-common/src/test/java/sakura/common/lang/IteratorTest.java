package sakura.common.lang;

import com.google.common.collect.Iterators;
import lombok.val;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import sakura.common.AbstractTest;
import sakura.common.lang.iterator.AbstractBatchIterator;
import sakura.common.lang.iterator.PageableIterator;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by haomu on 2018/5/4.
 */
public class IteratorTest extends AbstractTest {

    @Test
    public void testBatchIterator() {
        val array = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        val iterator = Iterators.partition(Arrays.asList(array).iterator(), 2);
        val batch = new AbstractBatchIterator<Integer>() {
            @Override
            protected Iterable<Integer> computeNextBatch() {
                int random = RandomUtils.nextInt();
                if (random % 3 == 0) return null;
                if (random % 3 == 1) return Collections.emptyList();
                return iterator.hasNext() ? iterator.next() : endOfBatchData();
            }
        };
        assertThat(batch).containsExactly(array);
    }

    @Test
    public void testPageableIterator() {
        val array = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        val pageable = new PageableIterator<Integer, Integer>(
                (i, n) -> {
                    if (i >= array.length) return Collections.emptyList();
                    return Arrays.asList(array).subList(i, Math.min(i + n, array.length));
                },
                (c, i) -> i + c.size(), 0, 2);
        assertThat(pageable).containsExactly(array);
    }

}

package sakura.common.lang;

import com.google.common.collect.Iterators;
import lombok.val;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import sakura.common.lang.iterator.AbstractBatchIterator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by haomu on 2018/5/4.
 */
public class IteratorTest {

    @Test
    public void testBatchIterator() {
        val array = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        val iterator = Iterators.partition(Arrays.asList(array).iterator(), 2);
        val batch = new AbstractBatchIterator<Integer>() {
            @Override
            protected Collection<Integer> computeNextBatch() {
                int random = RandomUtils.nextInt();
                if (random % 3 == 0) return null;
                if (random % 3 == 1) return Collections.emptyList();
                return iterator.hasNext() ? iterator.next() : endOfBatchData();
            }
        };
        assertThat(batch).containsExactly(array);
    }

}

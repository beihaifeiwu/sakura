package sakura.common.reactor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by haomu on 2018/7/19.
 */
@FixMethodOrder
public class ReactorParallelTest {

    private List<Integer> input;
    private List<Integer> output;
    private Set<String> threads;

    private volatile int batch;

    @Before
    public void setUp() {
        input = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            input.add(i);
        }
        threads = Sets.newConcurrentHashSet();
        batch = 0;
    }

    @Test
    public void testCallWithEmptyInput() {
        batch = 100;
        output = Reactor.call(new ArrayList<Integer>(), batch, this::transform);
        assertThat(output).isNotNull().isEmpty();
        assertThat(threads).hasSize(0);

        output = Reactor.call((List<Integer>) null, batch, this::transform);
        assertThat(output).isNotNull().isEmpty();
        assertThat(threads).hasSize(0);
    }

    @Test
    public void testCallWithOneBatch() {
        batch = 100;
        output = Reactor.call(input, batch, this::transform);
        assertThat(output).isNotEmpty().hasSameElementsAs(input);
        assertThat(threads).hasSize(1).containsExactly(Thread.currentThread().getName());
        threads.clear();

        batch = 200;
        output = Reactor.call(input, batch, this::transform);
        assertThat(output).isNotEmpty().hasSameElementsAs(input);
        assertThat(threads).hasSize(1).containsExactly(Thread.currentThread().getName());
    }

    @Test
    public void testCallWithBatches() {
        batch = 5;
        output = Reactor.call(input, batch, this::transform);
        output.sort(Comparator.naturalOrder());
        assertThat(output).isNotEmpty().hasSameElementsAs(input);
        assertThat(threads).hasSize(Runtime.getRuntime().availableProcessors());
        threads.clear();

        batch = 7;
        output = Reactor.call(input, batch, this::transform);
        output.sort(Comparator.naturalOrder());
        assertThat(output).isNotEmpty().hasSameElementsAs(input);
        assertThat(threads).hasSize(Runtime.getRuntime().availableProcessors());
    }

    @Test(expected = ExpectedException.class)
    public void testCallWithException() {
        output = Reactor.call(input, 5, i -> {
            throw new ExpectedException();
        });
        assertThat(threads).hasSize(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void testRunWithEmptyInput() {
        batch = 100;
        Reactor.run(null, batch, this::transform);
        assertThat(threads).hasSize(0);

        Reactor.run(Collections.emptyList(), batch, this::transform);
        assertThat(threads).hasSize(0);
    }

    @Test
    public void testRunWithOneBatch() {
        batch = 100;
        Reactor.run(input, batch, this::transform);
        assertThat(threads).hasSize(1).containsExactly(Thread.currentThread().getName());
        threads.clear();

        batch = 200;
        Reactor.run(input, batch, this::transform);
        assertThat(threads).hasSize(1).containsExactly(Thread.currentThread().getName());
        threads.clear();
    }

    @Test
    public void testRunWithBatches() {
        batch = 5;
        Reactor.run(input, batch, this::transform);
        assertThat(threads).hasSize(Runtime.getRuntime().availableProcessors());
        threads.clear();

        batch = 7;
        Reactor.run(input, batch, this::transform);
        assertThat(threads).hasSize(Runtime.getRuntime().availableProcessors());
    }

    @Test(expected = ExpectedException.class)
    public void testRunWithException() {
        Reactor.run(input, 5, i -> {
            throw new ExpectedException();
        });
        assertThat(threads).hasSize(Runtime.getRuntime().availableProcessors());
    }

    private <T> List<T> transform(List<T> input) {
        assertThat(batch).isGreaterThanOrEqualTo(input.size());
        String name = Thread.currentThread().getName();
        threads.add(name);
//        System.out.println(name + ": " + input);
        return input;
    }

    private class ExpectedException extends RuntimeException {

    }

}

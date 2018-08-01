package sakura.common.reactor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import sakura.common.AbstractTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by haomu on 2018/7/19.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReactorParallelTest extends AbstractTest {

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

        output = Reactor.call(new ArrayList<Integer>(), this::transform);
        assertThat(output).isNotNull().isEmpty();
        assertThat(threads).hasSize(0);

        output = Reactor.call((List<Integer>) null, this::transform);
        assertThat(output).isNotNull().isEmpty();
        assertThat(threads).hasSize(0);
    }

    @Test
    public void testCallWithBatches() {
        batch = 5;
        output = Reactor.call(input, batch, this::transform);
        output.sort(Comparator.naturalOrder());
        assertThat(output).isNotEmpty().hasSameElementsAs(input);
        assertRunInElasticThreadPool();

        batch = 7;
        output = Reactor.call(input, batch, this::transform);
        output.sort(Comparator.naturalOrder());
        assertThat(output).isNotEmpty().hasSameElementsAs(input);
        assertRunInElasticThreadPool();

        output = Reactor.call(input, this::transform);
        output.sort(Comparator.naturalOrder());
        assertThat(output).isNotEmpty().hasSameElementsAs(input);
        assertRunInElasticThreadPool();
    }

    @Test
    public void testRunWithEmptyInput() {
        batch = 100;
        Reactor.run(null, batch, this::transform);
        assertThat(threads).hasSize(0);

        Reactor.run(Collections.emptyList(), batch, this::transform);
        assertThat(threads).hasSize(0);

        Reactor.run(null, this::transform);
        assertThat(threads).hasSize(0);

        Reactor.run(Collections.emptyList(), this::transform);
        assertThat(threads).hasSize(0);
    }

    @Test
    public void testRunWithBatches() {
        batch = 5;
        Reactor.run(input, batch, this::transform);
        assertRunInElasticThreadPool();

        batch = 7;
        Reactor.run(input, batch, this::transform);
        assertRunInElasticThreadPool();

        Reactor.run(input, this::transform);
        assertRunInElasticThreadPool();
    }

    private void assertRunInElasticThreadPool() {
        int processors = Runtime.getRuntime().availableProcessors();
        assertThat(threads).allMatch(s -> s.startsWith("elastic"));
        assertThat(threads.size()).isBetween(1, processors);
        threads.clear();
    }

    private <T> T transform(T input) {
        String name = Thread.currentThread().getName();
        threads.add(name);
//        System.out.println(name + ": " + input);
        return input;
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

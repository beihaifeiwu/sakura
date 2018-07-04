package sakura.common.reactor;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jooq.lambda.fi.util.function.CheckedConsumer;
import org.jooq.lambda.fi.util.function.CheckedFunction;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sakura.common.lang.$;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by haomu on 2018/4/23.
 */
@UtilityClass
public class Reactor {

    @SneakyThrows
    public static <I> void run(@Nullable Collection<I> inputs,
                               @Nonnegative int partitionSize,
                               CheckedConsumer<List<I>> consumer) {
        if (inputs == null || inputs.isEmpty()) return;

        List<I> list = $.list(inputs);
        partitionSize = Math.max(partitionSize, 1);

        if (list.size() <= partitionSize) {
            consumer.accept(list);
            return;
        }

        val reference = new AtomicReference<Throwable>();
        Flux.fromIterable($.partition(list, partitionSize))
                .parallel()
                .runOn(Schedulers.elastic())
                .doOnNext(is -> {
                    try {
                        consumer.accept(is);
                    } catch (Throwable throwable) {
                        reference.set(throwable);
                    }
                })
                .sequential()
                .blockLast();
        if (reference.get() != null) throw reference.get();
    }

    @SneakyThrows
    public static <I, R> List<R> call(@Nullable Collection<I> inputs,
                                      @Nonnegative int partitionSize,
                                      CheckedFunction<List<I>, List<R>> transformer) {
        if (inputs == null || inputs.isEmpty()) return Collections.emptyList();

        List<I> list = $.list(inputs);
        partitionSize = Math.max(partitionSize, 1);

        if (list.size() <= partitionSize) return transformer.apply(list);

        val reference = new AtomicReference<Throwable>();
        List<R> results = $.newList(inputs.size());
        Flux.fromIterable($.partition(list, partitionSize))
                .parallel()
                .runOn(Schedulers.elastic())
                .map(is -> {
                    try {
                        return transformer.apply(is);
                    } catch (Throwable throwable) {
                        reference.set(throwable);
                    }
                    return Collections.<R>emptyList();
                })
                .sequential()
                .doOnNext(results::addAll)
                .blockLast();

        if (reference.get() != null) throw reference.get();

        return results;
    }

}

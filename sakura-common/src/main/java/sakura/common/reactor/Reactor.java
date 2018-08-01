package sakura.common.reactor;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sakura.common.$;
import sakura.common.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * Created by haomu on 2018/4/23.
 */
@UtilityClass
public class Reactor {

    public static <I> void run(@Nullable Iterable<I> inputs,
                               @Nullable Integer size,
                               @Nullable Consumer<List<I>> consumer) {
        if (inputs == null) return;
        if (consumer == null) return;
        run($.partition(inputs, ObjectUtils.defaultIfNull(size, 1)), consumer);
    }

    public static <I> void run(@Nullable Iterable<I> inputs,
                               @Nullable Consumer<I> consumer) {
        if (inputs == null) return;
        if (consumer == null) return;
        Flux.fromIterable(inputs)
                .parallel()
                .runOn(Schedulers.elastic())
                .doOnNext(consumer)
                .sequential()
                .blockLast();
    }

    public static <I, R> List<R> call(@Nullable Iterable<I> inputs,
                                      @Nullable Integer size,
                                      @Nullable Function<List<I>, List<R>> transformer) {
        if (inputs == null) return emptyList();
        if (transformer == null) return emptyList();
        val parts = $.partition(inputs, ObjectUtils.defaultIfNull(size, 1));
        val result = call(parts, transformer);
        return result.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public static <I, R> List<R> call(@Nullable Iterable<I> inputs,
                                      @Nullable Function<I, R> transformer) {
        if (inputs == null) return emptyList();
        if (transformer == null) return emptyList();
        return Flux.fromIterable(inputs)
                .parallel()
                .runOn(Schedulers.elastic())
                .map(transformer)
                .sequential()
                .collectList()
                .blockOptional()
                .orElse(emptyList());
    }

}

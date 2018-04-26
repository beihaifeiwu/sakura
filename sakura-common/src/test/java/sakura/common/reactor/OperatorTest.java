package sakura.common.reactor;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import sakura.common.lang.Threads;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Reactor中提供了非常丰富的操作符, 有：
 * <ul>
 * <li>用于编程方式自定义生成数据流的create和generate等及其变体方法</li>
 * <li>用于“无副作用的peek”场景的doOnNext、doOnError、doOnComplete、doOnSubscribe、doOnCancel等及其变体方法</li>
 * <li>用于数据流转换的when、and/or、merge、concat、collect、count、repeat等及其变体方法</li>
 * <li>用于过滤/拣选的take、first、last、sample、skip、limitRequest等及其变体方法</li>
 * <li>用于错误处理的timeout、onErrorReturn、onErrorResume、doFinally、retryWhen等及其变体方法</li>
 * <li>用于分批的window、buffer、group等及其变体方法</li>
 * <li>用于线程调度的publishOn和subscribeOn方法</li>
 * </ul>
 * <p>
 * Created by haomu on 2018/4/15.
 */
public class OperatorTest {

    @Test
    public void testErrorHandling() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3)) // 当i为3时会导致异常
                .map(i -> i * i)
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void testErrorReturn() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3))
                .onErrorReturn(0)   // 当发生异常时提供一个缺省值0
                .map(i -> i * i)
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void testErrorResume() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3))
                .onErrorResume(e -> Mono.just(RandomUtils.nextInt(1, 7))) // 提供新的数据流
                .map(i -> i * i)
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void testErrorRetry() throws InterruptedException {
        // retry对于上游Flux是采取的重订阅（re-subscribing）的方式，
        // 因此重试之后实际上已经一个不同的序列了， 发出错误信号的序列仍然是终止了的
        Flux.range(1, 6)
                .map(i -> 10 / (3 - i))
                .retry(1)
                .subscribe(System.out::println, System.err::println);
        Threads.sleepQuietly(100, TimeUnit.MILLISECONDS);  // 确保序列执行完
    }

    @Test
    public void testFilter() {
        Flux<Integer> flux = Flux.range(1, 6)
                .filter(i -> i % 2 == 1)
                .map(i -> i * i);

        StepVerifier.create(flux)
                .expectNext(1, 9, 25)
                .verifyComplete();
    }

    @Test
    public void testMap() {
        Flux<Integer> flux = Flux.range(1, 6).map(i -> i * i);

        StepVerifier.create(flux)
                .expectNext(1, 4, 9, 16, 25, 36)
                .expectComplete();
    }

    @Test
    public void testFlatMap() {
        // 流的合并是异步的，先来先到，并非是严格按照原始序列的顺序
        Flux<String> flux = Flux.just("flux", "mono")
                .flatMap(s -> Flux.fromArray(s.split("\\s*")).delayElements(Duration.ofMillis(100)))
                .doOnNext(System.out::print)
                .doOnComplete(System.out::println);
        StepVerifier.create(flux).expectNextCount(8).verifyComplete();

        // 结果按照原序列顺序排序
        flux = Flux.just("flux", "mono")
                .flatMapSequential(s -> Flux.fromArray(s.split("\\s*")).delayElements(Duration.ofMillis(100)))
                .doOnNext(System.out::print)
                .doOnComplete(System.out::println);
        StepVerifier.create(flux).expectNextCount(8).verifyComplete();
    }

    @Test
    public void testCompose() {
        AtomicInteger ai = new AtomicInteger();
        Function<Flux<String>, Flux<String>> filterAndMap = f -> {
            if (ai.incrementAndGet() == 1) {
                return f.filter(color -> !color.equals("orange"))
                        .map(String::toUpperCase);
            }
            return f.filter(color -> !color.equals("purple"))
                    .map(String::toUpperCase);
        };

        Flux<String> composedFlux =
                Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                        .doOnNext(System.out::println)
                        .compose(filterAndMap);

        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :" + d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: " + d));
    }

    @Test
    public void testTransform() {
        Function<Flux<String>, Flux<String>> filterAndMap =
                f -> f.filter(color -> !color.equals("orange"))
                        .map(String::toUpperCase);

        Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .doOnNext(System.out::println)
                .transform(filterAndMap)
                .subscribe(d -> System.out.println("Subscriber to Transformed MapAndFilter: " + d));
    }

    @Test
    public void testZip() throws InterruptedException {
        String desc = "Zip two sources together, that is to say wait for all the sources to emit one element and combine these elements once into a Tuple2.";
        CountDownLatch latch = new CountDownLatch(3);
        Flux.zip(Flux.fromArray(desc.split("\\s+")),
                Flux.interval(Duration.ofMillis(100)))
                .doOnNext(t -> latch.countDown())
                .subscribe(t -> System.out.println(t.getT1()));
        latch.await(10, TimeUnit.SECONDS);
    }

}

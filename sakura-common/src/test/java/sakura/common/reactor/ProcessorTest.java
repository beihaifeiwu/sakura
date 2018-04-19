package sakura.common.reactor;

import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import sakura.common.lang.Threads;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by haomu on 2018/4/16.
 */
public class ProcessorTest {

    @Test
    public void testHotSequence() {
        UnicastProcessor<String> hotSource = UnicastProcessor.create();

        Flux<String> hotFlux = hotSource.publish()
                .autoConnect()
                .map(String::toUpperCase);

        hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: " + d));

        hotSource.onNext("blue");
        hotSource.onNext("green");

        hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: " + d));

        hotSource.onNext("orange");
        hotSource.onNext("purple");
        hotSource.onComplete();
    }

    @Test
    public void testConnectableFlux() {
        Flux<Integer> source = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("上游收到订阅"));

        ConnectableFlux<Integer> co = source.publish();

        co.subscribe(System.out::println);
        co.subscribe(System.out::println);

        System.out.println("订阅者完成订阅操作");
        Threads.sleepQuitely(500, TimeUnit.MILLISECONDS);
        System.out.println("还没有连接上");

        co.connect();
    }

    @Test
    public void testConnectableFluxAutoConnect() {
        Flux<Integer> source = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("上游收到订阅"));

        // 需要两个订阅者才自动连接
        Flux<Integer> autoCo = source.publish().autoConnect(2);

        autoCo.subscribe(System.out::println);
        System.out.println("第一个订阅者完成订阅操作");
        Threads.sleepQuitely(500, TimeUnit.MILLISECONDS);
        System.out.println("第二个订阅者完成订阅操作");
        autoCo.subscribe(System.out::println);
    }

    @Test
    public void testConnectableFluxRefConnect() {
        Flux<Long> source = Flux.interval(Duration.ofMillis(100))
                .doOnSubscribe(s -> System.out.println("上游收到订阅"))
                .doOnCancel(() -> System.out.println("上游发布者断开连接"));

        Flux<Long> refCounted = source.publish().refCount(2, Duration.ofSeconds(1));

        System.out.println("第一个订阅者订阅");
        Disposable sub1 = refCounted.subscribe(l -> System.out.println("sub1: " + l));

        Threads.sleepQuitely(200, TimeUnit.MILLISECONDS);
        System.out.println("第二个订阅者订阅");
        Disposable sub2 = refCounted.subscribe(l -> System.out.println("sub2: " + l));

        Threads.sleepQuitely(200, TimeUnit.MILLISECONDS);
        System.out.println("第一个订阅者取消订阅");
        sub1.dispose();

        Threads.sleepQuitely(200, TimeUnit.MILLISECONDS);
        System.out.println("第二个订阅者取消订阅");
        sub2.dispose();

        Threads.sleepQuitely(200, TimeUnit.MILLISECONDS);
        System.out.println("第三个订阅者订阅");
        Disposable sub3 = refCounted.subscribe(l -> System.out.println("sub3: " + l));

        Threads.sleepQuitely(200, TimeUnit.MILLISECONDS);
        System.out.println("第三个订阅者取消订阅");
        sub3.dispose();

        Threads.sleepQuitely(1500, TimeUnit.MILLISECONDS);
        System.out.println("第四个订阅者订阅");
        Disposable sub4 = refCounted.subscribe(l -> System.out.println("sub4: " + l));
        Threads.sleepQuitely(200, TimeUnit.MILLISECONDS);
        System.out.println("第五个订阅者订阅");
        Disposable sub5 = refCounted.subscribe(l -> System.out.println("sub5: " + l));
        Threads.sleepQuitely(200, TimeUnit.MILLISECONDS);
    }

}

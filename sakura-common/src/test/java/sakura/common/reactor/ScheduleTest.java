package sakura.common.reactor;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * publishOn会影响链中其后的操作符<br>
 * subscribeOn无论出现在什么位置，都只影响源头的执行环境<br>
 * Flux和Mono的调度操作符subscribeOn和publishOn支持work-stealing<br>
 * <p>
 * Created by haomu on 2018/4/16.
 */
public class ScheduleTest {

    @Test
    public void testDelayElements() {
        Flux.range(0, 10)
                .delayElements(Duration.ofMillis(10))
                .log()
                .blockLast();
    }

    @Test
    public void testParallelFlux() throws InterruptedException {
        Flux.range(1, 10)
                .publishOn(Schedulers.parallel())
                .log().subscribe();
        TimeUnit.MILLISECONDS.sleep(10);

        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())
                .log()
                .subscribe();

        TimeUnit.MILLISECONDS.sleep(10);
    }

    @Test
    public void testScheduling() {
        Flux.range(0, 10)
                .log()
                .publishOn(Schedulers.newParallel("myParallel"))
                .subscribeOn(Schedulers.newElastic("myElastic"))
                .blockLast();

        Flux.range(0, 10)
                .publishOn(Schedulers.newParallel("myParallel"))
                .subscribeOn(Schedulers.newElastic("myElastic"))
                .log()
                .blockLast();
    }

    @Test
    public void testSyncToAsync() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Mono.fromCallable(this::getStringSync)
                .log()
                .subscribeOn(Schedulers.elastic())  // 将任务调度到Schedulers内置的弹性线程池执行
                .subscribe(System.out::println, null, latch::countDown);
        latch.await(10, TimeUnit.SECONDS);
    }

    private String getStringSync() {
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        return "Hello, Reactor!";
    }

}

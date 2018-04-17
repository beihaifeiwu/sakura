package sakura.common.reactor;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sakura.common.reactor.event.MyEvent;
import sakura.common.reactor.event.MyEventListener;
import sakura.common.reactor.event.MyEventSource;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by haomu on 2018/4/16.
 */
public class BackpressureTest {

    private Flux<MyEvent> fastPublisher;
    private SlowSubscriber slowSubscriber;
    private MyEventSource eventSource;
    private CountDownLatch latch;

    /**
     * 准备工作。
     */
    @Before
    public void setup() {
        latch = new CountDownLatch(1);
        slowSubscriber = new SlowSubscriber();
        eventSource = new MyEventSource();
    }

    /**
     * 触发订阅，使用CountDownLatch等待订阅者处理完成。
     */
    @After
    public void subscribe() {
        fastPublisher.subscribe(slowSubscriber);
        generateEvent();
        Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.MINUTES);
    }

    @Test
    public void testOnBackPressureBuffer() {
        fastPublisher = createFlux()
                .onBackpressureBuffer()   // BUFFER
                .doOnRequest(n -> System.out.println("         ===  request: " + n + " ==="))
                .publishOn(Schedulers.single(), 1);
    }

    @Test
    public void testOnBackPressureDrop() {
        fastPublisher = createFlux()
                .onBackpressureDrop()     // DROP
                .doOnRequest(n -> System.out.println("         ===  request: " + n + " ==="))
                .publishOn(Schedulers.single(), 1);
    }

    @Test
    public void testOnBackPressureLatest() {
        fastPublisher = createFlux()
                .onBackpressureLatest()   // LATEST
                .doOnRequest(n -> System.out.println("         ===  request: " + n + " ==="))
                .publishOn(Schedulers.single(), 1);
    }

    @Test
    public void testOnBackPressureError() {
        fastPublisher = createFlux()
                .onBackpressureError()    // ERROR
                .doOnRequest(n -> System.out.println("         ===  request: " + n + " ==="))
                .publishOn(Schedulers.single(), 1);
    }

    private Flux<MyEvent> createFlux() {
        return Flux.create(sink -> eventSource.register(new MyEventListener() {
            @Override
            public void onNewEvent(MyEvent event) {
                System.out.println("publish >>> " + event.getMessage());
                sink.next(event);
            }

            @Override
            public void onEventStopped() {
                sink.complete();
            }
        }));
    }

    private void generateEvent() {
        for (int i = 0; i < 20; i++) {
            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
            eventSource.newEvent(new MyEvent(new Date(), "Event-" + i));
        }
        eventSource.eventStopped();
    }

    class SlowSubscriber extends BaseSubscriber<MyEvent> {

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            request(1);     // 订阅时请求1个数据
        }

        @Override
        protected void hookOnNext(MyEvent event) {
            System.out.println("                      receive <<< " + event.getMessage());
            // 订阅者处理每个元素的时间，单位毫秒
            Uninterruptibles.sleepUninterruptibly(30, TimeUnit.MILLISECONDS);
            request(1);     // 每处理完1个数据，就再请求1个
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            System.err.println("                      receive <<< " + throwable);
            latch.countDown();
        }

        @Override
        protected void hookOnComplete() {
            latch.countDown();
        }
    }
}

package sakura.common.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import sakura.common.lang.Threads;
import sakura.common.reactor.event.MyEvent;
import sakura.common.reactor.event.MyEventListener;
import sakura.common.reactor.event.MyEventSource;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <ul>
 * <li>generate 同步且逐个的</li>
 * <li>create 异步（也可同步）的，每次尽可能多发出元素</li>
 * </ul>
 * <p>
 * Created by haomu on 2018/4/16.
 */
public class FluxSinkTest {

    @Test
    public void testGenerate1() {
        final AtomicInteger count = new AtomicInteger(1);
        Flux.generate(sink -> {
            sink.next(count.get() + " : " + new Date());
            Threads.sleepQuietly(100, TimeUnit.MILLISECONDS);
            if (count.getAndIncrement() >= 5) {
                sink.complete();
            }
        }).subscribe(System.out::println);
    }

    @Test
    public void testGenerate2() {
        Flux.generate(
                () -> 1,                // 初始化状态值
                (count, sink) -> {
                    sink.next(count + " : " + new Date());
                    Threads.sleepQuietly(100, TimeUnit.MILLISECONDS);
                    if (count >= 5) {
                        sink.complete();
                    }
                    return count + 1;   // 每次循环都要返回新的状态值给下次使用
                }).subscribe(System.out::println);
    }

    @Test
    public void testCreate() {
        MyEventSource eventSource = new MyEventSource();
        Flux.create(sink -> {
            MyEventListener listener = new MyEventListener() {
                @Override
                public void onNewEvent(MyEvent event) {
                    sink.next(event);
                }

                @Override
                public void onEventStopped() {
                    sink.complete();
                }
            };
            eventSource.register(listener);
        }).subscribe(System.out::println);

        for (int i = 0; i < 20; i++) {
            Random random = new Random();
            Threads.sleepQuietly(random.nextInt(100), TimeUnit.MILLISECONDS);
            eventSource.newEvent(new MyEvent(new Date(), "Event-" + i));
        }
        eventSource.eventStopped();
    }

}

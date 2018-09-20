package sakura.common.reactor;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sakura.common.lang.Threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by haomu on 2018/9/20.
 */
public class PipelineTest {

    @Test
    public void testFlux() {
        Flux<Integer> flux = Flux.range(0, 100)
                .publishOn(Schedulers.elastic(), 8)
                .map(i -> {
                    Threads.sleepQuietly(20, TimeUnit.MILLISECONDS);
                    return i;
                })
                .publishOn(Schedulers.elastic(), 8)
                .map(i -> {
                    Threads.sleepQuietly(20, TimeUnit.MILLISECONDS);
                    return i;
                });
//                .subscribeOn(Schedulers.elastic())
//                .blockLast();
        Stopwatch sw = Stopwatch.createStarted();
//        flux.subscribeOn(Schedulers.elastic()).blockLast();
        flux.blockLast();
        System.out.println("Flux: " + sw);
    }

    @Test
    public void testDisruptor() throws InterruptedException {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("disruptor-%d")
                .setDaemon(true)
                .build();

        CountDownLatch latch = new CountDownLatch(100);

        Disruptor<Event<Integer>> disruptor2 = new Disruptor<>(Event::new, 8,
                factory, ProducerType.MULTI, new SleepingWaitStrategy());
        disruptor2.handleEventsWith(new EventHandler<Event<Integer>>() {
            @Override
            public void onEvent(Event<Integer> event, long sequence, boolean endOfBatch) throws Exception {
                Threads.sleepQuietly(20, TimeUnit.MILLISECONDS);
                latch.countDown();
            }
        });


        Disruptor<Event<Integer>> disruptor1 = new Disruptor<>(Event::new, 8,
                factory, ProducerType.MULTI, new SleepingWaitStrategy());
        disruptor1.handleEventsWith(new EventHandler<Event<Integer>>() {
            @Override
            public void onEvent(Event<Integer> event, long sequence, boolean endOfBatch) throws Exception {
                Threads.sleepQuietly(20, TimeUnit.MILLISECONDS);
                publish(disruptor2, event.value);
            }
        });

        disruptor1.start();
        disruptor2.start();

        Stopwatch sw = Stopwatch.createStarted();

        for (int i = 0; i < 100; i++) {
            publish(disruptor1, i);
        }

        latch.await();
        System.out.println("Disruptor: " + sw);

        disruptor1.shutdown();
        disruptor2.shutdown();
    }

    private <T> void publish(Disruptor<Event<T>> disruptor, T value) {
        RingBuffer<Event<T>> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            Event<T> event = ringBuffer.get(sequence);
            event.setValue(value);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    @Data
    static class Event<T> {
        T value;
    }

}

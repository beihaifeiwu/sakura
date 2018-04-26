package sakura.common.reactor;

import lombok.experimental.UtilityClass;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import sakura.common.lang.annotation.Nullable;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by haomu on 2018/1/4.
 */
@UtilityClass
public class Subscribers {

    public static <T> BaseSubscriber<T> batch(int batchSize, Consumer<T> consumer) {
        return batch(batchSize, consumer, null);
    }

    public static <T> BaseSubscriber<T> batch(int batchSize, Consumer<T> consumer,
                                              @Nullable Consumer<? super Throwable> errorConsumer) {
        return batch(batchSize, consumer, errorConsumer, null);
    }

    public static <T> BaseSubscriber<T> batch(int batchSize, Consumer<T> consumer,
                                              @Nullable Consumer<? super Throwable> errorConsumer,
                                              @Nullable Runnable completeConsumer) {
        return new BatchSubscriber<>(batchSize, consumer, errorConsumer, completeConsumer);
    }

    public static <T> BaseSubscriber<T> oneByOne(Consumer<T> consumer) {
        return oneByOne(consumer, null, null);
    }

    public static <T> BaseSubscriber<T> oneByOne(Consumer<T> consumer,
                                                 @Nullable Consumer<? super Throwable> errorConsumer) {
        return oneByOne(consumer, errorConsumer, null);
    }

    public static <T> BaseSubscriber<T> oneByOne(Consumer<T> consumer,
                                                 @Nullable Consumer<? super Throwable> errorConsumer,
                                                 @Nullable Runnable completeConsumer) {
        return new OneByOneSubscriber<>(consumer, errorConsumer, completeConsumer);
    }

    public static class OneByOneSubscriber<T> extends BaseSubscriber<T> {

        final Consumer<T> consumer;
        final Consumer<? super Throwable> errorConsumer;
        final Runnable completeConsumer;

        public OneByOneSubscriber(Consumer<T> consumer,
                                  @Nullable Consumer<? super Throwable> errorConsumer,
                                  @Nullable Runnable completeConsumer) {
            this.consumer = consumer;
            this.errorConsumer = errorConsumer;
            this.completeConsumer = completeConsumer;
        }

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            subscription.request(1L);
        }

        @Override
        protected void hookOnNext(T value) {
            consumer.accept(value);
            request(1L);
        }

        @Override
        protected void hookOnComplete() {
            if (completeConsumer != null) {
                completeConsumer.run();
            }
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            if (errorConsumer != null) {
                errorConsumer.accept(throwable);
            }
        }

    }

    public static class BatchSubscriber<T> extends OneByOneSubscriber<T> {

        final AtomicLong count = new AtomicLong(0);
        final int size;

        public BatchSubscriber(int batchSize, Consumer<T> consumer,
                               @Nullable Consumer<? super Throwable> errorConsumer,
                               @Nullable Runnable completeConsumer) {
            super(consumer, errorConsumer, completeConsumer);
            this.size = Math.max(1, batchSize);
        }

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            subscription.request(size);
            count.set(size);
        }

        @Override
        protected void hookOnNext(T value) {
            consumer.accept(value);
            if (count.decrementAndGet() <= 0) {
                request(size);
                count.set(size);
            }
        }

    }

}

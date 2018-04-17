package sakura.common.reactor;

import lombok.experimental.UtilityClass;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import sakura.common.lang.annotation.Nullable;

import java.util.function.Consumer;

/**
 * Created by haomu on 2018/1/4.
 */
@UtilityClass
public class Subscribers {

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

        private final Consumer<T> consumer;
        private final Consumer<? super Throwable> errorConsumer;
        private final Runnable completeConsumer;

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
}

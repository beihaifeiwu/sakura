package com.github.beihaifeiwu.sakura.common.lang;

import lombok.experimental.UtilityClass;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.fi.lang.CheckedRunnable;
import org.jooq.lambda.fi.util.CheckedComparator;
import org.jooq.lambda.fi.util.function.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.function.*;

/**
 * Created by liupin on 2017/9/14.
 */
@UtilityClass
public class EX {

    private static final Consumer<Throwable> THROWABLE_TO_RUNTIME_EXCEPTION = t -> {
        if (t instanceof Error) {
            throw (Error) t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        if (t instanceof IOException) {
            throw new UncheckedIOException((IOException) t);
        }
        // Clients will not expect needing to handle this.
        if (t instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        throw new UncheckedException(t);
    };

    public static void rethrow(Throwable cause) {
        throw wrap(cause);
    }

    public static void rethrow(String format, Object... args) {
        throw wrap(format, args);
    }

    public static void rethrow(Throwable cause, String format, Object... args) {
        throw wrap(cause, format, args);
    }

    public static RuntimeException wrap(Throwable cause) {
        return wrap(cause, null, (Object) null);
    }

    public static RuntimeException wrap(String format, Object... args) {
        return wrap(null, format, args);
    }

    public static RuntimeException wrap(Throwable cause, String format, Object... args) {
        String message = format;
        if (format != null && args != null && args.length > 0) {
            message = String.format(format, args);
        }
        if (message == null && cause == null) {
            return new UncheckedException();
        }
        if (message == null) {
            return new UncheckedException(cause);
        }
        if (cause == null) {
            return new UncheckedException(message);
        }
        return new UncheckedException(message, cause);
    }


    public static Runnable unchecked(CheckedRunnable runnable) {
        return Unchecked.runnable(runnable, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> Comparator<T> unchecked(CheckedComparator<T> comparator) {
        return Unchecked.comparator(comparator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T, U> BiConsumer<T, U> unchecked(CheckedBiConsumer<T, U> consumer) {
        return Unchecked.biConsumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> ObjIntConsumer<T> unchecked(CheckedObjIntConsumer<T> consumer) {
        return Unchecked.objIntConsumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> ObjLongConsumer<T> unchecked(CheckedObjLongConsumer<T> consumer) {
        return Unchecked.objLongConsumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> ObjDoubleConsumer<T> unchecked(CheckedObjDoubleConsumer<T> consumer) {
        return Unchecked.objDoubleConsumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R> function) {
        return Unchecked.biFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T, U> ToIntBiFunction<T, U> unchecked(CheckedToIntBiFunction<T, U> function) {
        return Unchecked.toIntBiFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T, U> ToLongBiFunction<T, U> unchecked(CheckedToLongBiFunction<T, U> function) {
        return Unchecked.toLongBiFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T, U> ToDoubleBiFunction<T, U> unchecked(CheckedToDoubleBiFunction<T, U> function) {
        return Unchecked.toDoubleBiFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T, U> BiPredicate<T, U> unchecked(CheckedBiPredicate<T, U> predicate) {
        return Unchecked.biPredicate(predicate, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> BinaryOperator<T> unchecked(CheckedBinaryOperator<T> operator) {
        return Unchecked.binaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static IntBinaryOperator unchecked(CheckedIntBinaryOperator operator) {
        return Unchecked.intBinaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static LongBinaryOperator unchecked(CheckedLongBinaryOperator operator) {
        return Unchecked.longBinaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static DoubleBinaryOperator unchecked(CheckedDoubleBinaryOperator operator) {
        return Unchecked.doubleBinaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
        return Unchecked.consumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static IntConsumer unchecked(CheckedIntConsumer consumer) {
        return Unchecked.intConsumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static LongConsumer unchecked(CheckedLongConsumer consumer) {
        return Unchecked.longConsumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static DoubleConsumer unchecked(CheckedDoubleConsumer consumer) {
        return Unchecked.doubleConsumer(consumer, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R> function) {
        return Unchecked.function(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> ToIntFunction<T> unchecked(CheckedToIntFunction<T> function) {
        return Unchecked.toIntFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> ToLongFunction<T> unchecked(CheckedToLongFunction<T> function) {
        return Unchecked.toLongFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> ToDoubleFunction<T> unchecked(CheckedToDoubleFunction<T> function) {
        return Unchecked.toDoubleFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <R> IntFunction<R> unchecked(CheckedIntFunction<R> function) {
        return Unchecked.intFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static IntToLongFunction unchecked(CheckedIntToLongFunction function) {
        return Unchecked.intToLongFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static IntToDoubleFunction unchecked(CheckedIntToDoubleFunction function) {
        return Unchecked.intToDoubleFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <R> LongFunction<R> unchecked(CheckedLongFunction<R> function) {
        return Unchecked.longFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static LongToIntFunction unchecked(CheckedLongToIntFunction function) {
        return Unchecked.longToIntFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static LongToDoubleFunction unchecked(CheckedLongToDoubleFunction function) {
        return Unchecked.longToDoubleFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <R> DoubleFunction<R> unchecked(CheckedDoubleFunction<R> function) {
        return Unchecked.doubleFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static DoubleToIntFunction unchecked(CheckedDoubleToIntFunction function) {
        return Unchecked.doubleToIntFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static DoubleToLongFunction unchecked(CheckedDoubleToLongFunction function) {
        return Unchecked.doubleToLongFunction(function, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> Predicate<T> unchecked(CheckedPredicate<T> predicate) {
        return Unchecked.predicate(predicate, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static IntPredicate unchecked(CheckedIntPredicate predicate) {
        return Unchecked.intPredicate(predicate, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static LongPredicate unchecked(CheckedLongPredicate predicate) {
        return Unchecked.longPredicate(predicate, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static DoublePredicate unchecked(CheckedDoublePredicate predicate) {
        return Unchecked.doublePredicate(predicate, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> Supplier<T> unchecked(CheckedSupplier<T> supplier) {
        return Unchecked.supplier(supplier, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static IntSupplier unchecked(CheckedIntSupplier supplier) {
        return Unchecked.intSupplier(supplier, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static LongSupplier unchecked(CheckedLongSupplier supplier) {
        return Unchecked.longSupplier(supplier, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static DoubleSupplier unchecked(CheckedDoubleSupplier supplier) {
        return Unchecked.doubleSupplier(supplier, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static BooleanSupplier unchecked(CheckedBooleanSupplier supplier) {
        return Unchecked.booleanSupplier(supplier, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static <T> UnaryOperator<T> unchecked(CheckedUnaryOperator<T> operator) {
        return Unchecked.unaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static IntUnaryOperator unchecked(CheckedIntUnaryOperator operator) {
        return Unchecked.intUnaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static LongUnaryOperator unchecked(CheckedLongUnaryOperator operator) {
        return Unchecked.longUnaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static DoubleUnaryOperator unchecked(CheckedDoubleUnaryOperator operator) {
        return Unchecked.doubleUnaryOperator(operator, THROWABLE_TO_RUNTIME_EXCEPTION);
    }

    public static class UncheckedException extends RuntimeException {

        public UncheckedException() {
            super();
        }

        public UncheckedException(String message) {
            super(message);
        }

        public UncheckedException(String message, Throwable cause) {
            super(message, cause);
        }

        public UncheckedException(Throwable cause) {
            super(cause);
        }
    }

}

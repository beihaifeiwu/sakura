package sakura.common.lang;

import lombok.experimental.UtilityClass;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.fi.lang.CheckedRunnable;
import org.jooq.lambda.fi.util.CheckedComparator;
import org.jooq.lambda.fi.util.function.*;

import java.util.Comparator;
import java.util.function.*;

/**
 * Created by haomu on 2018/4/19.
 */
@UtilityClass
public class Functions {
    
    public static Runnable unchecked(CheckedRunnable runnable) {
        return Unchecked.runnable(runnable, EX::rethrow);
    }

    public static <T> Comparator<T> unchecked(CheckedComparator<T> comparator) {
        return Unchecked.comparator(comparator, EX::rethrow);
    }

    public static <T, U> BiConsumer<T, U> unchecked(CheckedBiConsumer<T, U> consumer) {
        return Unchecked.biConsumer(consumer, EX::rethrow);
    }

    public static <T> ObjIntConsumer<T> unchecked(CheckedObjIntConsumer<T> consumer) {
        return Unchecked.objIntConsumer(consumer, EX::rethrow);
    }

    public static <T> ObjLongConsumer<T> unchecked(CheckedObjLongConsumer<T> consumer) {
        return Unchecked.objLongConsumer(consumer, EX::rethrow);
    }

    public static <T> ObjDoubleConsumer<T> unchecked(CheckedObjDoubleConsumer<T> consumer) {
        return Unchecked.objDoubleConsumer(consumer, EX::rethrow);
    }

    public static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R> function) {
        return Unchecked.biFunction(function, EX::rethrow);
    }

    public static <T, U> ToIntBiFunction<T, U> unchecked(CheckedToIntBiFunction<T, U> function) {
        return Unchecked.toIntBiFunction(function, EX::rethrow);
    }

    public static <T, U> ToLongBiFunction<T, U> unchecked(CheckedToLongBiFunction<T, U> function) {
        return Unchecked.toLongBiFunction(function, EX::rethrow);
    }

    public static <T, U> ToDoubleBiFunction<T, U> unchecked(CheckedToDoubleBiFunction<T, U> function) {
        return Unchecked.toDoubleBiFunction(function, EX::rethrow);
    }

    public static <T, U> BiPredicate<T, U> unchecked(CheckedBiPredicate<T, U> predicate) {
        return Unchecked.biPredicate(predicate, EX::rethrow);
    }

    public static <T> BinaryOperator<T> unchecked(CheckedBinaryOperator<T> operator) {
        return Unchecked.binaryOperator(operator, EX::rethrow);
    }

    public static IntBinaryOperator unchecked(CheckedIntBinaryOperator operator) {
        return Unchecked.intBinaryOperator(operator, EX::rethrow);
    }

    public static LongBinaryOperator unchecked(CheckedLongBinaryOperator operator) {
        return Unchecked.longBinaryOperator(operator, EX::rethrow);
    }

    public static DoubleBinaryOperator unchecked(CheckedDoubleBinaryOperator operator) {
        return Unchecked.doubleBinaryOperator(operator, EX::rethrow);
    }

    public static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
        return Unchecked.consumer(consumer, EX::rethrow);
    }

    public static IntConsumer unchecked(CheckedIntConsumer consumer) {
        return Unchecked.intConsumer(consumer, EX::rethrow);
    }

    public static LongConsumer unchecked(CheckedLongConsumer consumer) {
        return Unchecked.longConsumer(consumer, EX::rethrow);
    }

    public static DoubleConsumer unchecked(CheckedDoubleConsumer consumer) {
        return Unchecked.doubleConsumer(consumer, EX::rethrow);
    }

    public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R> function) {
        return Unchecked.function(function, EX::rethrow);
    }

    public static <T> ToIntFunction<T> unchecked(CheckedToIntFunction<T> function) {
        return Unchecked.toIntFunction(function, EX::rethrow);
    }

    public static <T> ToLongFunction<T> unchecked(CheckedToLongFunction<T> function) {
        return Unchecked.toLongFunction(function, EX::rethrow);
    }

    public static <T> ToDoubleFunction<T> unchecked(CheckedToDoubleFunction<T> function) {
        return Unchecked.toDoubleFunction(function, EX::rethrow);
    }

    public static <R> IntFunction<R> unchecked(CheckedIntFunction<R> function) {
        return Unchecked.intFunction(function, EX::rethrow);
    }

    public static IntToLongFunction unchecked(CheckedIntToLongFunction function) {
        return Unchecked.intToLongFunction(function, EX::rethrow);
    }

    public static IntToDoubleFunction unchecked(CheckedIntToDoubleFunction function) {
        return Unchecked.intToDoubleFunction(function, EX::rethrow);
    }

    public static <R> LongFunction<R> unchecked(CheckedLongFunction<R> function) {
        return Unchecked.longFunction(function, EX::rethrow);
    }

    public static LongToIntFunction unchecked(CheckedLongToIntFunction function) {
        return Unchecked.longToIntFunction(function, EX::rethrow);
    }

    public static LongToDoubleFunction unchecked(CheckedLongToDoubleFunction function) {
        return Unchecked.longToDoubleFunction(function, EX::rethrow);
    }

    public static <R> DoubleFunction<R> unchecked(CheckedDoubleFunction<R> function) {
        return Unchecked.doubleFunction(function, EX::rethrow);
    }

    public static DoubleToIntFunction unchecked(CheckedDoubleToIntFunction function) {
        return Unchecked.doubleToIntFunction(function, EX::rethrow);
    }

    public static DoubleToLongFunction unchecked(CheckedDoubleToLongFunction function) {
        return Unchecked.doubleToLongFunction(function, EX::rethrow);
    }

    public static <T> Predicate<T> unchecked(CheckedPredicate<T> predicate) {
        return Unchecked.predicate(predicate, EX::rethrow);
    }

    public static IntPredicate unchecked(CheckedIntPredicate predicate) {
        return Unchecked.intPredicate(predicate, EX::rethrow);
    }

    public static LongPredicate unchecked(CheckedLongPredicate predicate) {
        return Unchecked.longPredicate(predicate, EX::rethrow);
    }

    public static DoublePredicate unchecked(CheckedDoublePredicate predicate) {
        return Unchecked.doublePredicate(predicate, EX::rethrow);
    }

    public static <T> Supplier<T> unchecked(CheckedSupplier<T> supplier) {
        return Unchecked.supplier(supplier, EX::rethrow);
    }

    public static IntSupplier unchecked(CheckedIntSupplier supplier) {
        return Unchecked.intSupplier(supplier, EX::rethrow);
    }

    public static LongSupplier unchecked(CheckedLongSupplier supplier) {
        return Unchecked.longSupplier(supplier, EX::rethrow);
    }

    public static DoubleSupplier unchecked(CheckedDoubleSupplier supplier) {
        return Unchecked.doubleSupplier(supplier, EX::rethrow);
    }

    public static BooleanSupplier unchecked(CheckedBooleanSupplier supplier) {
        return Unchecked.booleanSupplier(supplier, EX::rethrow);
    }

    public static <T> UnaryOperator<T> unchecked(CheckedUnaryOperator<T> operator) {
        return Unchecked.unaryOperator(operator, EX::rethrow);
    }

    public static IntUnaryOperator unchecked(CheckedIntUnaryOperator operator) {
        return Unchecked.intUnaryOperator(operator, EX::rethrow);
    }

    public static LongUnaryOperator unchecked(CheckedLongUnaryOperator operator) {
        return Unchecked.longUnaryOperator(operator, EX::rethrow);
    }

    public static DoubleUnaryOperator unchecked(CheckedDoubleUnaryOperator operator) {
        return Unchecked.doubleUnaryOperator(operator, EX::rethrow);
    }

}

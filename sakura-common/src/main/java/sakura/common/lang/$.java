package sakura.common.lang;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.fi.lang.CheckedRunnable;
import org.jooq.lambda.fi.util.CheckedComparator;
import org.jooq.lambda.fi.util.function.*;
import sakura.common.annotation.Nullable;

import java.util.*;
import java.util.function.*;

/**
 * Created by liupin on 2018/6/23.
 */
@UtilityClass
public class $ {

    //--------------------------------Empty---------------------------------
    public static boolean isBlank(@Nullable CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    public static boolean isEmpty(@Nullable CharSequence cs) {
        return StringUtils.isEmpty(cs);
    }

    public static boolean isEmpty(@Nullable long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(@Nullable Map m) {
        return m == null || m.isEmpty();
    }

    public static boolean isEmpty(@Nullable Object o) {
        return Objects.isEmpty(o);
    }


    //--------------------------------Object---------------------------------
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static boolean deepEquals(@Nullable Object a, @Nullable Object b) {
        return java.util.Objects.deepEquals(a, b);
    }

    public static int hashCode(@Nullable Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static int hashCode(@Nullable Object... values) {
        return Arrays.hashCode(values);
    }

    public static String toString(@Nullable Object o) {
        return String.valueOf(o);
    }

    public static String toString(@Nullable Object o, final String nullDefault) {
        return (o != null) ? o.toString() : nullDefault;
    }


    //----------------------------------Convert--------------------------------
    public static Object[] toArray(@Nullable Object o) {
        return Objects.toArray(o);
    }

    public static Iterable<Object> iterable(@Nullable Object o) {
        return Objects.toIterable(o);
    }


    //----------------------------------Compare--------------------------------
    public static int min(int a, int b, @Nullable int... rest) {
        int r = (a <= b) ? a : b;
        if (!isEmpty(rest)) {
            for (int i = 0; i < rest.length; i++) {
                r = (r <= rest[i]) ? r : rest[i];
            }
        }
        return r;
    }

    public static long min(long a, long b, @Nullable long... rest) {
        long r = (a <= b) ? a : b;
        if (!isEmpty(rest)) {
            for (int i = 0; i < rest.length; i++) {
                r = (r <= rest[i]) ? r : rest[i];
            }
        }
        return r;
    }

    @Nullable
    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(@Nullable T... values) {
        if (isEmpty(values)) return null;
        if (values.length == 1) return values[0];
        return ObjectUtils.max(values);
    }

    @Nullable
    @SafeVarargs
    public static <T extends Comparable<? super T>> T median(final T... values) {
        if (isEmpty(values)) return null;
        if (values.length == 1) return values[0];
        return ObjectUtils.median(values);
    }

    @Nullable
    @SafeVarargs
    public static <T> T median(final Comparator<T> comparator, final T... items) {
        return ObjectUtils.median(comparator, items);
    }


    //----------------------------------Collection/Map--------------------------
    public static <T> List<T> newList(final int estimatedSize) {
        return Lists.newArrayListWithExpectedSize(estimatedSize);
    }

    public static <T> Set<T> newSet(final int expectedSize) {
        return Sets.newHashSetWithExpectedSize(expectedSize);
    }

    public static <K, V> Map<K, V> newMap(final int expectedSize) {
        return Maps.newHashMapWithExpectedSize(expectedSize);
    }

    @SafeVarargs
    public static <T> List<T> list(@Nullable T... elements) {
        if (isEmpty(elements)) return Collections.emptyList();
        if (elements.length == 1) return Collections.singletonList(elements[0]);
        return Lists.newArrayList(elements);
    }

    public static <T> List<T> list(@Nullable Iterable<T> iterable) {
        if (iterable == null) return Collections.emptyList();
        return Lists.newArrayList(iterable);
    }

    @SafeVarargs
    public static <T> Set<T> set(@Nullable T... elements) {
        if (isEmpty(elements)) return Collections.emptySet();
        if (elements.length == 1) return Collections.singleton(elements[0]);
        return Sets.newHashSet(elements);
    }

    public static <T> Set<T> set(@Nullable Iterable<T> iterable) {
        if (iterable == null) return Collections.emptySet();
        return Sets.newHashSet(iterable);
    }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final int size) {
        return Iterables.partition(iterable, size);
    }

    public static <T> List<List<T>> partition(final List<T> list, final int size) {
        return Lists.partition(list, size);
    }


    //----------------------------------Function--------------------------------
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

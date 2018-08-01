package sakura.common;

import com.google.common.collect.*;
import lombok.experimental.UtilityClass;
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
        return sakura.common.lang.Objects.isEmpty(o);
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
        return sakura.common.lang.Objects.toArray(o);
    }

    public static Iterable<Object> iterable(@Nullable Object o) {
        return sakura.common.lang.Objects.toIterable(o);
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
        return isEmpty(elements) ? Collections.emptyList() : ImmutableList.copyOf(elements);
    }

    public static <T> List<T> list(@Nullable Iterable<T> iterable) {
        return iterable == null ? Collections.emptyList() : ImmutableList.copyOf(iterable);
    }

    @SafeVarargs
    public static <T> Set<T> set(@Nullable T... elements) {
        return isEmpty(elements) ? Collections.emptySet() : ImmutableSet.copyOf(elements);
    }

    public static <T> Set<T> set(@Nullable Iterable<T> iterable) {
        return iterable == null ? Collections.emptySet() : ImmutableSet.copyOf(iterable);
    }

    public static <K, V> Map<K, V> map(K k1, V v1) {
        return ImmutableMap.of(k1, v1);
    }

    public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
        return ImmutableMap.of(k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
        return ImmutableMap.of(k1, v1, k2, v2, k3, v3);
    }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final int size) {
        return Iterables.partition(iterable, size);
    }

    public static <T> List<List<T>> partition(final List<T> list, final int size) {
        return Lists.partition(list, size);
    }


    //----------------------------------Function--------------------------------
    public static Runnable unchecked(CheckedRunnable runnable) {
        return Unchecked.runnable(runnable);
    }

    public static <T> Comparator<T> unchecked(CheckedComparator<T> comparator) {
        return Unchecked.comparator(comparator);
    }

    public static <T, U> BiConsumer<T, U> unchecked(CheckedBiConsumer<T, U> consumer) {
        return Unchecked.biConsumer(consumer);
    }

    public static <T> ObjIntConsumer<T> unchecked(CheckedObjIntConsumer<T> consumer) {
        return Unchecked.objIntConsumer(consumer);
    }

    public static <T> ObjLongConsumer<T> unchecked(CheckedObjLongConsumer<T> consumer) {
        return Unchecked.objLongConsumer(consumer);
    }

    public static <T> ObjDoubleConsumer<T> unchecked(CheckedObjDoubleConsumer<T> consumer) {
        return Unchecked.objDoubleConsumer(consumer);
    }

    public static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R> function) {
        return Unchecked.biFunction(function);
    }

    public static <T, U> ToIntBiFunction<T, U> unchecked(CheckedToIntBiFunction<T, U> function) {
        return Unchecked.toIntBiFunction(function);
    }

    public static <T, U> ToLongBiFunction<T, U> unchecked(CheckedToLongBiFunction<T, U> function) {
        return Unchecked.toLongBiFunction(function);
    }

    public static <T, U> ToDoubleBiFunction<T, U> unchecked(CheckedToDoubleBiFunction<T, U> function) {
        return Unchecked.toDoubleBiFunction(function);
    }

    public static <T, U> BiPredicate<T, U> unchecked(CheckedBiPredicate<T, U> predicate) {
        return Unchecked.biPredicate(predicate);
    }

    public static <T> BinaryOperator<T> unchecked(CheckedBinaryOperator<T> operator) {
        return Unchecked.binaryOperator(operator);
    }

    public static IntBinaryOperator unchecked(CheckedIntBinaryOperator operator) {
        return Unchecked.intBinaryOperator(operator);
    }

    public static LongBinaryOperator unchecked(CheckedLongBinaryOperator operator) {
        return Unchecked.longBinaryOperator(operator);
    }

    public static DoubleBinaryOperator unchecked(CheckedDoubleBinaryOperator operator) {
        return Unchecked.doubleBinaryOperator(operator);
    }

    public static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
        return Unchecked.consumer(consumer);
    }

    public static IntConsumer unchecked(CheckedIntConsumer consumer) {
        return Unchecked.intConsumer(consumer);
    }

    public static LongConsumer unchecked(CheckedLongConsumer consumer) {
        return Unchecked.longConsumer(consumer);
    }

    public static DoubleConsumer unchecked(CheckedDoubleConsumer consumer) {
        return Unchecked.doubleConsumer(consumer);
    }

    public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R> function) {
        return Unchecked.function(function);
    }

    public static <T> ToIntFunction<T> unchecked(CheckedToIntFunction<T> function) {
        return Unchecked.toIntFunction(function);
    }

    public static <T> ToLongFunction<T> unchecked(CheckedToLongFunction<T> function) {
        return Unchecked.toLongFunction(function);
    }

    public static <T> ToDoubleFunction<T> unchecked(CheckedToDoubleFunction<T> function) {
        return Unchecked.toDoubleFunction(function);
    }

    public static <R> IntFunction<R> unchecked(CheckedIntFunction<R> function) {
        return Unchecked.intFunction(function);
    }

    public static IntToLongFunction unchecked(CheckedIntToLongFunction function) {
        return Unchecked.intToLongFunction(function);
    }

    public static IntToDoubleFunction unchecked(CheckedIntToDoubleFunction function) {
        return Unchecked.intToDoubleFunction(function);
    }

    public static <R> LongFunction<R> unchecked(CheckedLongFunction<R> function) {
        return Unchecked.longFunction(function);
    }

    public static LongToIntFunction unchecked(CheckedLongToIntFunction function) {
        return Unchecked.longToIntFunction(function);
    }

    public static LongToDoubleFunction unchecked(CheckedLongToDoubleFunction function) {
        return Unchecked.longToDoubleFunction(function);
    }

    public static <R> DoubleFunction<R> unchecked(CheckedDoubleFunction<R> function) {
        return Unchecked.doubleFunction(function);
    }

    public static DoubleToIntFunction unchecked(CheckedDoubleToIntFunction function) {
        return Unchecked.doubleToIntFunction(function);
    }

    public static DoubleToLongFunction unchecked(CheckedDoubleToLongFunction function) {
        return Unchecked.doubleToLongFunction(function);
    }

    public static <T> Predicate<T> unchecked(CheckedPredicate<T> predicate) {
        return Unchecked.predicate(predicate);
    }

    public static IntPredicate unchecked(CheckedIntPredicate predicate) {
        return Unchecked.intPredicate(predicate);
    }

    public static LongPredicate unchecked(CheckedLongPredicate predicate) {
        return Unchecked.longPredicate(predicate);
    }

    public static DoublePredicate unchecked(CheckedDoublePredicate predicate) {
        return Unchecked.doublePredicate(predicate);
    }

    public static <T> Supplier<T> unchecked(CheckedSupplier<T> supplier) {
        return Unchecked.supplier(supplier);
    }

    public static IntSupplier unchecked(CheckedIntSupplier supplier) {
        return Unchecked.intSupplier(supplier);
    }

    public static LongSupplier unchecked(CheckedLongSupplier supplier) {
        return Unchecked.longSupplier(supplier);
    }

    public static DoubleSupplier unchecked(CheckedDoubleSupplier supplier) {
        return Unchecked.doubleSupplier(supplier);
    }

    public static BooleanSupplier unchecked(CheckedBooleanSupplier supplier) {
        return Unchecked.booleanSupplier(supplier);
    }

    public static <T> UnaryOperator<T> unchecked(CheckedUnaryOperator<T> operator) {
        return Unchecked.unaryOperator(operator);
    }

    public static IntUnaryOperator unchecked(CheckedIntUnaryOperator operator) {
        return Unchecked.intUnaryOperator(operator);
    }

    public static LongUnaryOperator unchecked(CheckedLongUnaryOperator operator) {
        return Unchecked.longUnaryOperator(operator);
    }

    public static DoubleUnaryOperator unchecked(CheckedDoubleUnaryOperator operator) {
        return Unchecked.doubleUnaryOperator(operator);
    }

}

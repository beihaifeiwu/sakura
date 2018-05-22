package sakura.common.lang;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import sakura.common.lang.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by haomu on 2018/4/18.
 */
@UtilityClass
public class OBJ {

    public static boolean isEmpty(@Nullable final Object o) {
        if (o == null) return true;
        if (o instanceof CharSequence) return ((CharSequence) o).length() == 0;
        if (o.getClass().isArray()) return Array.getLength(o) == 0;
        if (o instanceof Collection) return ((Collection) o).isEmpty();
        if (o instanceof Map) return ((Map) o).isEmpty();
        if (o instanceof Iterator) return !((Iterator) o).hasNext();
        if (o instanceof Iterable) return !((Iterable) o).iterator().hasNext();
        return false;
    }

    public static boolean isNotEmpty(@Nullable final Object o) {
        return !isEmpty(o);
    }

    public static Object[] toArray(@Nullable final Object o) {
        if (o == null) return new Object[0];
        if (o instanceof Object[]) return (Object[]) o;
        if (o instanceof Collection) return ((Collection) o).toArray();
        if (o instanceof Map) return ((Map) o).entrySet().toArray();
        if (o instanceof Iterator) return Iterators.toArray((Iterator<?>) o, Object.class);
        if (o instanceof Iterable) return Iterables.toArray((Iterable<?>) o, Object.class);
        if (o.getClass().isArray()) {
            int length = Array.getLength(o);
            if (length == 0) return new Object[0];
            Class<?> wrapperType = Array.get(o, 0).getClass();
            Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
            for (int i = 0; i < length; i++) {
                newArray[i] = Array.get(o, i);
            }
            return newArray;
        }
        return new Object[]{o};
    }

    public static Iterable<?> toIterable(@Nullable final Object o) {
        if (o == null) return Collections.emptyList();
        if (o instanceof Iterable) return (Iterable<?>) o;
        if (o instanceof Map) return ((Map) o).entrySet();
        if (o.getClass().isArray()) {
            // the array may be primitive, so Arrays.asList() may throw
            // a ClassCastException.  Do the work manually
            // Curse primitives! :) (JGB)
            int size = Array.getLength(o);
            List<Object> answer = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Object e = Array.get(o, i);
                answer.add(e);
            }
            return answer;
        }
        return Collections.singleton(o);
    }

    @Nullable
    @SafeVarargs
    public static <T> T firstNonNull(@Nullable final T... values) {
        return ObjectUtils.firstNonNull(values);
    }

    @Nullable
    @SafeVarargs
    public static <T extends Comparable<? super T>> T min(@Nullable final T... values) {
        return ObjectUtils.min(values);
    }

    @Nullable
    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(@Nullable final T... values) {
        return ObjectUtils.max(values);
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> T median(final T... items) {
        return ObjectUtils.median(items);
    }

    @SafeVarargs
    public static <T> T median(final Comparator<T> comparator, final T... items) {
        return ObjectUtils.median(comparator, items);
    }

    /**
     * Find the most frequently occurring item.
     *
     * @param <T> type of values processed by this method
     * @param items to check
     * @return most populous T, {@code null} if non-unique or no items supplied
     */
    @Nullable
    @SafeVarargs
    public static <T> T mode(@Nullable final T... items) {
        return ObjectUtils.mode(items);
    }

    @Nullable
    public static String identityToString(@Nullable Object object) {
        return ObjectUtils.identityToString(object);
    }

    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static boolean deepEquals(@Nullable Object a, @Nullable Object b) {
        return Objects.deepEquals(a, b);
    }

    public static int hashCode(@Nullable Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static int hashCode(Object... values) {
        return Arrays.hashCode(values);
    }

    public static String toString(@Nullable Object o) {
        return String.valueOf(o);
    }

    public static String toString(@Nullable Object o, String nullDefault) {
        return (o != null) ? o.toString() : nullDefault;
    }

    public static <T> int compare(@Nullable T a, @Nullable T b, Comparator<? super T> c) {
        return (a == b) ? 0 : c.compare(a, b);
    }

}

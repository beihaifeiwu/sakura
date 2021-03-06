package sakura.common.lang;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import sakura.common.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by haomu on 2018/4/18.
 */
@UtilityClass
public class Objects {

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

    @SuppressWarnings("unchecked")
    public static Iterable<Object> toIterable(@Nullable final Object o) {
        if (o == null) return Collections.emptyList();
        if (o instanceof Iterable) return (Iterable<Object>) o;
        if (o instanceof Iterator) return Lists.newArrayList((Iterator) o);
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

}

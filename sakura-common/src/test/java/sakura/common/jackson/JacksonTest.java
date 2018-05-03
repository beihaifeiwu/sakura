package sakura.common.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static sakura.common.jackson.Jackson.*;

/**
 * Created by haomu on 2018/4/29.
 */
public class JacksonTest {

    @Test
    public void testConvert() {
        int value = convertValue("1.25", Integer.class);
        assertEquals(1, value);
        value = convertValue("1", int.class);
        assertEquals(1, value);
        value = convertValue(1.25, Integer.class);
        assertEquals(1, value);
    }

    @Test
    public void testCollectionType() {
        // explicit
        JavaType t = collectionType(LongList.class, Long.class);
        assertEquals(LongList.class, t.getRawClass());
        assertEquals(Long.class, t.getContentType().getRawClass());

        // implicit
        t = parametricType(List.class, Long.class);
        assertEquals(CollectionType.class, t.getClass());
        assertEquals(List.class, t.getRawClass());
        assertEquals(Long.class, t.getContentType().getRawClass());
    }

    @Test
    public void testMapType() {
        // explicit
        JavaType t = mapType(StringLongMap.class, String.class, Long.class);
        assertEquals(StringLongMap.class, t.getRawClass());
        assertEquals(String.class, t.getKeyType().getRawClass());
        assertEquals(Long.class, t.getContentType().getRawClass());

        // implicit
        t = parametricType(Map.class, Long.class, Boolean.class);
        assertEquals(MapType.class, t.getClass());
        assertEquals(Long.class, t.getKeyType().getRawClass());
        assertEquals(Boolean.class, t.getContentType().getRawClass());
    }

    static abstract class LongList implements List<Long> {
    }

    static abstract class StringLongMap implements Map<String, Long> {
    }

}

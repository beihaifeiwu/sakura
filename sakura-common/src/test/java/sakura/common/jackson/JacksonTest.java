package sakura.common.jackson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static sakura.common.jackson.Jackson.convertValue;

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

}

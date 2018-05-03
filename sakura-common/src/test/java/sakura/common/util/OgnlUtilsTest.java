package sakura.common.util;

import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static sakura.common.util.OgnlUtils.*;

/**
 * Created by haomu on 2018/4/20.
 */
@SuppressWarnings("unchecked")
public class OgnlUtilsTest {

    private Root root;

    @Before
    public void setUp() {
        root = new Root();
    }

    @Test
    public void testGetValue() {
        assertThat(getValue("array[0].name", root)).isEqualTo("Michael");
        assertThat(getValue("vector.size()", root)).isEqualTo(4);
        assertThat(getValue("array.{? #this.name == 'Alex' }[1].id", root)).isEqualTo("004");
        assertThat(getValue("vector.{^ #this.name == 'Alex' }[0].id", root)).isEqualTo("001");

        root.exp = "array[0].name";
        assertThat(getValue("(exp)(#this)", root)).isEqualTo("Michael");

        assertThat(getValue("#num", root, Collections.singletonMap("num", 5))).isEqualTo(5);
        assertThat(getValue("#num", root, Collections.singletonMap("num", "5"), Integer.class))
                .isInstanceOf(Integer.class)
                .isEqualTo(5);
    }

    @Test
    public void testGetBoolean() {
        assertThat(getBoolean("array[0].id == '002'", root)).isTrue();
        assertThat(getBoolean("'005' in vector.{id}", root)).isFalse();
    }

    @Test
    public void testGetIterable() {
        Iterable iterable;

        // ? 匹配所有记录
        iterable = getIterable("vector.{? #this.name == 'Alex'}", root);
        assertThat(iterable).contains(new Part("Alex", "001"), new Part("Alex", "004"));

        // ^ 匹配第一条记录
        iterable = getIterable("vector.{^ #this.name == 'Alex'}", root);
        assertThat(iterable).contains(new Part("Alex", "001"));

        // $ 匹配最后一条记录
        iterable = getIterable("vector.{$ #this.name == 'Alex'}", root);
        assertThat(iterable).contains(new Part("Alex", "004"));
    }

    @Data
    private static class Root {
        private Object[] array;
        private Vector<Part> vector;
        private String exp;

        Root() {
            vector = new Vector<>();
            vector.add(new Part("Michael", "002"));
            vector.add(new Part("Alex", "001"));
            vector.add(new Part("Joseph", "003"));
            vector.add(new Part("Alex", "004"));

            array = new Part[4];
            array[0] = new Part("Michael", "002");
            array[1] = new Part("Alex", "001");
            array[2] = new Part("Joseph", "003");
            array[3] = new Part("Alex", "004");
        }

    }

    @Data
    private static class Part {
        private String id;
        private String name;

        Part(String name, String id) {
            this.id = id;
            this.name = name;
        }
    }

}

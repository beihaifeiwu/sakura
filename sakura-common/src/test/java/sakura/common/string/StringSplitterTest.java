package sakura.common.string;

import com.google.common.base.CharMatcher;
import lombok.val;
import org.junit.Test;
import sakura.common.AbstractTest;
import sakura.common.S;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by liupin on 2018/6/23.
 */
public class StringSplitterTest extends AbstractTest {

    @Test
    public void testDefault() {
        val s = S.splitter();
        assertThat(s.split(null)).isNotNull().isEmpty();
        assertThat(s.split("")).isNotNull().isEmpty();
        assertThat(s.split("abc def")).containsExactly("abc", "def");
        assertThat(s.split("abc  def")).containsExactly("abc", "def");
        assertThat(s.split(" abc ")).containsExactly("abc");
    }

    @Test
    public void testPreserveAll() {
        val s = S.splitter().on('.').preserveAll();
        assertThat(s.split(null)).isNotNull().isEmpty();
        assertThat(s.split("")).containsExactly("");
        assertThat(s.split("a.b.c")).containsExactly("a", "b", "c");
        assertThat(s.split("a..b.c")).containsExactly("a", "", "b", "c");
        assertThat(s.split("a:b:c")).containsExactly("a:b:c");
    }

    @Test
    public void testTrim() {
        assertThat(S.splitter().on("*").trim().split("  ")).isNotNull().isEmpty();
        assertThat(S.splitter().on("*").trim().split("*ab *cd* ef  ")).containsExactly("ab", "cd", "ef");
        assertThat(S.splitter().trim(CharMatcher.is('*')).split("****")).isNotNull().isEmpty();
        assertThat(S.splitter().trim(CharMatcher.is('*')).split("*ab *cd* ef  ")).containsExactly("ab", "cd", "ef");
    }

    @Test
    public void testLimit() {
        assertThat(S.splitter().limit(0).split("ab cd ef")).containsExactly("ab", "cd", "ef");
        assertThat(S.splitter().limit(-1).split("ab   cd ef")).containsExactly("ab", "cd", "ef");
        assertThat(S.splitter().on(":").limit(0).split("ab:cd:ef")).containsExactly("ab", "cd", "ef");
        assertThat(S.splitter().on(":").limit(2).split("ab:cd:ef")).containsExactly("ab", "cd:ef");
    }

}

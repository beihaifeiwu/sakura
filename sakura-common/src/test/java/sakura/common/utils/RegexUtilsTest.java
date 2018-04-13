package sakura.common.utils;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liupin on 2017/9/14.
 */
public class RegexUtilsTest {

    private final String content = "ZZZaaabbbccc中文1234";

    @Test
    public void findAllTest() {
        // 查找所有匹配文本
        List<String> resultFindAll = RegexUtils.findAll("\\w{2}", content, 0, new ArrayList<>());
        ArrayList<String> expected = Lists.newArrayList("ZZ", "Za", "aa", "bb", "bc", "cc", "12", "34");
        Assert.assertEquals(expected, resultFindAll);
    }

    @Test
    public void isMatchTest() {
        // 给定字符串是否匹配给定正则
        boolean isMatch = RegexUtils.isMatch("\\w+[\u4E00-\u9FFF]+\\d+", content);
        Assert.assertTrue(isMatch);
    }

    @Test
    public void replaceAllTest() {
        //通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
        //此处把1234替换为 ->1234<-
        String replaceAll = RegexUtils.replaceAll(content, "(\\d+)", "->$1<-");
        Assert.assertEquals("ZZZaaabbbccc中文->1234<-", replaceAll);
    }

    @Test
    public void escapeTest() {
        //转义给定字符串，为正则相关的特殊符号转义
        String escape = RegexUtils.escape("我有个$符号{}");
        Assert.assertEquals("我有个\\$符号\\{\\}", escape);
    }

}

package sakura.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liupin on 2017/8/17.
 */
public class RegexUtils {

    /**
     * 英文字母 、数字和下划线
     */
    public static final Pattern GENERAL = Pattern.compile("^\\w+$");
    /**
     * 数字
     */
    public static final Pattern NUMBERS = Pattern.compile("\\d+");
    /**
     * 单个中文汉字
     */
    public static final Pattern CHINESE = Pattern.compile("[\u4E00-\u9FFF]");
    /**
     * 中文汉字
     */
    public static final Pattern CHINESES = Pattern.compile("[\u4E00-\u9FFF]+");
    /**
     * 分组
     */
    public static final Pattern GROUP_VAR = Pattern.compile("\\$(\\d+)");
    /**
     * IP v4
     */
    public static final Pattern IPV4 = Pattern.compile(
            "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
    /**
     * 货币
     */
    public static final Pattern MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
    /**
     * 邮件
     */
    public static final Pattern EMAIL = Pattern.compile("(\\w|.)+@\\w+(\\.\\w+){1,2}");
    /**
     * 移动电话
     */
    public static final Pattern MOBILE = Pattern.compile("1\\d{10}");
    /**
     * 身份证号码
     */
    public static final Pattern CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)");
    /**
     * 邮编
     */
    public static final Pattern ZIP_CODE = Pattern.compile("\\d{6}");
    /**
     * 生日
     */
    public static final Pattern BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-\\.年]?)(\\d{1,2})([/\\-\\.月]?)(\\d{1,2})日?$");
    /**
     * URL
     */
    public static final Pattern URL = Pattern.compile("(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");
    /**
     * 中文字、英文字母、数字和下划线
     */
    public static final Pattern GENERAL_WITH_CHINESE = Pattern.compile("^[\u4E00-\u9FFF\\w]+$");
    /**
     * UUID
     */
    public static final Pattern UUID = Pattern.compile("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$");
    /**
     * 不带横线的UUID
     */
    public static final Pattern UUID_SIMPLE = Pattern.compile("^[0-9a-z]{32}$");
    /**
     * 中国车牌号码
     */
    public static final Pattern PLATE_NUMBER = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$");
    /**
     * MAC地址正则
     */
    public static final Pattern MAC_ADDRESS = Pattern.compile("((?:[A-F0-9]{1,2}[:-]){5}[A-F0-9]{1,2})|(?:0x)(\\d{12})(?:.+ETHER)", Pattern.CASE_INSENSITIVE);

    /**
     * 正则中需要被转义的关键字
     */
    private static final Set<Character> RE_KEYS = Sets.newHashSet('$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|');

    /**
     * Pattern池
     */
    private static final SimpleCache<RegexWithFlag, Pattern> POOL = new SimpleCache<>();

    /**
     * 先从Pattern池中查找正则对应的{@link Pattern}，找不到则编译正则表达式并入池。
     *
     * @param regex 正则表达式
     * @return {@link Pattern}
     */
    public static Pattern getPattern(String regex) {
        return getPattern(regex, 0);
    }

    /**
     * 先从Pattern池中查找正则对应的{@link Pattern}，找不到则编译正则表达式并入池。
     *
     * @param regex 正则表达式
     * @param flags 正则标识位集合 {@link Pattern}
     * @return {@link Pattern}
     */
    public static Pattern getPattern(String regex, int flags) {
        final RegexWithFlag regexWithFlag = new RegexWithFlag(regex, flags);

        Pattern pattern = POOL.get(regexWithFlag);
        if (null == pattern) {
            pattern = Pattern.compile(regex, flags);
            POOL.put(regexWithFlag, pattern);
        }
        return pattern;
    }

    /**
     * 移除缓存
     *
     * @param regex 正则
     * @param flags 标识
     * @return 移除的{@link Pattern}，可能为{@code null}
     */
    public static Pattern removePattern(String regex, int flags) {
        return POOL.remove(new RegexWithFlag(regex, flags));
    }

    /**
     * 清空缓存池
     */
    public static void clearPatterns() {
        POOL.clear();
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param regex   正则
     * @param content 被查找的内容
     * @param group   正则的分组
     * @return 结果列表
     */
    public static List<String> findAll(String regex, String content, int group) {
        return findAll(regex, content, group, new ArrayList<>());
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param regex      正则
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(String regex, String content, int group, T collection) {
        if (null == regex) {
            return null;
        }

        Pattern pattern = getPattern(regex, Pattern.DOTALL);
        return findAll(pattern, content, group, collection);
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @param group   正则的分组
     * @return 结果列表
     */
    public static List<String> findAll(Pattern pattern, String content, int group) {
        return findAll(pattern, content, group, new ArrayList<String>());
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param pattern    编译后的正则模式
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(Pattern pattern, String content, int group, T collection) {
        if (null == pattern || null == content) {
            return null;
        }

        if (null == collection) {
            throw new NullPointerException("Null collection param provided!");
        }

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            collection.add(matcher.group(group));
        }
        return collection;
    }

    /**
     * 计算指定字符串中，匹配pattern的个数
     *
     * @param regex   正则表达式
     * @param content 被查找的内容
     * @return 匹配个数
     */
    public static int count(String regex, String content) {
        if (null == regex) {
            return 0;
        }

        final Pattern pattern = getPattern(regex, Pattern.DOTALL);
        return count(pattern, content);
    }

    /**
     * 计算指定字符串中，匹配pattern的个数
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 匹配个数
     */
    public static int count(Pattern pattern, String content) {
        if (null == pattern || null == content) {
            return 0;
        }

        int count = 0;
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            count++;
        }

        return count;
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param regex   正则
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(String regex, String content) {
        if (content == null) {
            //提供null的字符串为不匹配
            return false;
        }

        if (Strings.isNullOrEmpty(regex)) {
            //正则不存在则为全匹配
            return true;
        }

        Pattern pattern = getPattern(regex, Pattern.DOTALL);
        return isMatch(pattern, content);
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, String content) {
        if (content == null || pattern == null) {
            //提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * @param content             文本
     * @param regex               正则
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     */
    public static String replaceAll(String content, String regex, String replacementTemplate) {
        final Pattern pattern = getPattern(regex, Pattern.DOTALL);
        return replaceAll(content, pattern, replacementTemplate);
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * @param content             文本
     * @param pattern             {@link Pattern}
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     */
    public static String replaceAll(String content, Pattern pattern, String replacementTemplate) {
        if (Strings.isNullOrEmpty(content)) {
            return content;
        }

        final Matcher matcher = pattern.matcher(content);
        boolean result = matcher.find();
        if (result) {
            final Set<String> varNums = findAll(GROUP_VAR, replacementTemplate, 1, new HashSet<>());
            final StringBuffer sb = new StringBuffer();
            do {
                String replacement = replacementTemplate;
                for (String var : varNums) {
                    int group = Integer.parseInt(var);
                    replacement = replacement.replace("$" + var, matcher.group(group));
                }
                matcher.appendReplacement(sb, escape(replacement));
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return content;
    }

    /**
     * 转义字符，将正则的关键字转义
     *
     * @param c 字符
     * @return 转义后的文本
     */
    public static String escape(char c) {
        final StringBuilder builder = new StringBuilder();
        if (RE_KEYS.contains(c)) {
            builder.append('\\');
        }
        builder.append(c);
        return builder.toString();
    }

    /**
     * 转义字符串，将正则的关键字转义
     *
     * @param content 文本
     * @return 转义后的文本
     */
    public static String escape(String content) {
        if (Strings.isNullOrEmpty(content)) {
            return content;
        }

        final StringBuilder builder = new StringBuilder();
        int len = content.length();
        char current;
        for (int i = 0; i < len; i++) {
            current = content.charAt(i);
            if (RE_KEYS.contains(current)) {
                builder.append('\\');
            }
            builder.append(current);
        }
        return builder.toString();
    }


    /**
     * 正则表达式和正则标识位的包装
     */
    private static class RegexWithFlag {

        private String regex;
        private int flag;

        RegexWithFlag(String regex, int flag) {
            this.regex = regex;
            this.flag = flag;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + flag;
            result = prime * result + ((regex == null) ? 0 : regex.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            RegexWithFlag other = (RegexWithFlag) obj;
            if (flag != other.flag) {
                return false;
            }
            if (regex == null) {
                if (other.regex != null) {
                    return false;
                }
            } else if (!regex.equals(other.regex)) {
                return false;
            }
            return true;
        }

    }
}

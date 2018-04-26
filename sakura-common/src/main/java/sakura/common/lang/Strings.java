package sakura.common.lang;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import sakura.common.lang.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by haomu on 2018/4/17.
 */
@UtilityClass
public class Strings {

    public static boolean isBlank(@Nullable CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    public static boolean isNotBlank(@Nullable CharSequence cs) {
        return StringUtils.isNotBlank(cs);
    }

    public static String[] tokenize(@Nullable String str, String delimiters) {
        return tokenize(str, delimiters, true, true);
    }

    public static String[] tokenize(@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) return new String[0];
        List<String> tokens = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(str, delimiters);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }

        return tokens.toArray(new String[0]);
    }

}

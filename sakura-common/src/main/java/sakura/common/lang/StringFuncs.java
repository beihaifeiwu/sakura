package sakura.common.lang;

import org.apache.commons.lang3.StringUtils;
import sakura.common.lang.annotation.Nullable;
import sakura.common.lang.string.StringSplitter;

/**
 * Created by haomu on 2018/4/17.
 */
public class StringFuncs {

    public boolean isBlank(@Nullable CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    public boolean isNotBlank(@Nullable CharSequence cs) {
        return StringUtils.isNotBlank(cs);
    }

    @Nullable
    public String trimToNull(@Nullable String str) {
        return StringUtils.trimToNull(str);
    }

    public String trimToEmpty(@Nullable String str) {
        return StringUtils.trimToEmpty(str);
    }

    public StringSplitter splitter(String delimiter) {
        return new StringSplitter(delimiter);
    }

}

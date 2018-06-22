package sakura.common;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import sakura.common.annotation.Nullable;
import sakura.common.string.StringSplitter;
import sakura.common.string.StringStripper;

/**
 * Created by liupin on 2018/6/23.
 */
@UtilityClass
public class S {

    public boolean isEmpty(@Nullable CharSequence cs) {
        return StringUtils.isEmpty(cs);
    }

    public boolean isBlank(@Nullable CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    public StringStripper stripper(String stripChars) {
        return new StringStripper(stripChars);
    }

    public StringSplitter splitter(String delimiter) {
        return new StringSplitter(delimiter);
    }

}

package sakura.common.string;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import sakura.common.S;
import sakura.common.annotation.Nullable;

/**
 * Created by liupin on 2018/6/23.
 */
@Setter
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
public class StringStripper {

    private final String chars;

    private boolean start;
    private boolean end;

    public StringStripper all() {
        this.start = true;
        this.end = true;
        return this;
    }

    public StringStripper start() {
        this.start = true;
        return this;
    }

    public StringStripper end() {
        this.end = true;
        return this;
    }

    @Nullable
    public String strip(@Nullable String str) {
        if (S.isEmpty(str)) return str;
        if (start) str = StringUtils.stripStart(str, chars);
        if (end) str = StringUtils.stripEnd(str, chars);
        return str;
    }

    @Nullable
    public String stripToNull(@Nullable String str) {
        str = strip(str);
        if (str == null) return null;
        return str.isEmpty() ? null : str;
    }

    public String stripToEmpty(@Nullable String str) {
        str = strip(str);
        return str == null ? "" : str;
    }

}

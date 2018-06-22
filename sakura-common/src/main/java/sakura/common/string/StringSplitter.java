package sakura.common.string;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import sakura.common.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by haomu on 2018/6/22.
 */
@Setter
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
public class StringSplitter {

    private final String delimiter;

    private boolean trim;
    private boolean omitEmpty;
    private int limit = -1;

    public StringSplitter trim() {
        this.trim = true;
        return this;
    }

    public StringSplitter omitEmpty() {
        this.omitEmpty = true;
        return this;
    }

    public List<String> splitToList(@Nullable String str) {
        Validate.isTrue(limit > 0, "must be greater than zero: %s", limit);

        if (str == null) return Collections.emptyList();
        List<String> tokens = new ArrayList<>();
        int count = 0;
        StringTokenizer st = new StringTokenizer(str, delimiter);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trim) token = token.trim();
            if (omitEmpty && token.length() <= 0) continue;
            if (limit > -1 && count >= limit) break;
            tokens.add(token);
            count += 1;
        }
        return tokens;
    }

    public String[] split(@Nullable String str) {
        List<String> tokens = splitToList(str);
        return tokens.toArray(new String[0]);
    }

}

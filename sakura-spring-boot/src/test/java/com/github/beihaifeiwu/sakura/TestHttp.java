package com.github.beihaifeiwu.sakura;

import com.github.beihaifeiwu.sakura.core.SpringContext;
import com.github.beihaifeiwu.sakura.common.http.HTTP;
import com.github.beihaifeiwu.sakura.common.http.HTTP.HttpRequest;
import com.google.common.base.Strings;

/**
 * Created by liupin on 2017/6/12.
 */
public class TestHttp {

    public static String ensureUrl(CharSequence url) {
        String validUrl = url != null ? url.toString() : "";

        if (Strings.isNullOrEmpty(validUrl)
                || (!validUrl.startsWith("http") && !validUrl.startsWith("https"))) {
            validUrl = SpringContext.baseUrl() + validUrl;
        }

        return validUrl;
    }

    public static HTTP.HttpRequest get(final CharSequence url) {
        return HTTP.get(ensureUrl(url));
    }

    public static HttpRequest post(final CharSequence url) {
        return HTTP.post(ensureUrl(url));
    }

    public static HttpRequest put(final CharSequence url) {
        return HTTP.put(ensureUrl(url));
    }

    public static HttpRequest delete(final CharSequence url) {
        return HTTP.delete(ensureUrl(url));
    }

    public static HttpRequest options(final CharSequence url) {
        return HTTP.options(ensureUrl(url));
    }

    public static HttpRequest trace(final CharSequence url) {
        return HTTP.trace(ensureUrl(url));
    }

    public static HttpRequest head(final CharSequence url) {
        return HTTP.head(ensureUrl(url));
    }
}

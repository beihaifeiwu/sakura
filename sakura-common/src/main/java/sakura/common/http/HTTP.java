package sakura.common.http;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import sakura.common.lang.SSL;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static sakura.common.lang.Urls.append;
import static sakura.common.lang.Urls.encode;

/**
 * Created by liupin on 2017/5/9.
 */
@Slf4j
@UtilityClass
public final class HTTP {

    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String PARAM_CHARSET = "charset";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String ENCODING_GZIP = "gzip";

    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_DATE = "Date";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_EXPIRES = "Expires";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String HEADER_REFERER = "Referer";
    public static final String HEADER_SERVER = "Server";
    public static final String HEADER_USER_AGENT = "User-Agent";


    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";

    public static final String[] EMPTY_STRINGS = new String[0];
    public static final ZoneId GMT = ZoneId.of("GMT");

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.RFC_1123_DATE_TIME,
            DateTimeFormatter.ofPattern("EEEE, dd-MMM-yy HH:mm:ss zz", Locale.US),
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.US).withZone(GMT)
    };

    private static volatile OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (HTTP.class) {
                if (okHttpClient == null) {
                    val logging = new HttpLoggingInterceptor(log::debug);
                    logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                    val params = SSL.getSSLParams(null, null, null);
                    okHttpClient = new OkHttpClient.Builder()
                            .followRedirects(true)
                            .followSslRedirects(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .pingInterval(0, TimeUnit.SECONDS)
                            .addInterceptor(logging)
                            .sslSocketFactory(params.socketFactory, params.trustManager)
                            .hostnameVerifier(SSL.UN_SAFE_HOSTNAME_VERIFIER)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static void setOkHttpClient(final OkHttpClient okHttpClient) {
        HTTP.okHttpClient = okHttpClient;
    }

    public static HttpRequest get(CharSequence url) {
        return new HttpRequest(url, METHOD_GET);
    }

    public static HttpRequest get(URL url) {
        return new HttpRequest(url, METHOD_GET);
    }

    public static HttpRequest get(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = append(baseUrl, params);
        return get(encode ? encode(url) : url);
    }

    public static HttpRequest get(CharSequence baseUrl, boolean encode, Object... params) {
        String url = append(baseUrl, params);
        return get(encode ? encode(url) : url);
    }

    public static HttpRequest post(CharSequence url) {
        return new HttpRequest(url, METHOD_POST);
    }

    public static HttpRequest post(URL url) {
        return new HttpRequest(url, METHOD_POST);
    }

    public static HttpRequest post(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = append(baseUrl, params);
        return post(encode ? encode(url) : url);
    }

    public static HttpRequest post(CharSequence baseUrl, boolean encode, Object... params) {
        String url = append(baseUrl, params);
        return post(encode ? encode(url) : url);
    }

    public static HttpRequest put(CharSequence url) {
        return new HttpRequest(url, METHOD_PUT);
    }

    public static HttpRequest put(URL url) {
        return new HttpRequest(url, METHOD_PUT);
    }

    public static HttpRequest put(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = append(baseUrl, params);
        return put(encode ? encode(url) : url);
    }

    public static HttpRequest put(CharSequence baseUrl, boolean encode, Object... params) {
        String url = append(baseUrl, params);
        return put(encode ? encode(url) : url);
    }

    public static HttpRequest delete(CharSequence url) {
        return new HttpRequest(url, METHOD_DELETE);
    }

    public static HttpRequest delete(URL url) {
        return new HttpRequest(url, METHOD_DELETE);
    }

    public static HttpRequest delete(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = append(baseUrl, params);
        return delete(encode ? encode(url) : url);
    }

    public static HttpRequest delete(CharSequence baseUrl, boolean encode, Object... params) {
        String url = append(baseUrl, params);
        return delete(encode ? encode(url) : url);
    }

    public static HttpRequest head(CharSequence url) {
        return new HttpRequest(url, METHOD_HEAD);
    }

    public static HttpRequest head(URL url) {
        return new HttpRequest(url, METHOD_HEAD);
    }

    public static HttpRequest head(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = append(baseUrl, params);
        return head(encode ? encode(url) : url);
    }

    public static HttpRequest head(CharSequence baseUrl, boolean encode, Object... params) {
        String url = append(baseUrl, params);
        return head(encode ? encode(url) : url);
    }

    public static HttpRequest options(CharSequence url) {
        return new HttpRequest(url, METHOD_OPTIONS);
    }

    public static HttpRequest options(URL url) {
        return new HttpRequest(url, METHOD_OPTIONS);
    }

    public static HttpRequest trace(CharSequence url) {
        return new HttpRequest(url, METHOD_TRACE);
    }

    public static HttpRequest trace(URL url) {
        return new HttpRequest(url, METHOD_TRACE);
    }

    public static long parseDate(final String dateString) {
        if (dateString == null || dateString.length() < 3) {
            return -1;
        }
        String value = dateString;
        // See https://stackoverflow.com/questions/12626699/if-modified-since-http-header-passed-by-ie9-includes-length
        int parametersIndex = value.indexOf(";");
        if (parametersIndex != -1) {
            value = value.substring(0, parametersIndex);
        }

        for (DateTimeFormatter dateFormatter : DATE_FORMATTERS) {
            try {
                return ZonedDateTime.parse(value, dateFormatter).toInstant().toEpochMilli();
            } catch (Exception ignore) {
            }
        }
        return -1;
    }

    public static String formatDate(final long date) {
        Instant instant = Instant.ofEpochMilli(date);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, GMT);
        return DATE_FORMATTERS[0].format(zonedDateTime);
    }

}

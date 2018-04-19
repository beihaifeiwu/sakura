package sakura.common.http;

import sakura.common.lang.EX;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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

    private static X509TrustManager trustManager;
    private static SSLSocketFactory trustedFactory;
    private static HostnameVerifier trustedVerifier;

    private static volatile OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (HTTP.class) {
                if (okHttpClient == null) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::debug);
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                    okHttpClient = new OkHttpClient.Builder()
                            .followRedirects(true)
                            .followSslRedirects(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .pingInterval(0, TimeUnit.SECONDS)
                            .addInterceptor(loggingInterceptor)
                            .sslSocketFactory(getTrustedFactory(), getTrustManager())
                            .hostnameVerifier(getTrustedVerifier())
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

    /**
     * Encode the given URL as an ASCII {@link String}
     * <p>
     * This method ensures the path and query segments of the URL are properly
     * encoded such as ' ' characters being encoded to '%20' or any UTF-8
     * characters that are non-ASCII. No encoding of URLs is done by default by
     * the {@link HttpRequest} constructors and so if URL encoding is needed this
     * method should be called before calling the {@link HttpRequest} constructor.
     *
     * @param url
     * @return encoded URL
     */
    public static String encode(final CharSequence url) {
        URL parsed;
        try {
            parsed = new URL(url.toString());
        } catch (IOException e) {
            throw EX.unchecked(e);
        }

        String host = parsed.getHost();
        int port = parsed.getPort();
        if (port != -1) {
            host = host + ':' + Integer.toString(port);
        }
        try {
            String encoded = new URI(parsed.getProtocol(), host, parsed.getPath(),
                    parsed.getQuery(), null).toASCIIString();
            int paramsStart = encoded.indexOf('?');
            if (paramsStart > 0 && paramsStart + 1 < encoded.length()) {
                encoded = encoded.substring(0, paramsStart + 1)
                        + encoded.substring(paramsStart + 1).replace("+", "%2B");
            }
            return encoded;
        } catch (URISyntaxException e) {
            throw EX.unchecked(e, "Parsing URI failed");
        }
    }

    /**
     * Append given map as query parameters to the base URL
     * <p>
     * Each map entry's key will be a parameter name and the value's
     * {@link Object#toString()} will be the parameter value.
     *
     * @param url
     * @param params
     * @return URL with appended query params
     */
    public static String append(final CharSequence url, final Map<?, ?> params) {
        final String baseUrl = url.toString();
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        final StringBuilder result = new StringBuilder(baseUrl);

        addPathSeparator(baseUrl, result);
        addParamPrefix(baseUrl, result);

        Entry<?, ?> entry;
        Iterator<?> iterator = params.entrySet().iterator();
        entry = (Entry<?, ?>) iterator.next();
        addParam(entry.getKey().toString(), entry.getValue(), result);

        while (iterator.hasNext()) {
            result.append('&');
            entry = (Entry<?, ?>) iterator.next();
            addParam(entry.getKey().toString(), entry.getValue(), result);
        }

        return result.toString();
    }

    /**
     * Append given name/value pairs as query parameters to the base URL
     * <p>
     * The params argument is interpreted as a sequence of name/value pairs so the
     * given number of params must be divisible by 2.
     *
     * @param url
     * @param params name/value pairs
     * @return URL with appended query params
     */
    public static String append(final CharSequence url, final Object... params) {
        final String baseUrl = url.toString();
        if (params == null || params.length == 0) {
            return baseUrl;
        }

        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Must specify an even number of parameter names/values");
        }

        final StringBuilder result = new StringBuilder(baseUrl);

        addPathSeparator(baseUrl, result);
        addParamPrefix(baseUrl, result);

        addParam(params[0], params[1], result);

        for (int i = 2; i < params.length; i += 2) {
            result.append('&');
            addParam(params[i], params[i + 1], result);
        }

        return result.toString();
    }

    public static long parseDate(final String dateString) {
        if (dateString != null && dateString.length() >= 3) {
            String value = dateString;
            // See https://stackoverflow.com/questions/12626699/if-modified-since-http-header-passed-by-ie9-includes-length
            int parametersIndex = value.indexOf(";");
            if (parametersIndex != -1) {
                value = value.substring(0, parametersIndex);
            }

            for (DateTimeFormatter dateFormatter : DATE_FORMATTERS) {
                try {
                    return ZonedDateTime.parse(value, dateFormatter).toInstant().toEpochMilli();
                } catch (DateTimeParseException ex) {
                    // ignore
                }
            }
        }
        return -1;
    }

    public static String formatDate(final long date) {
        Instant instant = Instant.ofEpochMilli(date);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, GMT);
        return DATE_FORMATTERS[0].format(zonedDateTime);
    }

    private static X509TrustManager getTrustManager() {
        if (trustManager == null) {
            trustManager = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    // Intentionally left blank
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    // Intentionally left blank
                }
            };
        }
        return trustManager;
    }

    private static SSLSocketFactory getTrustedFactory() {
        if (trustedFactory == null) {
            final TrustManager[] trustAllCerts = new TrustManager[]{getTrustManager()};
            try {
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new SecureRandom());
                trustedFactory = context.getSocketFactory();
            } catch (GeneralSecurityException e) {
                throw EX.unchecked(e, "Security exception configuring SSL context");
            }
        }
        return trustedFactory;
    }

    private static HostnameVerifier getTrustedVerifier() {
        if (trustedVerifier == null) {
            trustedVerifier = (hostname, session) -> true;
        }
        return trustedVerifier;
    }

    private static void addPathSeparator(String baseUrl, StringBuilder result) {
        // Add trailing slash if the base URL doesn't have any path segments.
        //
        // The following test is checking for the last slash not being part of
        // the protocol to host separator: '://'.
        if (baseUrl.indexOf(':') + 2 == baseUrl.lastIndexOf('/')) {
            result.append('/');
        }
    }

    private static void addParamPrefix(String baseUrl, StringBuilder result) {
        // Add '?' if missing and add '&' if params already exist in base url
        final int queryStart = baseUrl.indexOf('?');
        final int lastChar = result.length() - 1;
        if (queryStart == -1) {
            result.append('?');
        } else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&') {
            result.append('&');
        }
    }

    private static void addParam(Object key, Object value, StringBuilder result) {
        if (value != null && value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(Array.get(value, i));
            }
            value = list;
        }
        if (value instanceof Iterable<?>) {
            Iterator<?> iterator = ((Iterable<?>) value).iterator();
            while (iterator.hasNext()) {
                result.append(key);
                result.append("[]=");
                Object element = iterator.next();
                if (element != null) {
                    result.append(element);
                }
                if (iterator.hasNext()) {
                    result.append("&");
                }
            }
        } else {
            result.append(key);
            result.append("=");
            if (value != null) {
                result.append(value);
            }
        }
    }

}

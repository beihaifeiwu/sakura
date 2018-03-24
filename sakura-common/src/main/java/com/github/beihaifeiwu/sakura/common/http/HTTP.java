package com.github.beihaifeiwu.sakura.common.http;

import com.github.beihaifeiwu.sakura.common.jackson.JSON;
import com.github.beihaifeiwu.sakura.common.lang.EX;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
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
import java.util.function.Consumer;

import static java.net.HttpURLConnection.*;

/**
 * Created by liupin on 2017/5/9.
 */
@Slf4j
public final class HTTP {

    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String PARAM_CHARSET = "charset";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MSGPACK = "application/x-msgpack";

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

    private static final ZoneId GMT = ZoneId.of("GMT");

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.RFC_1123_DATE_TIME,
            DateTimeFormatter.ofPattern("EEEE, dd-MMM-yy HH:mm:ss zz", Locale.US),
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.US).withZone(GMT)
    };

    private static final String[] EMPTY_STRINGS = new String[0];

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
            throw EX.wrap(e);
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
            throw EX.wrap(e, "Parsing URI failed");
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
        if (params == null || params.isEmpty()) return baseUrl;

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
        if (params == null || params.length == 0) return baseUrl;

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
            final TrustManager[] trustAllCerts = new TrustManager[]{ getTrustManager() };
            try {
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new SecureRandom());
                trustedFactory = context.getSocketFactory();
            } catch (GeneralSecurityException e) {
                throw EX.wrap(e, "Security exception configuring SSL context");
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

    public static class HttpRequest {

        private final URL url;
        private final String method;

        private MediaType contentType;
        private Headers.Builder headers = new Headers.Builder();
        private RequestBody requestBody;
        private MultipartBody.Builder multipartBody;
        private FormBody.Builder formBody;

        public HttpRequest(@NonNull CharSequence url, @NonNull String method) {
            try {
                this.url = new URL(url.toString());
            } catch (MalformedURLException e) {
                throw EX.wrap(e);
            }
            this.method = method;
        }

        public HttpRequest(@NonNull URL url, @NonNull String method) {
            this.url = url;
            this.method = method;
        }

        @Override
        public String toString() {
            return method + ' ' + url;
        }

        public HttpRequest header(final String name, final String value) {
            this.headers.set(name, value);
            return this;
        }

        public HttpRequest header(final String name, final Number value) {
            return header(name, value != null ? value.toString() : null);
        }

        public HttpRequest headers(final Map<String, String> headers) {
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(this::header);
            }
            return this;
        }

        public HttpRequest userAgent(final String userAgent) {
            return header(HEADER_USER_AGENT, userAgent);
        }

        public HttpRequest referer(final String referer) {
            return header(HEADER_REFERER, referer);
        }

        public HttpRequest accept(final String accept) {
            return header(HEADER_ACCEPT, accept);
        }

        public HttpRequest acceptCharset(final String acceptCharset) {
            return header(HEADER_ACCEPT_CHARSET, acceptCharset);
        }

        public HttpRequest acceptEncoding(final String acceptEncoding) {
            return header(HEADER_ACCEPT_ENCODING, acceptEncoding);
        }

        public HttpRequest acceptGzipEncoding() {
            return acceptEncoding(ENCODING_GZIP);
        }

        public HttpRequest acceptJson() {
            return accept(CONTENT_TYPE_JSON);
        }

        public HttpRequest authorization(final String authorization) {
            return header(HEADER_AUTHORIZATION, authorization);
        }

        public HttpRequest proxyAuthorization(final String proxyAuthorization) {
            return header(HEADER_PROXY_AUTHORIZATION, proxyAuthorization);
        }

        public HttpRequest basic(final String name, final String password) {
            return authorization("Basic " + Base64.getEncoder().encodeToString((name + ':' + password).getBytes()));
        }

        public HttpRequest proxyBasic(final String name, final String password) {
            return proxyAuthorization("Basic " + Base64.getEncoder().encodeToString((name + ':' + password).getBytes()));
        }

        public HttpRequest ifModifiedSince(final long ifModifiedSince) {
            header(HEADER_IF_MODIFIED_SINCE, formatDate(ifModifiedSince));
            return this;
        }

        public HttpRequest ifNoneMatch(final String ifNoneMatch) {
            return header(HEADER_IF_NONE_MATCH, ifNoneMatch);
        }

        public HttpRequest contentType(final String contentType) {
            return contentType(contentType, null);
        }

        public HttpRequest contentType(final String contentType, final String charset) {
            String mediaType;
            if (!StringUtils.isEmpty(charset)) {
                final String separator = "; " + PARAM_CHARSET + '=';
                mediaType = contentType + separator + charset;
            } else {
                mediaType = contentType;
            }
            this.contentType = MediaType.parse(mediaType);
            return this;
        }

        public HttpRequest part(final String name, final String part) {
            return part(name, null, part);
        }

        public HttpRequest part(final String name, final String filename, final String part) {
            return part(name, filename, null, part);
        }

        public HttpRequest part(final String name, final String filename, final String contentType, final String part) {
            addPart(name, filename, contentType, part);
            return this;
        }

        public HttpRequest part(final String name, final Number part) {
            return part(name, null, part);
        }

        public HttpRequest part(final String name, final String filename, final Number part) {
            return part(name, filename, part != null ? part.toString() : null);
        }

        public HttpRequest part(final String name, final File part) {
            return part(name, null, part);
        }

        public HttpRequest part(final String name, final String filename, final File part) {
            return part(name, filename, null, part);
        }

        public HttpRequest part(final String name, final String filename, final String contentType, final File part) {
            addPart(name, filename, contentType, part);
            return this;
        }

        public HttpRequest part(final String name, final InputStream part) {
            return part(name, null, part);
        }

        public HttpRequest part(final String name, final String filename, final InputStream part) {
            return part(name, filename, null, part);
        }

        public HttpRequest part(final String name, final String filename, final String contentType, final InputStream part) {
            addPart(name, filename, contentType, part);
            return this;
        }

        public HttpRequest body(final File input) {
            this.requestBody = createRequestBody(input);
            return this;
        }

        public HttpRequest body(final byte[] input) {
            this.requestBody = createRequestBody(input == null ? new byte[0] : input);
            return this;
        }

        public HttpRequest body(final InputStream input) {
            this.requestBody = createRequestBody(input);
            return this;
        }

        public HttpRequest body(final CharSequence value) {
            this.requestBody = createRequestBody(value);
            return this;
        }

        public HttpRequest jsonBody(final Object value) {
            if (this.contentType == null) {
                contentType(CONTENT_TYPE_JSON, CHARSET_UTF8);
            }
            return body(JSON.writeAsString(value));
        }

        /**
         * Write the values in the map as form data to the request requestBody
         * <p>
         * The pairs specified will be URL-encoded in specified Charset and sent with the
         * 'application/x-www-form-urlencoded' content-type
         */
        public HttpRequest form(final Map<?, ?> values) {
            for (Entry<?, ?> entry : values.entrySet()) {
                form(entry);
            }
            return this;
        }

        public HttpRequest form(final Entry<?, ?> entry) {
            return form(entry.getKey(), entry.getValue());
        }

        public HttpRequest form(final Object name, final Object value) {
            if (formBody == null) {
                if (contentType == null) {
                    contentType(CONTENT_TYPE_FORM, CHARSET_UTF8);
                }
                final Charset charset = contentType == null ? null : contentType.charset();
                formBody = charset == null ? new FormBody.Builder() : new FormBody.Builder(charset);
            }
            formBody.addEncoded(Objects.toString(name), Objects.toString(value));
            return this;
        }

        @SneakyThrows
        public HttpResponse execute() {
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.headers(headers.build());
            if (requestBody != null) {
                builder.method(method, delegate(requestBody));
            } else if (formBody != null) {
                builder.method(method, delegate(formBody.build()));
            } else if (multipartBody != null) {
                builder.method(method, delegate(multipartBody.build()));
            } else {
                if (Objects.equals(METHOD_POST, method)) {
                    requestBody = RequestBody.create(contentType, "");
                }
                builder.method(method, requestBody);
            }
            Call call = getOkHttpClient().newCall(builder.build());
            return new HttpResponse(call.execute());
        }

        private RequestBody delegate(RequestBody origin) {
            return new RequestBody() {
                @Nullable
                @Override
                public MediaType contentType() {
                    return contentType != null ? contentType : origin.contentType();
                }

                @Override
                public long contentLength() throws IOException {
                    return origin.contentLength();
                }

                @Override
                public void writeTo(@Nonnull BufferedSink sink) throws IOException {
                    origin.writeTo(sink);
                }
            };
        }

        private <T> void addPart(String name, String filename, String contentType, @NonNull T part) {
            MediaType mediaType = parseMediaType(contentType);
            RequestBody requestBody = createRequestBody(mediaType, part);
            if (multipartBody == null) {
                this.multipartBody = new MultipartBody.Builder();
            }
            multipartBody.addFormDataPart(name, filename, requestBody);
        }

        private <T> RequestBody createRequestBody(@NonNull T body) {
            String contentType = null;
            if (this.headers != null) {
                contentType = this.headers.get(HEADER_CONTENT_TYPE);
            }
            MediaType mediaType = parseMediaType(contentType);
            return createRequestBody(mediaType, body);
        }

        private MediaType parseMediaType(final String contentType) {
            MediaType mediaType = null;
            if (!StringUtils.isEmpty(contentType)) {
                mediaType = MediaType.parse(contentType);
            }
            return mediaType;
        }

        private RequestBody createRequestBody(MediaType mediaType, @NonNull Object body) {
            Class<?> type = body.getClass();
            if (String.class.isAssignableFrom(type)) {
                return RequestBody.create(mediaType, (String) body);
            }
            if (CharSequence.class.isAssignableFrom(type)) {
                return RequestBody.create(mediaType, body.toString());
            }
            if (byte[].class.isAssignableFrom(type)) {
                return RequestBody.create(mediaType, (byte[]) body);
            }
            if (File.class.isAssignableFrom(type)) {
                return RequestBody.create(mediaType, (File) body);
            }
            if (InputStream.class.isAssignableFrom(type)) {
                return new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return mediaType;
                    }

                    @Override
                    public void writeTo(@Nonnull BufferedSink sink) throws IOException {
                        try (Source source = Okio.source((InputStream) body)) {
                            sink.writeAll(source);
                        }
                    }
                };
            }
            throw EX.wrap("Cannot create request requestBody instance for %s", body);
        }

    }

    public static class HttpResponse implements AutoCloseable {

        private final Response response;

        public HttpResponse(@NonNull Response response) {
            this.response = response;

        }

        public URL url() {
            return response.request().url().url();
        }

        public String method() {
            return response.request().method();
        }

        public int code() {
            return response.code();
        }

        public boolean ok() {
            return HTTP_OK == code();
        }

        public boolean created() {
            return HTTP_CREATED == code();
        }

        public boolean noContent() {
            return HTTP_NO_CONTENT == code();
        }

        public boolean serverError() {
            return HTTP_INTERNAL_ERROR == code();
        }

        public boolean badRequest() {
            return HTTP_BAD_REQUEST == code();
        }

        public boolean notFound() {
            return HTTP_NOT_FOUND == code();
        }

        public boolean notModified() {
            return HTTP_NOT_MODIFIED == code();
        }

        public String message() {
            return response.message();
        }

        public String header(final String name) {
            return response.header(name);
        }

        public String[] headers(final String name) {
            List<String> headers = response.headers(name);
            if (headers == null || headers.isEmpty()) {
                return EMPTY_STRINGS;
            }
            return headers.toArray(new String[headers.size()]);
        }

        public Map<String, List<String>> headers() {
            return response.headers().toMultimap();
        }

        /**
         * Get a date header from the response falling back to returning -1 if the
         * header is missing or parsing fails
         *
         * @param name
         * @return date, -1 on failures
         */
        public long dateHeader(final String name) {
            return dateHeader(name, -1L);
        }

        public long dateHeader(final String name, final long defaultValue) {
            String dateString = header(name);
            long result = parseDate(dateString);
            return result != -1 ? result : defaultValue;
        }

        /**
         * Get an integer header from the response falling back to returning -1 if the
         * header is missing or parsing fails
         *
         * @param name
         * @return header value as an integer, -1 when missing or parsing fails
         */
        public long longHeader(final String name) {
            return longHeader(name, -1L);
        }

        public long longHeader(final String name, final long defaultValue) {
            String longString = header(name);
            if (StringUtils.isEmpty(longString)) {
                return defaultValue;
            }
            try {
                return Long.parseLong(longString);
            } catch (Exception ignore) {
            }
            return defaultValue;
        }

        /**
         * Get all parameters from header value in response
         * <p>
         * This will be all key=value pairs after the first ';' that are separated by a ';'
         */
        public Map<String, String> parameters(final String headerName) {
            return getParams(header(headerName));
        }

        public String parameter(final String headerName, final String paramName) {
            return getParam(header(headerName), paramName);
        }

        public String charset() {
            return responseBody()
                    .map(ResponseBody::contentType)
                    .map(MediaType::charset)
                    .map(Charset::displayName)
                    .orElseGet(() -> parameter(HEADER_CONTENT_TYPE, PARAM_CHARSET));
        }

        public long date() {
            return dateHeader(HEADER_DATE);
        }

        public String contentEncoding() {
            return header(HEADER_CONTENT_ENCODING);
        }

        public String server() {
            return header(HEADER_SERVER);
        }

        public String cacheControl() {
            return header(HEADER_CACHE_CONTROL);
        }

        public String eTag() {
            return header(HEADER_ETAG);
        }

        public long expires() {
            return dateHeader(HEADER_EXPIRES);
        }

        public long lastModified() {
            return dateHeader(HEADER_LAST_MODIFIED);
        }

        public String location() {
            return header(HEADER_LOCATION);
        }

        public String contentType() {
            return responseBody()
                    .map(ResponseBody::contentType)
                    .map(MediaType::toString)
                    .orElseGet(() -> header(HEADER_CONTENT_TYPE));
        }

        public long contentLength() {
            return responseBody()
                    .map(ResponseBody::contentLength)
                    .orElseGet(() -> longHeader(HEADER_CONTENT_LENGTH));
        }

        public Optional<String> body() {
            return responseBody().map(EX.unchecked(ResponseBody::string));
        }

        public Optional<byte[]> bytes() {
            return responseBody().map(EX.unchecked(ResponseBody::bytes));
        }

        public <T> Optional<T> json(final Class<T> type) {
            return body().map(s -> JSON.readObject(s, type));
        }

        public <T> Optional<List<T>> jsonArray(final Class<T> type) {
            return body().map(s -> JSON.readArray(s, type));
        }

        public Optional<InputStream> stream() {
            return responseBody().map(EX.unchecked(ResponseBody::byteStream));
        }

        public Optional<Reader> reader() {
            return responseBody().map(EX.unchecked(ResponseBody::charStream));
        }

        public HttpResponse body(final Consumer<String> consumer) {
            body().ifPresent(consumer);
            return this;
        }

        @SneakyThrows
        public HttpResponse receive(final File file) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                return receive(outputStream);
            }
        }

        @SneakyThrows
        public HttpResponse receive(final OutputStream output) {
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    try (InputStream inputStream = body.byteStream()) {
                        ByteStreams.copy(inputStream, output);
                    }
                }
            }
            return this;
        }

        @SneakyThrows
        public HttpResponse receive(final Appendable appendable) {
            receive(CharStreams.asWriter(appendable));
            return this;
        }

        @SneakyThrows
        public HttpResponse receive(final Writer writer) {
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    try (Reader reader = body.charStream()) {
                        CharStreams.copy(reader, writer);
                    }
                }
            }
            return this;
        }

        @SneakyThrows
        @Override
        public void close() {
            if (response != null) {
                response.close();
            }
        }

        Optional<ResponseBody> responseBody() {
            return Optional.ofNullable(response.body());
        }

        /**
         * Get parameter values from header value
         */
        Map<String, String> getParams(final String header) {
            if (header == null || header.length() == 0) {
                return Collections.emptyMap();
            }

            final int headerLength = header.length();
            int start = header.indexOf(';') + 1;
            if (start == 0 || start == headerLength) {
                return Collections.emptyMap();
            }

            int end = header.indexOf(';', start);
            if (end == -1) {
                end = headerLength;
            }

            Map<String, String> params = new LinkedHashMap<>();
            while (start < end) {
                int nameEnd = header.indexOf('=', start);

                if (nameEnd != -1 && nameEnd < end) {
                    String name = header.substring(start, nameEnd).trim();
                    if (name.length() > 0) {
                        String value = header.substring(nameEnd + 1, end).trim();
                        int length = value.length();
                        if (length != 0) {
                            if (length > 2 && '"' == value.charAt(0)
                                    && '"' == value.charAt(length - 1)) {
                                params.put(name, value.substring(1, length - 1));
                            } else {
                                params.put(name, value);
                            }
                        }
                    }
                }

                start = end + 1;
                end = header.indexOf(';', start);
                if (end == -1) {
                    end = headerLength;
                }
            }

            return params;
        }


        /**
         * Get parameter value from header value
         */
        String getParam(final String value, final String paramName) {
            if (value == null || value.length() == 0) {
                return null;
            }

            final int length = value.length();
            int start = value.indexOf(';') + 1;
            if (start == 0 || start == length) {
                return null;
            }

            int end = value.indexOf(';', start);
            if (end == -1) {
                end = length;
            }

            while (start < end) {
                int nameEnd = value.indexOf('=', start);
                if (nameEnd != -1 && nameEnd < end
                        && paramName.equals(value.substring(start, nameEnd).trim())) {
                    String paramValue = value.substring(nameEnd + 1, end).trim();
                    int valueLength = paramValue.length();
                    if (valueLength != 0) {
                        if (valueLength > 2 && '"' == paramValue.charAt(0)
                                && '"' == paramValue.charAt(valueLength - 1)) {
                            return paramValue.substring(1, valueLength - 1);
                        } else {
                            return paramValue;
                        }
                    }
                }

                start = end + 1;
                end = value.indexOf(';', start);
                if (end == -1) {
                    end = length;
                }
            }

            return null;
        }

    }

}

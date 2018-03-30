package com.github.beihaifeiwu.sakura.common.http;

import com.github.beihaifeiwu.sakura.common.jackson.JSON;
import com.github.beihaifeiwu.sakura.common.lang.EX;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static com.github.beihaifeiwu.sakura.common.http.HTTP.*;

/**
 * Created by liupin on 2017/5/9.
 */
public class HttpRequest {

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
        RequestBody body = createRequestBody(mediaType, part);
        if (multipartBody == null) {
            this.multipartBody = new MultipartBody.Builder();
        }
        multipartBody.addFormDataPart(name, filename, body);
    }

    private <T> RequestBody createRequestBody(@NonNull T body) {
        String type = null;
        if (this.headers != null) {
            type = this.headers.get(HEADER_CONTENT_TYPE);
        }
        MediaType mediaType = parseMediaType(type);
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

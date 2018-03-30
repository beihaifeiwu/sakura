package com.github.beihaifeiwu.sakura.common.http;

import com.github.beihaifeiwu.sakura.common.jackson.JSON;
import com.github.beihaifeiwu.sakura.common.lang.EX;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;

import static com.github.beihaifeiwu.sakura.common.http.HTTP.*;
import static java.net.HttpURLConnection.*;

/**
 * Created by liupin on 2017/5/9.
 */
public class HttpResponse implements AutoCloseable {

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

    private Optional<ResponseBody> responseBody() {
        return Optional.ofNullable(response.body());
    }

    /**
     * Get parameter values from header value
     */
    private Map<String, String> getParams(final String header) {
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
    private String getParam(final String value, final String paramName) {
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

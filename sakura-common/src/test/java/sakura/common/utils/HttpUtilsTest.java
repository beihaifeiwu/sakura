package sakura.common.utils;

import sakura.common.http.HTTP;
import sakura.common.http.HttpRequest;
import sakura.common.http.HttpResponse;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import lombok.Cleanup;
import lombok.Data;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.Route;
import spark.Spark;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;

/**
 * Created by liupin on 2017/9/13.
 */
public class HttpUtilsTest {

    private static final AtomicReference<String> METHOD = new AtomicReference<>();
    private static final AtomicReference<String> BODY = new AtomicReference<>();
    private static final AtomicReference<Integer> CONTENT_LENGTH = new AtomicReference<>();
    private static final AtomicReference<String> CONTENT_TYPE = new AtomicReference<>();
    private static final AtomicReference<String> USER_AGENT = new AtomicReference<>();
    private static final AtomicReference<String> USER = new AtomicReference<>();
    private static final AtomicReference<String> PASSWORD = new AtomicReference<>();

    private static final Map<String, String> REQ_HEADERS = new CaseInsensitiveMap<>();
    private static final Map<String, String> REQ_PARAMS = new HashMap<>();
    private static final Multimap<String, String> RES_HEADERS = ArrayListMultimap.create();

    @BeforeClass
    public static void startServer() {
        Spark.get("/empty", router(HTTP_OK));
        Spark.get("/non_empty", router(HTTP_OK, "hello"));
        Spark.get("/no_content", router(HTTP_NO_CONTENT));
        Spark.get("/get", router(HTTP_OK, "text/plain; charset=UTF-8", null));
        Spark.get("/get_2", router(HTTP_OK, "text/plain; param1=val1; charset=UTF-8", null));
        Spark.delete("/empty", router(HTTP_OK));
        Spark.options("/empty", router(HTTP_OK));
        Spark.head("/empty", router(HTTP_OK));
        Spark.put("/empty", router(HTTP_OK));
        Spark.trace("/empty", router(HTTP_OK));
        Spark.post("/post", router(HTTP_OK));
        Spark.get("/json", router(HTTP_OK, HTTP.CONTENT_TYPE_JSON, "{\"name\":\"Jack\", \"age\":25}"));
        Spark.post("/json", router(HTTP_OK));
        Spark.awaitInitialization();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Test
    public void testGetEmpty() {
        @Cleanup HttpResponse response = HTTP.get(url("/empty")).execute();
        assertTrue(response.ok());
        assertFalse(response.created());
        assertFalse(response.badRequest());
        assertFalse(response.serverError());
        assertFalse(response.notFound());
        assertFalse(response.notModified());
        assertEquals("GET", METHOD.get());
        assertEquals("OK", response.message());
        assertEquals("", response.body().orElse(null));
        assertEquals(HTTP_OK, response.code());
    }

    @Test
    public void testGetNoContent() {
        @Cleanup HttpResponse response = HTTP.get(url("/no_content")).execute();
        assertFalse(response.ok());
        assertTrue(response.noContent());
        assertEquals("GET", METHOD.get());
        assertEquals("GET", response.method());
        assertEquals("No Content", response.message());
        assertEquals("", response.body().orElse(null));
        assertEquals(HTTP_NO_CONTENT, response.code());
    }

    @Test
    public void testGetWithResponseCharset() {
        @Cleanup HttpResponse r1 = HTTP.get(url("/get")).execute();
        assertTrue(r1.ok());
        Assert.assertEquals(HTTP.CHARSET_UTF8, r1.charset().toUpperCase());

        @Cleanup HttpResponse r2 = HTTP.get(url("/get_2")).execute();
        assertTrue(r2.ok());
        Assert.assertEquals(HTTP.CHARSET_UTF8, r2.charset().toUpperCase());
    }

    @Test
    public void testGetNonEmpty() {
        @Cleanup HttpResponse r1 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r1.ok());
        assertEquals("hello", r1.body().orElse(null));
//        assertEquals("hello".getBytes().length, response.contentLength());

        @Cleanup HttpResponse r2 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r2.ok());
        assertTrue(Arrays.equals("hello".getBytes(), r2.bytes().orElse(null)));
    }

    @Test
    public void testGetError() {
        @Cleanup HttpResponse response = HTTP.get(url("/not_found")).execute();
        assertTrue(response.notFound());
        assertTrue(response.server().contains("Jetty"));
        assertTrue(response.date() > -1);
        assertEquals(response.contentType(), "text/html;charset=utf-8");
    }

    @Test
    public void testHeaders() {
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("h1", "v1");
        reqHeaders.put("h2", "v2");

        @Cleanup HttpResponse response = HTTP.get(url("/non_empty"))
                .headers(reqHeaders)
                .execute();
        assertTrue(response.ok());
        assertEquals("hello", response.body().orElse(null));
        assertEquals("v1", REQ_HEADERS.get("h1"));
        assertEquals("v2", REQ_HEADERS.get("h2"));
    }

    @Test
    public void testAllHeaders() {
        RES_HEADERS.put("a", "a");
        RES_HEADERS.put("b", "b");
        RES_HEADERS.put("a", "another");

        @Cleanup HttpResponse response = HTTP.get(url("/non_empty")).execute();
        Map<String, List<String>> resHeaders = response.headers();
        assertEquals(resHeaders.size(), 6);
        assertEquals(resHeaders.get("a").size(), 2);
        assertTrue(resHeaders.get("b").get(0).equals("b"));
    }

    @Test
    public void testNumberHeader() {
        @Cleanup HttpResponse response = HTTP.get(url("/non_empty"))
                .header("h1", 5)
                .header("h2", 60.2)
                .execute();
        assertTrue(response.ok());
        assertEquals("5", REQ_HEADERS.get("h1"));
        assertEquals("60.2", REQ_HEADERS.get("h2"));
    }

    @Test
    public void testUserAgentHeader() {
        @Cleanup HttpResponse response = HTTP.get(url("/non_empty")).execute();
        assertTrue(response.ok());
        assertTrue(USER_AGENT.get().startsWith("okhttp/"));
    }

    @Test
    public void testAcceptHeader() {
        @Cleanup HttpResponse r1 = HTTP.get(url("/non_empty"))
                .accept("text/plain")
                .execute();
        assertTrue(r1.ok());
        assertEquals("text/plain", REQ_HEADERS.get("Accept"));

        @Cleanup HttpResponse r2 = HTTP.get(url("/non_empty"))
                .acceptJson()
                .execute();
        assertTrue(r2.ok());
        assertEquals("application/json", REQ_HEADERS.get("Accept"));

        @Cleanup HttpResponse r3 = HTTP.get(url("/non_empty"))
                .acceptCharset(HTTP.CHARSET_UTF8)
                .execute();
        assertTrue(r3.ok());
        Assert.assertEquals(HTTP.CHARSET_UTF8, REQ_HEADERS.get("Accept-Charset"));

        @Cleanup HttpResponse r4 = HTTP.get(url("/empty"))
                .acceptEncoding("compress")
                .execute();
        assertTrue(r4.ok());
        assertEquals("compress", REQ_HEADERS.get("Accept-Encoding"));
    }

    @Test
    public void testIfNoneMatchHeader() {
        @Cleanup HttpResponse response = HTTP.get(url("/non_empty"))
                .ifNoneMatch("eid")
                .execute();
        assertTrue(response.ok());
        assertEquals("eid", REQ_HEADERS.get("If-None-Match"));
    }

    @Test
    public void testIfModifiedSinceHeader() {
        @Cleanup HttpResponse response = HTTP.get(url("/non_empty"))
                .ifModifiedSince(5000)
                .execute();
        assertTrue(response.ok());
        assertEquals(5000, HTTP.parseDate(REQ_HEADERS.get("If-Modified-Since")));
    }

    @Test
    public void testRefererHeader() {
        @Cleanup HttpResponse response = HTTP.get(url("/non_empty"))
                .referer("http://heroku.com")
                .execute();
        assertTrue(response.ok());
        assertEquals("http://heroku.com", REQ_HEADERS.get("Referer"));
    }

    @Test
    public void testBasicAuthentication() {
        @Cleanup HttpResponse response = HTTP.get(url("/non_empty"))
                .basic("user", "p4ssw0rd")
                .execute();
        assertTrue(response.ok());
        assertEquals("user", USER.get());
        assertEquals("p4ssw0rd", PASSWORD.get());
    }

    @Test
    public void testGetHeaderParameter() {
        RES_HEADERS.put("a", "b;c=d");
        @Cleanup HttpResponse r1 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r1.ok());
        assertEquals("d", r1.parameter("a", "c"));
        assertNull(r1.parameter("a", "e"));
        assertNull(r1.parameter("b", "c"));
        assertTrue(r1.parameters("b").isEmpty());

        RES_HEADERS.put("a", "b;c=d;e=f");
        @Cleanup HttpResponse r2 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r2.ok());
        assertEquals("d", r2.parameter("a", "c"));
        assertEquals("f", r2.parameter("a", "e"));

        RES_HEADERS.put("a", "b;c=\"d\"");
        @Cleanup HttpResponse r3 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r3.ok());
        assertEquals("d", r3.parameter("a", "c"));

        RES_HEADERS.put("a", "b;c=\"d\";e=\"f\"");
        @Cleanup HttpResponse r4 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r4.ok());
        assertEquals("d", r4.parameter("a", "c"));
        assertEquals("f", r4.parameter("a", "e"));

        RES_HEADERS.put("a", "b;c=");
        @Cleanup HttpResponse r5 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r5.ok());
        assertNull(r5.parameter("a", "c"));
        assertTrue(r5.parameters("a").isEmpty());

        RES_HEADERS.put("a", "b;");
        @Cleanup HttpResponse r6 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r6.ok());
        assertNull(r6.parameter("a", "c"));
        assertTrue(r6.parameters("a").isEmpty());
    }

    @Test
    public void testGetHeaderParameters() {
        RES_HEADERS.put("a", "value;b=c;d=e");
        @Cleanup HttpResponse r1 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r1.ok());
        Map<String, String> params = r1.parameters("a");
        assertNotNull(params);
        assertEquals(2, params.size());
        assertEquals("c", params.get("b"));
        assertEquals("e", params.get("d"));

        RES_HEADERS.put("a", "value;b=\"c\";d=\"e\"");
        @Cleanup HttpResponse r2 = HTTP.get(url("/non_empty")).execute();
        assertTrue(r2.ok());
        params = r2.parameters("a");
        assertNotNull(params);
        assertEquals(2, params.size());
        assertEquals("c", params.get("b"));
        assertEquals("e", params.get("d"));
    }

    @Test
    public void testPostEmpty() throws IOException {
        @Cleanup HttpResponse response = HTTP.post(url("/post"))
                .contentType("text/plain")
                .body("")
                .execute();
        assertTrue(response.ok());
        assertFalse(response.notFound());
        assertFalse(response.noContent());
        assertFalse(response.badRequest());
        assertEquals("POST", METHOD.get());
        assertEquals("", BODY.get());
        assertEquals("", response.body().orElse(null));
        assertEquals("text/plain", CONTENT_TYPE.get());
    }

    @Test
    public void testPostBody() throws IOException {
        @Cleanup HttpResponse r1 = HTTP.post(url("/post"))
                .contentType("text/plain", HTTP.CHARSET_UTF8)
                .body("hello")
                .execute();
        assertTrue(r1.ok());
        assertEquals("hello", BODY.get());
        assertEquals("text/plain; charset=UTF-8", CONTENT_TYPE.get());

        File file = createTempFile("hello");
        @Cleanup HttpResponse r2 = HTTP.post(url("/post")).body(file).execute();
        assertTrue(r2.ok());
        assertEquals("hello", BODY.get());

        @Cleanup HttpResponse r3 = HTTP.post(url("/post"))
                .body(new FileInputStream(file))
                .execute();
        assertTrue(r3.ok());
        assertEquals("hello", BODY.get());

        byte[] bytes = "hello".getBytes(HTTP.CHARSET_UTF8);
        @Cleanup HttpResponse r4 = HTTP.post(url("/post")).body(bytes).execute();
        assertTrue(r4.ok());
        assertEquals("hello", BODY.get());
        assertEquals(bytes.length, CONTENT_LENGTH.get().intValue());
    }

    @Test
    public void testPostForm() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("name", "user");
        data.put("number", "100");
        @Cleanup HttpResponse response = HTTP.post(url("/post"))
                .form(data)
                .form("zip", "12345")
                .execute();
        assertTrue(response.ok());
        assertEquals("name=user&number=100&zip=12345", BODY.get());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", CONTENT_TYPE.get());
    }

    @Test
    public void testPostFormWithoutCharset() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("name", "user");
        data.put("number", "100");
        @Cleanup HttpResponse response = HTTP.post(url("/post"))
                .contentType(HTTP.CONTENT_TYPE_FORM)
                .form(data)
                .form("zip", "12345")
                .execute();
        assertTrue(response.ok());
        assertEquals("name=user&number=100&zip=12345", BODY.get());
        assertEquals("application/x-www-form-urlencoded", CONTENT_TYPE.get());
    }

    @Test
    public void testPostFormAsEntries() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("name", "user");
        data.put("number", "100");

        HttpRequest request = HTTP.post(url("/post"));
        data.entrySet().forEach(request::form);

        @Cleanup HttpResponse response = request.execute();
        assertTrue(response.ok());
        assertEquals("name=user&number=100", BODY.get());
    }

    @Test
    public void testQueryParams() {
        Map<String, String> inputParams = new HashMap<>();
        inputParams.put("name", "user");
        inputParams.put("number", "100");
        @Cleanup HttpResponse r1 = HTTP.post(url("/post"), inputParams, false).execute();
        assertTrue(r1.ok());
        assertEquals("POST", METHOD.get());
        assertEquals("user", REQ_PARAMS.get("name"));
        assertEquals("100", REQ_PARAMS.get("number"));

        @Cleanup HttpResponse r2 = HTTP.post(url("/post"), false, "name", "user", "number", "100").execute();
        assertTrue(r2.ok());
        assertEquals("POST", METHOD.get());
        assertEquals("user", REQ_PARAMS.get("name"));
        assertEquals("100", REQ_PARAMS.get("number"));
    }

    @Test
    public void testEscapedQueryParams() {
        Map<String, String> inputParams = new HashMap<>();
        inputParams.put("name", "us er");
        inputParams.put("number", "100");
        @Cleanup HttpResponse r1 = HTTP.post(url("/post"), inputParams, true).execute();
        assertTrue(r1.ok());
        assertEquals("POST", METHOD.get());
        assertEquals("us er", REQ_PARAMS.get("name"));
        assertEquals("100", REQ_PARAMS.get("number"));

        @Cleanup HttpResponse r2 = HTTP.post(url("/post"), true, "name", "us er", "number", "100").execute();
        assertTrue(r2.ok());
        assertEquals("POST", METHOD.get());
        assertEquals("us er", REQ_PARAMS.get("name"));
        assertEquals("100", REQ_PARAMS.get("number"));
    }

    @Test
    public void testNumericQueryParams() {
        Map<Object, Object> inputParams = new HashMap<>();
        inputParams.put(1, 2);
        inputParams.put(3, 4);

        @Cleanup HttpResponse response = HTTP.post(url("/post"), inputParams, false).execute();
        assertTrue(response.ok());
        assertEquals("POST", METHOD.get());
        assertEquals("2", REQ_PARAMS.get("1"));
        assertEquals("4", REQ_PARAMS.get("3"));
    }

    @Test
    public void testPostMultipart() throws IOException {
        File file = createTempFile("content1");
        File file2 = createTempFile("content4");

        @Cleanup HttpResponse r1 = HTTP.post(url("/post"))
                .part("description", "content2")
                .part("size", file.length())
                .part("body", file.getName(), file)
                .part("file", file2)
                .part("stream", new ByteArrayInputStream("content3".getBytes()))
                .execute();
        assertTrue(r1.ok());
        assertTrue(BODY.get().contains("content1\r\n"));
        assertTrue(BODY.get().contains("content2\r\n"));
        assertTrue(BODY.get().contains("content3\r\n"));
        assertTrue(BODY.get().contains("content4\r\n"));
        assertTrue(BODY.get().contains(Long.toString(file.length()) + "\r\n"));

        @Cleanup HttpResponse r2 = HTTP.post(url("/post"))
                .part("body", null, "application/json", "contents")
                .execute();
        assertTrue(r2.ok());
        assertTrue(BODY.get().contains("Content-Type: application/json"));
        assertTrue(BODY.get().contains("contents\r\n"));
    }

    @Test
    public void testReceive() throws Exception {
        final StringBuilder body = new StringBuilder();
        boolean ok = HTTP.get(url("/non_empty")).execute().receive(body).ok();
        assertTrue(ok);
        assertEquals("hello", body.toString());

        final StringWriter writer = new StringWriter();
        ok = HTTP.get(url("/non_empty")).execute().receive(writer).ok();
        assertTrue(ok);
        assertEquals("hello", writer.toString());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ok = HTTP.get(url("/non_empty")).execute().receive(output).ok();
        assertTrue(ok);
        assertEquals("hello", output.toString());

        File file = createTempFile();
        ok = HTTP.get(url("/non_empty")).execute().receive(file).ok();
        assertTrue(ok);
        assertEquals("hello", Files.asCharSource(file, StandardCharsets.UTF_8).read());
    }

    @Test
    public void testJsonCodec() {
        Person person = new Person();
        person.name = "Smith";
        person.age = 42;
        HttpResponse response = HTTP.post(url("/json"))
                .jsonBody(person)
                .execute();
        assertTrue(response.ok());
        assertEquals("application/json; charset=UTF-8", CONTENT_TYPE.get());
        assertEquals("{\"name\":\"Smith\",\"age\":42}",
                CharMatcher.whitespace().removeFrom(BODY.get()));

        Optional<Person> optional = HTTP.get(url("/json"))
                .acceptJson()
                .execute()
                .json(Person.class);
        assertTrue(optional.isPresent());
        Person result = optional.get();
        assertEquals(result.name, "Jack");
        assertEquals(result.age, 25);
    }

    @Test
    public void testDeleteEmpty() {
        @Cleanup HttpResponse response = HTTP.delete(url("/empty")).execute();
        assertTrue(response.ok());
        assertFalse(response.notFound());
        assertEquals("DELETE", METHOD.get());
        assertEquals("", response.body().orElse(null));
    }

    @Test
    public void testOptionsEmpty() {
        @Cleanup HttpResponse response = HTTP.options(url("/empty")).execute();
        assertTrue(response.ok());
        assertFalse(response.notFound());
        assertEquals("OPTIONS", METHOD.get());
        assertEquals("", response.body().orElse(null));
    }

    @Test
    public void testHeadEmpty() {
        @Cleanup HttpResponse response = HTTP.head(url("/empty")).execute();
        assertTrue(response.ok());
        assertFalse(response.notFound());
        assertEquals("HEAD", METHOD.get());
        assertEquals("", response.body().orElse(null));
    }

    @Test
    public void testPutEmpty() {
        @Cleanup HttpResponse response = HTTP.put(url("/empty")).body("").execute();
        assertTrue(response.ok());
        assertFalse(response.notFound());
        assertFalse(response.noContent());
        assertEquals("PUT", METHOD.get());
        assertEquals("", response.body().orElse(null));
    }

    @Test
    public void testTraceEmpty() {
        @Cleanup HttpResponse response = HTTP.trace(url("/empty")).execute();
        assertTrue(response.ok());
        assertFalse(response.notFound());
        assertFalse(response.noContent());
        assertEquals("TRACE", METHOD.get());
        assertEquals("", response.body().orElse(null));
    }

    @Test
    public void testEncode() {
        assertEquals("http://google.com", HTTP.encode("http://google.com"));
        assertEquals("https://google.com", HTTP.encode("https://google.com"));
        assertEquals("http://google.com/a", HTTP.encode("http://google.com/a"));
        assertEquals("http://google.com/a/", HTTP.encode("http://google.com/a/"));
        assertEquals("http://google.com/a/b", HTTP.encode("http://google.com/a/b"));
        assertEquals("http://google.com/a?", HTTP.encode("http://google.com/a?"));
        assertEquals("http://google.com/a?b=c", HTTP.encode("http://google.com/a?b=c"));
        assertEquals("http://google.com/a?b=c%20d", HTTP.encode("http://google.com/a?b=c d"));
        assertEquals("http://google.com/a%20b", HTTP.encode("http://google.com/a b"));
        assertEquals("http://google.com/a.b", HTTP.encode("http://google.com/a.b"));
        assertEquals("http://google.com/%E2%9C%93?a=b", HTTP.encode("http://google.com/\u2713?a=b"));
        assertEquals("http://google.com/a%5Eb", HTTP.encode("http://google.com/a^b"));
        assertEquals("http://google.com/%25", HTTP.encode("http://google.com/%"));
        assertEquals("http://google.com/a.b?c=d.e", HTTP.encode("http://google.com/a.b?c=d.e"));
        assertEquals("http://google.com/a.b?c=d/e", HTTP.encode("http://google.com/a.b?c=d/e"));
        assertEquals("http://google.com/a?%E2%98%91", HTTP.encode("http://google.com/a?\u2611"));
        assertEquals("http://google.com/a?b=%E2%98%90", HTTP.encode("http://google.com/a?b=\u2610"));
        assertEquals("http://google.com/a?b=c%2Bd&e=f%2Bg", HTTP.encode("http://google.com/a?b=c+d&e=f+g"));
        assertEquals("http://google.com/+", HTTP.encode("http://google.com/+"));
        assertEquals("http://google.com/+?a=b%2Bc", HTTP.encode("http://google.com/+?a=b+c"));
    }

    @Test
    public void testAppendParams() {
        assertEquals("http://test.com/?a=b", HTTP.append("http://test.com", Collections.singletonMap("a", "b")));
        assertEquals("http://test.com/?a=b", HTTP.append("http://test.com", "a", "b"));
        assertEquals("http://test.com/segment1?a=b", HTTP.append("http://test.com/segment1", Collections.singletonMap("a", "b")));
        assertEquals("http://test.com/?a=b", HTTP.append("http://test.com/", Collections.singletonMap("a", "b")));
        assertEquals("http://test.com/segment1?a=b", HTTP.append("http://test.com/segment1", "a", "b"));
        assertEquals("http://test.com/?a=b", HTTP.append("http://test.com/", "a", "b"));

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("a", "b");
        params.put("c", "d");
        assertEquals("http://test.com/1?a=b&c=d", HTTP.append("http://test.com/1", params));
        assertEquals("http://test.com/1?a=b&c=d", HTTP.append("http://test.com/1", "a", "b", "c", "d"));

        assertEquals("http://test.com/1", HTTP.append("http://test.com/1", (Map<?, ?>) null));
        assertEquals("http://test.com/1", HTTP.append("http://test.com/1", (Object[]) null));
        assertEquals("http://test.com/1", HTTP.append("http://test.com/1", Collections.<String, String>emptyMap()));
        assertEquals("http://test.com/1", HTTP.append("http://test.com/1", new Object[0]));
        params = new LinkedHashMap<>();
        params.put("a", null);
        params.put("b", null);
        assertEquals("http://test.com/1?a=&b=", HTTP.append("http://test.com/1", params));
        assertEquals("http://test.com/1?a=&b=", HTTP.append("http://test.com/1", "a", null, "b", null));
        assertEquals("http://test.com/1?a=b", HTTP.append("http://test.com/1?", Collections.singletonMap("a", "b")));

        assertEquals("http://test.com/1?a=b", HTTP.append("http://test.com/1?", "a", "b"));
        assertEquals("http://test.com/1?a=b&c=d", HTTP.append("http://test.com/1?a=b", Collections.singletonMap("c", "d")));
        assertEquals("http://test.com/1?a=b&c=d", HTTP.append("http://test.com/1?a=b&", Collections.singletonMap("c", "d")));
        assertEquals("http://test.com/1?a=b&c=d", HTTP.append("http://test.com/1?a=b", "c", "d"));
        assertEquals("http://test.com/1?a=b&c=d", HTTP.append("http://test.com/1?a=b&", "c", "d"));

        assertEquals("http://test.com/?foo[]=bar&foo[]=baz", HTTP.append("http://test.com",
                Collections.singletonMap("foo", new String[]{"bar", "baz"})));
        assertEquals("http://test.com/?a[]=1&a[]=2", HTTP.append("http://test.com",
                Collections.singletonMap("a", new int[]{1, 2})));
        assertEquals("http://test.com/?a[]=1", HTTP.append("http://test.com", Collections.singletonMap("a", new int[]{1})));
        assertEquals("http://test.com/?", HTTP.append("http://test.com", Collections.singletonMap("a", new int[]{})));
        assertEquals("http://test.com/?foo[]=bar&foo[]=baz&a[]=1&a[]=2",
                HTTP.append("http://test.com", "foo", new String[]{"bar", "baz"}, "a", new int[]{1, 2}));

        assertEquals("http://test.com/?foo[]=bar&foo[]=baz", HTTP.append("http://test.com",
                Collections.singletonMap("foo", Arrays.asList("bar", "baz"))));
        assertEquals("http://test.com/?a[]=1&a[]=2", HTTP.append("http://test.com",
                Collections.singletonMap("a", Arrays.asList(1, 2))));
        assertEquals("http://test.com/?a[]=1", HTTP.append("http://test.com",
                Collections.singletonMap("a", Collections.singletonList(1))));
        assertEquals("http://test.com/?", HTTP.append("http://test.com",
                Collections.singletonMap("a", Arrays.asList(new Integer[]{}))));
        assertEquals("http://test.com/?foo[]=bar&foo[]=baz&a[]=1&a[]=2", HTTP.append("http://test.com",
                "foo", Arrays.asList("bar", "baz"), "a", Arrays.asList(1, 2)));
    }

    private static Route router(int code) {
        return router(code, null, null);
    }

    private static Route router(int code, Object result) {
        return router(code, null, result);
    }

    private static Route router(int code, String type, Object result) {
        return (request, response) -> {
            REQ_HEADERS.clear();
            for (String header : request.headers()) {
                REQ_HEADERS.put(header, request.headers(header));
            }

            METHOD.set(request.requestMethod());
            BODY.set(request.body());
            CONTENT_LENGTH.set(request.contentLength());
            CONTENT_TYPE.set(request.contentType());
            USER_AGENT.set(request.userAgent());

            String auth = request.headers("Authorization");
            if (StringUtils.isNoneBlank(auth)) {
                auth = auth.substring(auth.indexOf(' ') + 1);
                auth = new String(Base64.getDecoder().decode(auth), HTTP.CHARSET_UTF8);
                int colon = auth.indexOf(':');
                USER.set(auth.substring(0, colon));
                PASSWORD.set(auth.substring(colon + 1));
            }

            REQ_PARAMS.clear();
            for (String param : request.queryParams()) {
                REQ_PARAMS.put(param, request.queryParams(param));
            }

            RES_HEADERS.forEach(response::header);
            RES_HEADERS.clear();

            response.status(code);
            String contentType = StringUtils.isNotBlank(type) ? type : "text/plain; charset=UTF-8";
            response.type(contentType);
            return result == null ? "" : result;
        };
    }

    private String url(String path) {
        String url = "http://localhost:" + Spark.port();
        if (!path.startsWith("/")) {
            url += "/";
        }
        return url + path;
    }

    private File createTempFile() throws IOException {
        return createTempFile(null);
    }

    private File createTempFile(String content) throws IOException {
        String uuid = UUID.randomUUID().toString();
        File tempFile = File.createTempFile(uuid, ".txt");
        tempFile.deleteOnExit();
        if (StringUtils.isNotBlank(content)) {
            new FileWriter(tempFile).append(content).close();
        }
        return tempFile;
    }

    @Data
    private static class Person {
        private String name;
        private int age;
    }

}

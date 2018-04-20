package sakura.common.lang;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by haomu on 2018/4/20.
 */
public class UrlsTest {

    @Test
    public void testEncode() {
        assertEquals("http://google.com", Urls.encode("http://google.com"));
        assertEquals("https://google.com", Urls.encode("https://google.com"));
        assertEquals("http://google.com/a", Urls.encode("http://google.com/a"));
        assertEquals("http://google.com/a/", Urls.encode("http://google.com/a/"));
        assertEquals("http://google.com/a/b", Urls.encode("http://google.com/a/b"));
        assertEquals("http://google.com/a?", Urls.encode("http://google.com/a?"));
        assertEquals("http://google.com/a?b=c", Urls.encode("http://google.com/a?b=c"));
        assertEquals("http://google.com/a?b=c%20d", Urls.encode("http://google.com/a?b=c d"));
        assertEquals("http://google.com/a%20b", Urls.encode("http://google.com/a b"));
        assertEquals("http://google.com/a.b", Urls.encode("http://google.com/a.b"));
        assertEquals("http://google.com/%E2%9C%93?a=b", Urls.encode("http://google.com/\u2713?a=b"));
        assertEquals("http://google.com/a%5Eb", Urls.encode("http://google.com/a^b"));
        assertEquals("http://google.com/%25", Urls.encode("http://google.com/%"));
        assertEquals("http://google.com/a.b?c=d.e", Urls.encode("http://google.com/a.b?c=d.e"));
        assertEquals("http://google.com/a.b?c=d/e", Urls.encode("http://google.com/a.b?c=d/e"));
        assertEquals("http://google.com/a?%E2%98%91", Urls.encode("http://google.com/a?\u2611"));
        assertEquals("http://google.com/a?b=%E2%98%90", Urls.encode("http://google.com/a?b=\u2610"));
        assertEquals("http://google.com/a?b=c%2Bd&e=f%2Bg", Urls.encode("http://google.com/a?b=c+d&e=f+g"));
        assertEquals("http://google.com/+", Urls.encode("http://google.com/+"));
        assertEquals("http://google.com/+?a=b%2Bc", Urls.encode("http://google.com/+?a=b+c"));
    }

    @Test
    public void testAppend() {
        assertEquals("http://test.com/?a=b", Urls.append("http://test.com", Collections.singletonMap("a", "b")));
        assertEquals("http://test.com/?a=b", Urls.append("http://test.com", "a", "b"));
        assertEquals("http://test.com/segment1?a=b", Urls.append("http://test.com/segment1", Collections.singletonMap("a", "b")));
        assertEquals("http://test.com/?a=b", Urls.append("http://test.com/", Collections.singletonMap("a", "b")));
        assertEquals("http://test.com/segment1?a=b", Urls.append("http://test.com/segment1", "a", "b"));
        assertEquals("http://test.com/?a=b", Urls.append("http://test.com/", "a", "b"));

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("a", "b");
        params.put("c", "d");
        assertEquals("http://test.com/1?a=b&c=d", Urls.append("http://test.com/1", params));
        assertEquals("http://test.com/1?a=b&c=d", Urls.append("http://test.com/1", "a", "b", "c", "d"));

        assertEquals("http://test.com/1", Urls.append("http://test.com/1", (Map<?, ?>) null));
        assertEquals("http://test.com/1", Urls.append("http://test.com/1", (Object[]) null));
        assertEquals("http://test.com/1", Urls.append("http://test.com/1", Collections.<String, String>emptyMap()));
        assertEquals("http://test.com/1", Urls.append("http://test.com/1", new Object[0]));
        params = new LinkedHashMap<>();
        params.put("a", null);
        params.put("b", null);
        assertEquals("http://test.com/1?a=&b=", Urls.append("http://test.com/1", params));
        assertEquals("http://test.com/1?a=&b=", Urls.append("http://test.com/1", "a", null, "b", null));
        assertEquals("http://test.com/1?a=b", Urls.append("http://test.com/1?", Collections.singletonMap("a", "b")));

        assertEquals("http://test.com/1?a=b", Urls.append("http://test.com/1?", "a", "b"));
        assertEquals("http://test.com/1?a=b&c=d", Urls.append("http://test.com/1?a=b", Collections.singletonMap("c", "d")));
        assertEquals("http://test.com/1?a=b&c=d", Urls.append("http://test.com/1?a=b&", Collections.singletonMap("c", "d")));
        assertEquals("http://test.com/1?a=b&c=d", Urls.append("http://test.com/1?a=b", "c", "d"));
        assertEquals("http://test.com/1?a=b&c=d", Urls.append("http://test.com/1?a=b&", "c", "d"));

        assertEquals("http://test.com/?foo[]=bar&foo[]=baz", Urls.append("http://test.com",
                Collections.singletonMap("foo", new String[]{"bar", "baz"})));
        assertEquals("http://test.com/?a[]=1&a[]=2", Urls.append("http://test.com",
                Collections.singletonMap("a", new int[]{1, 2})));
        assertEquals("http://test.com/?a[]=1", Urls.append("http://test.com", Collections.singletonMap("a", new int[]{1})));
        assertEquals("http://test.com/?", Urls.append("http://test.com", Collections.singletonMap("a", new int[]{})));
        assertEquals("http://test.com/?foo[]=bar&foo[]=baz&a[]=1&a[]=2",
                Urls.append("http://test.com", "foo", new String[]{"bar", "baz"}, "a", new int[]{1, 2}));

        assertEquals("http://test.com/?foo[]=bar&foo[]=baz", Urls.append("http://test.com",
                Collections.singletonMap("foo", Arrays.asList("bar", "baz"))));
        assertEquals("http://test.com/?a[]=1&a[]=2", Urls.append("http://test.com",
                Collections.singletonMap("a", Arrays.asList(1, 2))));
        assertEquals("http://test.com/?a[]=1", Urls.append("http://test.com",
                Collections.singletonMap("a", Collections.singletonList(1))));
        assertEquals("http://test.com/?", Urls.append("http://test.com",
                Collections.singletonMap("a", Arrays.asList(new Integer[]{}))));
        assertEquals("http://test.com/?foo[]=bar&foo[]=baz&a[]=1&a[]=2", Urls.append("http://test.com",
                "foo", Arrays.asList("bar", "baz"), "a", Arrays.asList(1, 2)));
    }
    
}

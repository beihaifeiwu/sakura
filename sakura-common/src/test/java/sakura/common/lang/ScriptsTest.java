package sakura.common.lang;

import lombok.val;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by haomu on 2018/5/3.
 */
public class ScriptsTest {

    @Test
    public void testInvocable() throws Exception {
        val url = CLS.getResourceAsURL("nashorn_test.js");
        assertNotNull(url);
        String script = IOUtils.toString(url, StandardCharsets.UTF_8);
        val invocable = Scripts.getInvocable("JavaScript", script);
        val result = invocable.invokeFunction("fun1", "Peter Parker");
        assertEquals("greetings from javascript", result);

        invocable.invokeFunction("fun2", new Date()); // JS Class Definition: [object java.util.Date]
        invocable.invokeFunction("fun2", LocalDateTime.now()); // JS Class Definition: [object java.time.LocalDateTime]
    }

}

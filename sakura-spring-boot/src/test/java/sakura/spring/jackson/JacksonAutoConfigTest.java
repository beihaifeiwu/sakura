package sakura.spring.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import sakura.common.jackson.Jackson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sakura.spring.autoconfigure.JacksonExtAutoConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by liupin on 2017/3/31.
 */
public class JacksonAutoConfigTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setup() {
        context = new AnnotationConfigApplicationContext();
        context.register(JacksonExtAutoConfiguration.class);
    }

    @After
    public void teardown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testJacksonAutoConfigured() {
        context.refresh();
        ObjectMapper mapper = context.getBean(ObjectMapper.class);
        assertNotNull(mapper);
        assertEquals(mapper, Jackson.getObjectMapper());
    }

    @Test
    public void testsJacksonProperties() {
        TestPropertyValues.of("spring.jackson.default-property-inclusion=non_null").applyTo(context);
        TestPropertyValues.of("spring.jackson.deserialization.accept-float-as-int=false").applyTo(context);
        TestPropertyValues.of("spring.jackson.deserialization.fail-on-unknown-properties=false").applyTo(context);
        TestPropertyValues.of("spring.jackson.serialization.indent_output=true").applyTo(context);
        TestPropertyValues.of("spring.jackson.mapper.use-static-typing=false").applyTo(context);
        context.refresh();

        ObjectMapper mapper = context.getBean(ObjectMapper.class);
        assertNotNull(mapper);
        assertEquals(mapper, Jackson.getObjectMapper());
        assertEquals(mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion(), JsonInclude.Include.NON_NULL);
        assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT));
        assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertFalse(mapper.isEnabled(MapperFeature.USE_STATIC_TYPING));
    }
}

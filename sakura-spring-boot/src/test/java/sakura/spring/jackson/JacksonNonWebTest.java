package sakura.spring.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import sakura.common.jackson.Jackson;
import sakura.spring.core.SpringBeans;
import sakura.spring.test.SakuraSpringTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by liupin on 2017/4/10.
 */
public class JacksonNonWebTest extends SakuraSpringTest {

    @Test
    public void testGetObjectMapper() {
        ObjectMapper mapper = SpringBeans.getBean(ObjectMapper.class).orElse(null);
        assertThat(mapper).isNotNull().isEqualTo(Jackson.getObjectMapper());
        assertConfigs(mapper);
        assertModules(mapper);
    }

    private void assertConfigs(ObjectMapper mapper) {
        assertEquals(mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion(), JsonInclude.Include.NON_NULL);
        assertTrue(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT));
        assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertFalse(mapper.isEnabled(MapperFeature.USE_STATIC_TYPING));
        assertTrue(mapper.isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void assertModules(ObjectMapper mapper) {
        Object moduleTypes = FieldUtils.readField(mapper, "_registeredModuleTypes", true);
        assertThat(moduleTypes).isNotNull().isInstanceOf(Set.class);
        Set<Object> modules = (Set<Object>) moduleTypes;
        assertThat(modules)
                .contains("sakura.common.jackson.module.FuzzyEnumModule")
                .contains("sakura.common.jackson.module.GuavaExtrasModule")
                .contains("com.fasterxml.jackson.datatype.guava.GuavaModule")
                .contains("com.fasterxml.jackson.datatype.jdk8.Jdk8Module")
                .contains("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
                .contains("com.fasterxml.jackson.module.paramnames.ParameterNamesModule");
    }

}

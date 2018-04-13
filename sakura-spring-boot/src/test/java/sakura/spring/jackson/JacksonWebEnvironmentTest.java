package sakura.spring.jackson;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import sakura.common.jackson.Jackson;
import sakura.spring.test.SakuraSpringWebTest;
import sakura.spring.core.SpringBeans;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by liupin on 2017/4/10.
 */
public class JacksonWebEnvironmentTest extends SakuraSpringWebTest {

    @Test
    public void testGetObjectMapper() {
        ObjectMapper mapper = SpringBeans.getBean(ObjectMapper.class).orElse(null);
        assertThat(mapper).isNotNull().isEqualTo(Jackson.getObjectMapper());
        assertFeatureConfig(mapper);
        assertModules(mapper);
    }

    @Test
    public void testFeatures() {
        Employee employee = new Employee();
        employee.setName("Jack");
        employee.setAge(22);
        employee.setOther("address", "ShangHai");
        employee.setCoordinate(new Coordinate(30, 120));

        String jsonString = Jackson.writeAsString(employee);
        assertThat(jsonString)
                .isNotBlank()
                .contains("\"address\" : \"ShangHai\"")
                .contains("\"x\" : 30.0")
                .doesNotContain("\"z\" :");
        assertThat(Jackson.readObject(jsonString, Employee.class))
                .isNotNull()
                .isEqualToComparingFieldByFieldRecursively(employee);
    }

    private void assertFeatureConfig(ObjectMapper mapper) {
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
                .contains("com.github.beihaifeiwu.sakura.common.jackson.FuzzyEnumModule")
                .contains("com.github.beihaifeiwu.sakura.common.jackson.GuavaExtrasModule")
                .contains("com.fasterxml.jackson.datatype.guava.GuavaModule")
                .contains("com.fasterxml.jackson.datatype.jdk8.Jdk8Module")
                .contains("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
                .contains("com.fasterxml.jackson.module.paramnames.ParameterNamesModule");
    }

    @Data
    public static class Employee {
        private String name;
        private int age;
        private Coordinate coordinate;

        @JsonIgnore
        private Map<String, Object> other = Maps.newHashMap();

        @JsonAnyGetter
        public Map<String, Object> getOther() {
            return other;
        }

        @JsonAnySetter
        public void setOther(String name, Object value) {
            other.put(name, value);
        }
    }
}

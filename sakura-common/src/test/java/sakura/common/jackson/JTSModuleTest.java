package sakura.common.jackson;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;
import lombok.Data;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by haomu on 2018/4/17.
 */
public class JTSModuleTest {

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

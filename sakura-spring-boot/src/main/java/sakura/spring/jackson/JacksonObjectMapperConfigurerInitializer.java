package sakura.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import sakura.common.jackson.Jackson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by liupin on 2017/4/13.
 */
public class JacksonObjectMapperConfigurerInitializer implements InitializingBean {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private List<ObjectMapper> objectMappers;

    @Autowired(required = false)
    private List<JacksonObjectMapperConfigurer> configurers;

    @Override
    public void afterPropertiesSet() throws Exception {
        // set primary object mapper to Jackson
        if (!Objects.equals(objectMapper, Jackson.getObjectMapper())) {
            Jackson.setObjectMapper(objectMapper);
        }
        if (!CollectionUtils.isEmpty(objectMappers)) {
            for (ObjectMapper mapper : objectMappers) {
                configure(mapper);
            }
        }
    }

    private void configure(ObjectMapper mapper) {
        if (!CollectionUtils.isEmpty(configurers)) {
            for (JacksonObjectMapperConfigurer objectMapperConfigurer : configurers) {
                objectMapperConfigurer.configure(mapper);
            }
        }
    }

}

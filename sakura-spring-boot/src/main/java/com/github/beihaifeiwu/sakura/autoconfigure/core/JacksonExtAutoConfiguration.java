package com.github.beihaifeiwu.sakura.autoconfigure.core;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.beihaifeiwu.sakura.common.jackson.JSON;
import com.github.beihaifeiwu.sakura.jackson.JacksonObjectMapperConfigurer;
import com.github.beihaifeiwu.sakura.jackson.JacksonObjectMapperConfigurerInitializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Created by liupin on 2017/3/17.
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@EnableConfigurationProperties(JacksonProperties.class)
public class JacksonExtAutoConfiguration {

    private final JacksonProperties properties;

    public JacksonExtAutoConfiguration(JacksonProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public JacksonObjectMapperConfigurer jacksonObjectMapperConfigurer(ObjectProvider<List<Module>> modules) {
        return new JacksonObjectMapperConfigurer(properties, modules.getIfAvailable());
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(value = ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return JSON.getObjectMapper();
    }

    @Bean
    public JacksonObjectMapperConfigurerInitializer jacksonObjectMapperConfigurerInitializer() {
        return new JacksonObjectMapperConfigurerInitializer();
    }

}

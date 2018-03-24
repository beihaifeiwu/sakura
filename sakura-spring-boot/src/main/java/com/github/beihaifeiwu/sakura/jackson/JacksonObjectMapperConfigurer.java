package com.github.beihaifeiwu.sakura.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.github.beihaifeiwu.sakura.core.Configurer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by liupin on 2017/3/20.
 */
public class JacksonObjectMapperConfigurer implements Configurer<ObjectMapper> {

    private final JacksonProperties properties;

    @Nullable
    private final List<Module> modules;

    public JacksonObjectMapperConfigurer(JacksonProperties properties, @Nullable List<Module> modules) {
        this.properties = properties;
        this.modules = modules;
    }

    @Override
    public void configure(ObjectMapper objectMapper) {
        if (this.properties.getDefaultPropertyInclusion() != null) {
            objectMapper.setSerializationInclusion(properties.getDefaultPropertyInclusion());
        }
        if (this.properties.getTimeZone() != null) {
            objectMapper.setTimeZone(properties.getTimeZone());
        }
        configureFeatures(objectMapper, properties.getDeserialization());
        configureFeatures(objectMapper, properties.getSerialization());
        configureFeatures(objectMapper, properties.getMapper());
        configureFeatures(objectMapper, properties.getParser());
        configureFeatures(objectMapper, properties.getGenerator());
        configureDateFormat(objectMapper);
        configurePropertyNamingStrategy(objectMapper);
        configureModules(objectMapper);
        configureLocale(objectMapper);
    }

    private void configureFeatures(ObjectMapper objectMapper, Map<?, Boolean> features) {
        for (Map.Entry<?, Boolean> entry : features.entrySet()) {
            if (entry.getValue() != null && entry.getValue()) {
                configureFeature(objectMapper, entry.getKey(), true);
            } else {
                configureFeature(objectMapper, entry.getKey(), false);
            }
        }
    }

    private void configureFeature(ObjectMapper objectMapper, Object feature, boolean enabled) {
        if (feature instanceof JsonParser.Feature) {
            objectMapper.configure((JsonParser.Feature) feature, enabled);
        } else if (feature instanceof JsonGenerator.Feature) {
            objectMapper.configure((JsonGenerator.Feature) feature, enabled);
        } else if (feature instanceof SerializationFeature) {
            objectMapper.configure((SerializationFeature) feature, enabled);
        } else if (feature instanceof DeserializationFeature) {
            objectMapper.configure((DeserializationFeature) feature, enabled);
        } else if (feature instanceof MapperFeature) {
            objectMapper.configure((MapperFeature) feature, enabled);
        } else {
            throw new FatalBeanException("Unknown feature class: " + feature.getClass().getName());
        }
    }

    private void configureDateFormat(ObjectMapper objectMapper) {
        // We support a fully qualified class name extending DateFormat or a date
        // pattern string value
        String dateFormat = this.properties.getDateFormat();
        if (dateFormat != null) {
            try {
                Class<?> dateFormatClass = ClassUtils.forName(dateFormat, null);
                objectMapper.setDateFormat((DateFormat) BeanUtils.instantiateClass(dateFormatClass));
            } catch (ClassNotFoundException ex) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        dateFormat);
                // Since Jackson 2.6.3 we always need to set a TimeZone (see
                // gh-4170). If none in our securityExtProperties fallback to the Jackson's
                // default
                TimeZone timeZone = this.properties.getTimeZone();
                if (timeZone == null) {
                    timeZone = new ObjectMapper().getSerializationConfig().getTimeZone();
                }
                simpleDateFormat.setTimeZone(timeZone);
                objectMapper.setDateFormat(simpleDateFormat);
            }
        }
    }

    private void configurePropertyNamingStrategy(ObjectMapper objectMapper) {
        // We support a fully qualified class name extending Jackson's
        // PropertyNamingStrategy or a string value corresponding to the constant
        // names in PropertyNamingStrategy which hold default provided
        // implementations
        String strategy = this.properties.getPropertyNamingStrategy();
        if (strategy != null) {
            try {
                configurePropertyNamingStrategyClass(objectMapper, ClassUtils.forName(strategy, null));
            } catch (ClassNotFoundException ex) {
                configurePropertyNamingStrategyField(objectMapper, strategy);
            }
        }
    }

    private void configurePropertyNamingStrategyClass(ObjectMapper objectMapper, Class<?> propertyNamingStrategyClass) {
        objectMapper.setPropertyNamingStrategy((PropertyNamingStrategy) BeanUtils.instantiateClass(propertyNamingStrategyClass));
    }

    private void configurePropertyNamingStrategyField(ObjectMapper objectMapper, String fieldName) {
        // Find the field (this way we automatically support new constants
        // that may be added by Jackson in the future)
        Field field = ReflectionUtils.findField(PropertyNamingStrategy.class, fieldName, PropertyNamingStrategy.class);
        Assert.notNull(field, "Constant named '" + fieldName + "' not found on " + PropertyNamingStrategy.class.getName());
        try {
            objectMapper.setPropertyNamingStrategy((PropertyNamingStrategy) field.get(null));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void configureModules(ObjectMapper objectMapper) {
        if (!CollectionUtils.isEmpty(modules)) {
            objectMapper.registerModules(modules);
        }
        try {
            objectMapper.findAndRegisterModules();
        } catch (Throwable ignore) {
        }
    }

    private void configureLocale(ObjectMapper objectMapper) {
        Locale locale = this.properties.getLocale();
        if (locale != null) {
            objectMapper.setLocale(locale);
        }
    }
}

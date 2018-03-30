package com.github.beihaifeiwu.sakura.template.velocity;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Created by liupin on 2017/8/31.
 */
@Slf4j
@Configuration
@ConditionalOnClass({VelocityEngine.class, VelocityEngineFactory.class})
@EnableConfigurationProperties(VelocityProperties.class)
public class VelocityConfiguration {

    private final ApplicationContext applicationContext;
    private final VelocityProperties properties;

    @Autowired
    public VelocityConfiguration(ApplicationContext applicationContext,
                                 VelocityProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @PostConstruct
    public void checkTemplateLocationExists() {
        if (this.properties.isCheckTemplateLocation()) {
            TemplateLocation location = new TemplateLocation(this.properties.getResourceLoaderPath());
            if (!location.exists(this.applicationContext)) {
                log.warn("Cannot find template location: " + location
                        + " (please add some templates, check your Velocity "
                        + "configuration, or set spring.velocity."
                        + "checkTemplateLocation=false)");
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public VelocityEngineFactoryBean velocityConfiguration() {
        VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
        applyProperties(velocityEngineFactoryBean);
        return velocityEngineFactoryBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public VelocityTemplateRender velocityTemplateRender(VelocityEngine velocityEngine) {
        return new VelocityTemplateRender(velocityEngine, properties);
    }

    private void applyProperties(VelocityEngineFactory factory) {
        factory.setResourceLoaderPath(this.properties.getResourceLoaderPath());
        factory.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty("input.encoding", this.properties.getCharsetName());
        velocityProperties.putAll(this.properties.getProperties());
        factory.setVelocityProperties(velocityProperties);
    }

}

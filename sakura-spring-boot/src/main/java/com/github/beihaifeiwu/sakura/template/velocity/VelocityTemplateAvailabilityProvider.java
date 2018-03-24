package com.github.beihaifeiwu.sakura.template.velocity;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/**
 * Created by liupin on 2017/8/31.
 */
public class VelocityTemplateAvailabilityProvider implements TemplateAvailabilityProvider {

    @Override
    public boolean isTemplateAvailable(String view, Environment environment,
                                       ClassLoader classLoader, ResourceLoader resourceLoader) {
        if (ClassUtils.isPresent("org.apache.velocity.app.VelocityEngine", classLoader)) {
            String loaderPath = environment.getProperty("spring.velocity.resource-loader-path",
                    VelocityProperties.DEFAULT_RESOURCE_LOADER_PATH);
            String prefix = environment.getProperty("spring.velocity.prefix",
                    VelocityProperties.DEFAULT_PREFIX);
            String suffix = environment.getProperty("spring.velocity.suffix",
                    VelocityProperties.DEFAULT_SUFFIX);
            return resourceLoader.getResource(loaderPath + prefix + view + suffix).exists();
        }
        return false;
    }

}

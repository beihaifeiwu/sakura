package com.github.beihaifeiwu.sakura.template.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

/**
 * Velocity ResourceLoader adapter that loads via a Spring ResourceLoader.
 * Used by VelocityEngineFactory for any resource loader path that cannot
 * be resolved to a {@code java.io.File}.
 * <p>
 * <p>Note that this loader does not allow for modification detection:
 * Use Velocity's default FileResourceLoader for {@code java.io.File}
 * resources.
 * <p>
 * <p>Expects "spring.resource.loader" and "spring.resource.loader.path"
 * application attributes in the Velocity runtime: the former of type
 * {@code org.springframework.core.io.ResourceLoader}, the latter a String.
 *
 * @author Juergen Hoeller
 * @see VelocityEngineFactory#setResourceLoaderPath
 * @see org.springframework.core.io.ResourceLoader
 * @see org.apache.velocity.runtime.resource.loader.FileResourceLoader
 * @since 14.03.2004
 */
public class SpringResourceLoader extends ResourceLoader {

    public static final String NAME = "spring";

    public static final String SPRING_RESOURCE_LOADER_CLASS = "spring.resource.loader.class";

    public static final String SPRING_RESOURCE_LOADER_CACHE = "spring.resource.loader.cache";

    public static final String SPRING_RESOURCE_LOADER = "spring.resource.loader";

    public static final String SPRING_RESOURCE_LOADER_PATH = "spring.resource.loader.path";


    protected final Log logger = LogFactory.getLog(getClass());

    private org.springframework.core.io.ResourceLoader resourceLoader;

    private String[] resourceLoaderPaths;


    @Override
    public void init(ExtProperties configuration) {
        this.resourceLoader = (org.springframework.core.io.ResourceLoader)
                this.rsvc.getApplicationAttribute(SPRING_RESOURCE_LOADER);
        String resourceLoaderPath = (String) this.rsvc.getApplicationAttribute(SPRING_RESOURCE_LOADER_PATH);
        Assert.notNull(this.resourceLoader,
                "'resourceLoader' application attribute must be present for SpringResourceLoader");
        Assert.notNull(resourceLoaderPath, "'resourceLoaderPath' application attribute must be present for SpringResourceLoader");

        this.resourceLoaderPaths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);
        for (int i = 0; i < this.resourceLoaderPaths.length; i++) {
            String path = this.resourceLoaderPaths[i];
            if (!path.endsWith("/")) {
                this.resourceLoaderPaths[i] = path + "/";
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("SpringResourceLoader for Velocity: using resource loader [" + this.resourceLoader +
                    "] and resource loader paths " + Arrays.asList(this.resourceLoaderPaths));
        }
    }

    @Override
    public Reader getResourceReader(String source, String encoding) throws ResourceNotFoundException {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for Velocity resource with name [" + source + "]");
        }
        for (String resourceLoaderPath : this.resourceLoaderPaths) {
            org.springframework.core.io.Resource resource =
                    this.resourceLoader.getResource(resourceLoaderPath + source);
            try {
                return new InputStreamReader(resource.getInputStream(), encoding);
            } catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not find Velocity resource: " + resource);
                }
            }
        }
        throw new ResourceNotFoundException(
                "Could not find resource [" + source + "] in Spring resource loader path");
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return false;
    }

    @Override
    public long getLastModified(Resource resource) {
        return 0;
    }

}
package sakura.spring.template.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.web.context.ServletContextAware;
import sakura.spring.core.SakuraConstants;

import javax.servlet.ServletContext;
import java.io.IOException;

public class VelocityConfigurer extends VelocityEngineFactory
        implements VelocityConfig, InitializingBean, ResourceLoaderAware, ServletContextAware {

    /**
     * the name of the resource loader for Spring's bind macros
     */
    private static final String SPRING_MACRO_RESOURCE_LOADER_NAME = "springMacro";

    /**
     * the key for the class of Spring's bind macro resource loader
     */
    private static final String SPRING_MACRO_RESOURCE_LOADER_CLASS = "springMacro.resource.loader.class";

    /**
     * the name of Spring's default bind macro library
     */
    private static final String SPRING_MACRO_LIBRARY = SakuraConstants.SPRING_VELOCITY_MACROS;


    private VelocityEngine velocityEngine;

    private ServletContext servletContext;


    /**
     * Set a pre-configured VelocityEngine to use for the Velocity web
     * configuration: e.g. a shared one for web and email usage, set up via
     * {@link VelocityEngineFactoryBean}.
     * <p>Note that the Spring macros will <i>not</i> be enabled automatically in
     * case of an external VelocityEngine passed in here. Make sure to include
     * {@code spring.vm} in your template loader path in such a scenario
     * (if there is an actual need to use those macros).
     * <p>If this is not set, VelocityEngineFactory's properties
     * (inherited by this class) have to be specified.
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Initialize VelocityEngineFactory's VelocityEngine
     * if not overridden by a pre-configured VelocityEngine.
     *
     * @see #createVelocityEngine
     * @see #setVelocityEngine
     */
    @Override
    public void afterPropertiesSet() throws IOException, VelocityException {
        if (this.velocityEngine == null) {
            this.velocityEngine = createVelocityEngine();
        }
    }

    /**
     * Provides a ClasspathResourceLoader in addition to any default or user-defined
     * loader in order to load the spring Velocity macros from the class path.
     *
     * @see org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
     */
    @Override
    protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {
        velocityEngine.setApplicationAttribute(ServletContext.class.getName(), this.servletContext);
        velocityEngine.setProperty(
                SPRING_MACRO_RESOURCE_LOADER_CLASS, ClasspathResourceLoader.class.getName());
        velocityEngine.addProperty(
                VelocityEngine.RESOURCE_LOADER, SPRING_MACRO_RESOURCE_LOADER_NAME);
        velocityEngine.addProperty(
                VelocityEngine.VM_LIBRARY, SPRING_MACRO_LIBRARY);

        if (logger.isInfoEnabled()) {
            logger.info("ClasspathResourceLoader with name '"
                    + SPRING_MACRO_RESOURCE_LOADER_NAME
                    + "' added to configured VelocityEngine");
        }
    }

    @Override
    public VelocityEngine getVelocityEngine() {
        return this.velocityEngine;
    }

}
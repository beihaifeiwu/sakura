package com.github.beihaifeiwu.sakura.template.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;

import java.io.IOException;

/**
 * Factory bean that configures a VelocityEngine and provides it as bean
 * reference. This bean is intended for any kind of usage of Velocity in
 * application code, e.g. for generating email content. For web views,
 * VelocityConfigurer is used to set up a VelocityEngine for views.
 * <p>
 * <p>The simplest way to use this class is to specify a "resourceLoaderPath";
 * you do not need any further configuration then. For example, in a web
 * application context:
 * <p>
 * <pre class="code"> &lt;bean id="velocityEngine" class="com.github.beihaifeiwu.sakura.template.velocity.VelocityEngineFactoryBean"&gt;
 * &lt;property name="resourceLoaderPath" value="/WEB-INF/velocity/"/&gt;
 * &lt;/bean&gt;</pre>
 * <p>
 * See the base class VelocityEngineFactory for configuration details.
 *
 * @author Juergen Hoeller
 * @see #setConfigLocation
 * @see #setVelocityProperties
 * @see #setResourceLoaderPath
 * @see VelocityConfigurer
 */
public class VelocityEngineFactoryBean extends VelocityEngineFactory
        implements FactoryBean<VelocityEngine>, InitializingBean, ResourceLoaderAware {

    private VelocityEngine velocityEngine;


    @Override
    public void afterPropertiesSet() throws IOException, VelocityException {
        this.velocityEngine = createVelocityEngine();
    }


    @Override
    public VelocityEngine getObject() {
        return this.velocityEngine;
    }

    @Override
    public Class<? extends VelocityEngine> getObjectType() {
        return VelocityEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
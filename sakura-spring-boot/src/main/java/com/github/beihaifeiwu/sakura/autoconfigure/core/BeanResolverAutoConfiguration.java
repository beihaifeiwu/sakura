package com.github.beihaifeiwu.sakura.autoconfigure.core;

import com.github.beihaifeiwu.sakura.core.AbstractBeanPostProcessorsRegistrar;
import com.github.beihaifeiwu.sakura.core.SakuraConstants;
import com.github.beihaifeiwu.sakura.core.SpringStuffCollector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.expression.BeanResolver;

/**
 * Created by liupin on 2017/2/6.
 */
@Configuration
@PropertySource(value = { SakuraConstants.DEFAULT_SPRING_CONFIG_SOURCE }, ignoreResourceNotFound = true)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(BeanResolverAutoConfiguration.BeanPostProcessorsRegistrar.class)
public class BeanResolverAutoConfiguration
        implements ApplicationContextAware, EnvironmentAware, ApplicationEventPublisherAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringStuffCollector.setBeanFactory(applicationContext);
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringStuffCollector.setEnvironment(environment);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        SpringStuffCollector.setEventPublisher(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanResolver beanResolver() {
        return new BeanFactoryResolver(applicationContext.getAutowireCapableBeanFactory());
    }

    public static class BeanPostProcessorsRegistrar
            extends AbstractBeanPostProcessorsRegistrar implements ResourceLoaderAware {

        @Override
        protected void doRegister(AnnotationMetadata annotationMetadata,
                                  BeanDefinitionRegistry registry) {
            SpringStuffCollector.setBeanFactory(beanFactory);
            SpringStuffCollector.setEnvironment(environment);
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            ResourcePatternResolver resourcePatternResolver =
                    ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
            SpringStuffCollector.setResourcePatternResolver(resourcePatternResolver);
        }

    }
}

package sakura.spring.autoconfigure;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;
import sakura.spring.core.AbstractBeanRegistrar;
import sakura.spring.core.SpringCacheAnnotationProcessor;

/**
 * Created by liupin on 2017/6/19.
 */
@Configuration
@ConditionalOnClass(CacheManager.class)
@EnableCaching(proxyTargetClass = true)
@AutoConfigureBefore(CacheAutoConfiguration.class)
@Import(CacheExtAutoConfiguration.SpringCacheBeanRegistrar.class)
public class CacheExtAutoConfiguration {

    static class SpringCacheBeanRegistrar extends AbstractBeanRegistrar {

        @Override
        protected void doRegister(AnnotationMetadata annotationMetadata,
                                  BeanDefinitionRegistry registry) {
            Class<?> beanClass = SpringCacheAnnotationProcessor.class;
            if (isRegistered(beanClass)) {
                return;
            }
            String beanName = defaultBeanName(beanClass);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
            beanDefinition.setSynthetic(true);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }

    }

}

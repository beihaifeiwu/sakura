package sakura.spring.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.beans.Introspector;

/**
 * Created by liupin on 2017/6/10.
 */
public abstract class AbstractBeanRegistrar
        implements ImportBeanDefinitionRegistrar, BeanFactoryAware, EnvironmentAware {

    protected ConfigurableListableBeanFactory beanFactory;
    protected Environment environment;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
                                        BeanDefinitionRegistry registry) {
        if (this.beanFactory == null) {
            return;
        }

        doRegister(annotationMetadata, registry);
    }

    protected <T> boolean isRegistered(Class<T> beanClass) {
        String[] beanNames = this.beanFactory.getBeanNamesForType(beanClass, true, false);
        return !ObjectUtils.isEmpty(beanNames);
    }

    protected String defaultBeanName(Class<?> beanClass) {
        String shortName = ClassUtils.getShortName(beanClass);
        return Introspector.decapitalize(shortName);
    }

    protected abstract void doRegister(AnnotationMetadata annotationMetadata,
                                       BeanDefinitionRegistry registry);
}

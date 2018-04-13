package sakura.spring.core;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Created by liupin on 2017/2/6.
 */
public class SpringStuffCollector {

    private static volatile ListableBeanFactory beanFactory;

    private static volatile Environment environment;

    private static volatile ApplicationEventPublisher eventPublisher;

    private static volatile ResourcePatternResolver resourcePatternResolver;

    public static ListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public static void setBeanFactory(ListableBeanFactory beanFactory) {
        SpringStuffCollector.beanFactory = beanFactory;
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        SpringStuffCollector.environment = environment;
    }

    public static ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public static void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        SpringStuffCollector.eventPublisher = eventPublisher;
    }

    public static ResourcePatternResolver getResourcePatternResolver() {
        return resourcePatternResolver;
    }

    public static void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        SpringStuffCollector.resourcePatternResolver = resourcePatternResolver;
    }

}

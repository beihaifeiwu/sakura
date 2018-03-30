package com.github.beihaifeiwu.sakura.core;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class AbstractAnnotationBeanPostProcessor implements BeanPostProcessor {

    public enum Members {
        FIELDS,
        METHODS,
        ALL
    }

    public enum Phase {
        PRE_INIT,
        POST_INIT
    }

    private final Members members;
    private final Phase phase;
    private final AnnotationFilter filter;

    public AbstractAnnotationBeanPostProcessor(final Members members, final Phase phase, final AnnotationFilter filter) {
        this.members = members;
        this.phase = phase;
        this.filter = filter;
    }

    protected void withField(Object bean, String beanName, Class<?> targetClass, Field field) {

    }


    protected void withMethod(Object bean, String beanName, Class<?> targetClass, Method method) {

    }

    @Override
    public final Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (phase == Phase.PRE_INIT) {
            process(bean, beanName);
        }

        return bean;
    }

    @Override
    public final Object postProcessAfterInitialization(Object bean, String beanName) {
        if (phase == Phase.POST_INIT) {
            process(bean, beanName);
        }

        return bean;
    }

    private void process(final Object bean, final String beanName) {
        final Class<?> targetClass = AopUtils.getTargetClass(bean);

        if (members == Members.FIELDS || members == Members.ALL) {
            ReflectionUtils.doWithFields(targetClass, field -> withField(bean, beanName, targetClass, field), filter);
        }

        if (members == Members.METHODS || members == Members.ALL) {
            ReflectionUtils.doWithMethods(targetClass, method -> withMethod(bean, beanName, targetClass, method), filter);
        }
    }

}
package sakura.spring.core;

import sakura.common.lang.Lazy;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by liupin on 2017/2/6.
 */
@UtilityClass
public class SpringBeans {

    private static ListableBeanFactory getBeanFactory() {
        ListableBeanFactory beanFactory = SpringStuffCollector.getBeanFactory();
        if (beanFactory instanceof GenericApplicationContext) {
            beanFactory = ((GenericApplicationContext) beanFactory).getBeanFactory();
        }
        return beanFactory;
    }

    public static Optional<Object> getBean(String name) {
        try {
            Object bean = getBeanFactory().getBean(name);
            return Optional.of(bean);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getBean(String name, Class<T> requiredType) {
        try {
            T bean = getBeanFactory().getBean(name, requiredType);
            return Optional.of(bean);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getBean(Class<T> requiredType) {
        try {
            T bean = getBeanFactory().getBean(requiredType);
            return Optional.of(bean);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static Optional<Object> getBean(String name, Object... args) {
        try {
            Object bean = getBeanFactory().getBean(name, args);
            return Optional.of(bean);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getBean(Class<T> requiredType, Object... args) {
        try {
            T bean = getBeanFactory().getBean(requiredType, args);
            return Optional.of(bean);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getBean(Class<T> requiredType, String qualifier) {
        try {
            T bean = BeanFactoryAnnotationUtils.qualifiedBeanOfType(getBeanFactory(), requiredType, qualifier);
            return Optional.of(bean);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static <T> List<T> getBeans(Class<T> type) {
        try {
            Collection<T> values = BeanFactoryUtils.beansOfTypeIncludingAncestors(getBeanFactory(), type).values();
            return Lists.newArrayList(values);
        } catch (Throwable t) {
            return Collections.emptyList();
        }
    }

    @Nullable
    public static BeanDefinition getBeanDefinition(String beanName) {
        ListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            return ((ConfigurableListableBeanFactory) beanFactory).getBeanDefinition(beanName);
        }
        return null;
    }

    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isSingleton(name);
    }

    public static boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isPrototype(name);
    }

    public static boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    public static boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Nullable
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getType(name);
    }

    public static String[] getAliases(String name) {
        return getBeanFactory().getAliases(name);
    }

    public static <T> String[] getBeanNames(Class<T> beanType) {
        return getBeanFactory().getBeanNamesForType(beanType);
    }

    public static <T, A extends Annotation> List<String> getBeanNames(Class<T> beanType, Class<A> annotationType) {
        String[] names = getBeanFactory().getBeanNamesForType(beanType);
        if (ObjectUtils.isEmpty(names)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String name : names) {
            BeanDefinition beanDefinition = getBeanDefinition(name);
            if (beanDefinition != null && beanDefinition.getSource() != null
                    && beanDefinition.getSource() instanceof StandardMethodMetadata) {
                StandardMethodMetadata metadata = (StandardMethodMetadata) beanDefinition.getSource();
                if (metadata.isAnnotated(annotationType.getName())) {
                    result.add(name);
                    continue;
                }
            }
            if (null != findAnnotationOnBean(name, annotationType)) {
                result.add(name);
            }
        }
        return result;
    }

    @Nullable
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
    }

    public static <T> List<T> getBeans(ObjectProvider<List<T>> provider) {
        List<T> beans = provider.getIfAvailable();
        if (!CollectionUtils.isEmpty(beans)) {
            beans = new ArrayList<>(beans);
            AnnotationAwareOrderComparator.sort(beans);
            return beans;
        }
        return Collections.emptyList();
    }

    public static <T> void ifPresent(ObjectProvider<T> provider, Consumer<T> consumer) {
        T bean = provider.getIfAvailable();
        if (!ObjectUtils.isEmpty(bean)) {
            consumer.accept(bean);
        }
    }

    public static <T> Lazy<T> getLazyWrappedBean(Class<T> type) {
        return Lazy.of(() -> getBeanFactory().getBean(type));
    }

}

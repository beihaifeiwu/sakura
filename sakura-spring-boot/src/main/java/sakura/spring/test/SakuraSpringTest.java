package sakura.spring.test;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sakura.spring.core.AbstractBeanRegistrar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by haomu on 2018/4/13.
 */
@RunWith(SakuraSpringTest.SakuraSpringRunner.class)
@ContextConfiguration(classes = SakuraSpringTest.TestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@BootstrapWith(SakuraSpringTest.SakuraSpringBootstrapper.class)
public abstract class SakuraSpringTest {

    private static Class<?> testClass;

    @Configuration
    @Import({TestBeanRegistrar.class, TestAutoSelector.class})
    public static class TestConfiguration {

    }

    public static class TestBeanRegistrar extends AbstractBeanRegistrar {

        @Override
        protected void doRegister(AnnotationMetadata annotationMetadata,
                                  BeanDefinitionRegistry registry) {
            Set<Class<?>> classes = getTestBeanClass();
            for (Class<?> beanClass : classes) {
                if (isRegistered(beanClass)) continue;
                TestBean annotation = beanClass.getAnnotation(TestBean.class);
                String beanName = annotation.value();
                beanName = StringUtils.isBlank(beanName) ? defaultBeanName(beanClass) : beanName;
                RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
                beanDefinition.setSynthetic(true);
                beanDefinition.setScope(annotation.scope());
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }

        private Set<Class<?>> getTestBeanClass() {
            Set<Class<?>> beanClasses = new HashSet<>();
            Class<?> current = testClass;
            while (current != null && !current.isInterface()) {
                for (Class<?> clazz : current.getDeclaredClasses()) {
                    if (!clazz.isInterface() && clazz.isAnnotationPresent(TestBean.class)) {
                        beanClasses.add(clazz);
                    }
                }
                current = current.getSuperclass();
            }
            return beanClasses;
        }

    }

    public static class TestAutoSelector extends AutoConfigurationImportSelector {

        TestAutoConfig config = testClass.getAnnotation(TestAutoConfig.class);

        @Override
        protected List<String> getCandidateConfigurations(AnnotationMetadata metadata,
                                                          AnnotationAttributes attributes) {
            List<String> configurations = super.getCandidateConfigurations(metadata, attributes);

            if (isConfigAvailable()) {
                configurations.removeIf(this::isExcluded);
            }

            return configurations;
        }

        @Override
        protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
            AnnotationAttributes attributes = new AnnotationAttributes();
            attributes.put("exclude", new String[0]);
            attributes.put("excludeName", new String[0]);
            return attributes;
        }

        private boolean isConfigAvailable() {
            return config != null && (config.include().length > 0 || config.exclude().length > 0);
        }

        private boolean isExcluded(String source) {
            String name = source;
            int index = name.lastIndexOf(".");
            if (index != -1) {
                name = name.substring(index + 1);
            }
            if (config.include().length > 0 && StringUtils.startsWithAny(name, config.include())) {
                return false;
            }
            return config.exclude().length > 0 && StringUtils.startsWithAny(name, config.exclude());
        }

    }

    public static class SakuraSpringBootstrapper extends SpringBootTestContextBootstrapper {

    }

    public static class SakuraSpringRunner extends SpringJUnit4ClassRunner {

        public SakuraSpringRunner(Class<?> clazz) throws InitializationError, IllegalAccessException {
            super(clazz);

            if (SakuraSpringTest.class.isAssignableFrom(clazz)) {
                FieldUtils.writeStaticField(clazz, "testClass", clazz, true);
            }

        }

    }

}

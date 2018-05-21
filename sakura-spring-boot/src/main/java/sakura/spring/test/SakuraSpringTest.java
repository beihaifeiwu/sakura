package sakura.spring.test;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ClassUtils;
import sakura.common.lang.TIME;
import sakura.spring.core.AbstractBeanRegistrar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by haomu on 2018/4/13.
 */
@RunWith(SakuraSpringTest.TestRunner.class)
@ContextConfiguration(classes = SakuraSpringTest.TestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class SakuraSpringTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public final Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void succeeded(long nanos, Description description) {
            logInfo(description, "succeeded", nanos);
        }

        @Override
        protected void failed(long nanos, Throwable e, Description description) {
            logInfo(description, "failed", nanos);
        }

        @Override
        protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
            logInfo(description, "skipped", nanos);
        }
    };

    private static void logInfo(Description description, String status, long nanos) {
        String testName = description.getMethodName();
        System.out.println(String.format("Test %s %s, spent %s", testName, status, TIME.humanReadable(nanos)));
    }

    private static Class<?> testClass;

    @Configuration
    @Import({TestRegistrar.class, TestSelector.class})
    public static class TestConfiguration {

    }

    public static class TestRegistrar extends AbstractBeanRegistrar {

        @Override
        protected void doRegister(AnnotationMetadata annotationMetadata,
                                  BeanDefinitionRegistry registry) {
            registerBasePackages(registry);
            registerBeans(registry);
        }

        private void registerBasePackages(BeanDefinitionRegistry registry) {
            String packageName = ClassUtils.getPackageName(testClass);
            AutoConfigurationPackages.register(registry, packageName);
        }

        private void registerBeans(BeanDefinitionRegistry registry) {
            Set<Class<?>> classes = getTestBeanClass();
            for (Class<?> beanClass : classes) {
                if (isRegistered(beanClass)) continue;
                val annotation = beanClass.getAnnotation(TestBean.class);
                val beanName = getBeanName(beanClass, annotation.value());
                val beanDefinition = getBeanDefinition(beanClass);
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

    public static class TestSelector extends AutoConfigurationImportSelector {

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
            val attributes = new AnnotationAttributes();
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

    public static class TestRunner extends SpringJUnit4ClassRunner {

        public TestRunner(Class<?> clazz) throws InitializationError {
            super(clazz);

            if (SakuraSpringTest.class.isAssignableFrom(clazz)) {
                SakuraSpringTest.testClass = clazz;
            }

        }

    }

}

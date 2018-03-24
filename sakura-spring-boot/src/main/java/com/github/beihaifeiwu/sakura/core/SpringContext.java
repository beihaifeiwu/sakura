package com.github.beihaifeiwu.sakura.core;

import com.github.beihaifeiwu.sakura.common.lang.Lazy;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.Validator;

import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by liupin on 2017/6/12.
 */
@UtilityClass
public class SpringContext {

    /********************  lazy load constant properties  ***********************/

    private static final Lazy<Boolean> IS_SSL_ENABLED = Lazy.of(() -> {
        Boolean isEnabled = SpringBeans.getBean(AbstractReactiveWebServerFactory.class)
                .map(AbstractConfigurableWebServerFactory::getSsl)
                .map(Ssl::isEnabled)
                .orElse(false);
        if (!isEnabled) {
            isEnabled = SpringBeans.getBean(AbstractServletWebServerFactory.class)
                    .map(AbstractConfigurableWebServerFactory::getSsl)
                    .map(Ssl::isEnabled)
                    .orElse(false);
        }
        return isEnabled;
    });

    private static final Lazy<FormattingConversionService> FORMATTING_CONVERSION_SERVICE =
            SpringBeans.getLazyWrappedBean(FormattingConversionService.class);

    private static final Lazy<ValidatorFactory> VALIDATOR_FACTORY =
            SpringBeans.getLazyWrappedBean(ValidatorFactory.class);

    private static final Lazy<ThreadPoolTaskExecutor> TASK_EXECUTOR =
            SpringBeans.getLazyWrappedBean(ThreadPoolTaskExecutor.class);

    private static final Lazy<ThreadPoolTaskScheduler> TASK_SCHEDULER =
            SpringBeans.getLazyWrappedBean(ThreadPoolTaskScheduler.class);

    /*********************  public access methods  *********************/

    public static Integer serverPort() {
        return SpringEnvironment.getProperty("local.server.port", Integer.class, 8080);
    }

    public static String servletContextPath() {
        return SpringEnvironment.getProperty("server.servlet.context-path", "");
    }

    public static boolean isSslEnabled() {
        return IS_SSL_ENABLED.get();
    }

    public static String baseUrl() {
        return (isSslEnabled() ? "https" : "http") + "://localhost:" + serverPort() + servletContextPath();
    }

    public static String[] activeProfiles() {
        Environment env = SpringEnvironment.getEnvironment();
        String[] profiles = env.getActiveProfiles();
        if (profiles.length == 0) {
            return env.getDefaultProfiles();
        }
        return profiles;
    }

    public static List<String> basePackages() {
        return AutoConfigurationPackages.get(SpringStuffCollector.getBeanFactory());
    }

    public static String resolvePlaceHolder(String strVal) {
        return SpringStuffCollector.getEnvironment().resolvePlaceholders(strVal);
    }

    public static FormattingConversionService conversionService() {
        return FORMATTING_CONVERSION_SERVICE.get();
    }

    public static ValidatorFactory validatorFactory() {
        return VALIDATOR_FACTORY.get();
    }

    public static Validator springValidator() {
        ValidatorFactory validatorFactory = validatorFactory();
        Assert.isInstanceOf(Validator.class, validatorFactory);
        return (Validator) validatorFactory;
    }

    public static Resource getResource(String location) {
        return SpringStuffCollector.getResourcePatternResolver().getResource(location);
    }

    @SneakyThrows
    public static Resource[] getResources(String locationPattern) {
        return SpringStuffCollector.getResourcePatternResolver().getResources(locationPattern);
    }

    public static void publishEvent(Object event) {
        SpringStuffCollector.getEventPublisher().publishEvent(event);
    }

    public static ThreadPoolTaskExecutor executor() {
        return TASK_EXECUTOR.get();
    }

    public static ThreadPoolTaskScheduler scheduler() {
        return TASK_SCHEDULER.get();
    }

    public static void execute(Runnable task) {
        executor().execute(task);
    }

    public static ListenableFuture<?> submitListenable(Runnable task) {
        return executor().submitListenable(task);
    }

    public static <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return executor().submitListenable(task);
    }

    @Nullable
    public static ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return scheduler().schedule(task, trigger);
    }

    public static ScheduledFuture<?> scheduleOnce(Runnable task, long delay) {
        return scheduler().schedule(task, new Date(System.currentTimeMillis() + delay));
    }

    public static ScheduledFuture<?> scheduleOnce(Runnable task, Date startTime) {
        return scheduler().schedule(task, startTime);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return scheduler().scheduleAtFixedRate(task, startTime, period);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return scheduler().scheduleAtFixedRate(task, period);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return scheduler().scheduleWithFixedDelay(task, startTime, delay);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return scheduler().scheduleWithFixedDelay(task, delay);
    }
}

package sakura.spring.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import sakura.common.lang.Threads;
import sakura.spring.core.SakuraConstants;

import java.util.concurrent.Executor;

/**
 * Created by liupin on 2017/2/16.
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableAsync(proxyTargetClass = true)
@EnableRetry(proxyTargetClass = true)
@EnableScheduling
public class NiceFeatureAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ThreadPoolTaskExecutor.class)
    @ConfigurationProperties(prefix = SakuraConstants.EXECUTOR_PROP_PREFIX)
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(Threads.processors() * 2);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(1024 * 4);
        executor.setThreadNamePrefix("executor-");
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean(AsyncConfigurer.class)
    public AsyncConfigurer asyncConfigurer(ThreadPoolTaskExecutor executor) {
        return new AsyncConfigurerImpl(executor);
    }

    @Bean
    @ConditionalOnMissingBean(ThreadPoolTaskScheduler.class)
    @ConfigurationProperties(prefix = SakuraConstants.SCHEDULER_PROP_PREFIX)
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setDaemon(true);
        scheduler.setPoolSize(1);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setThreadNamePrefix("scheduler-");
        return scheduler;
    }

    @Bean
    @ConditionalOnMissingBean(SchedulingConfigurer.class)
    public SchedulingConfigurer schedulingConfigurer(ThreadPoolTaskScheduler scheduler) {
        return taskRegistrar -> taskRegistrar.setTaskScheduler(scheduler);
    }

    static class AsyncConfigurerImpl implements AsyncConfigurer {
        private final ThreadPoolTaskExecutor executor;

        AsyncConfigurerImpl(ThreadPoolTaskExecutor executor) {
            this.executor = executor;
        }

        @Override
        public Executor getAsyncExecutor() {
            return executor;
        }
    }



}

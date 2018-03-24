package com.github.beihaifeiwu.sakura.core;

import com.github.beihaifeiwu.sakura.SakuraTestPlainConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by liupin on 2017/2/6.
 */
@SakuraTestPlainConfiguration
public class CoreTestApplication {

    @Bean
    public AsyncFeatureTest.AsyncTestDemo asyncTestDemo() {
        return new AsyncFeatureTest.AsyncTestDemo();
    }

    @Bean
    public JCacheFeatureTest.JCacheTestDemo jCacheTestDemo() {
        return new JCacheFeatureTest.JCacheTestDemo();
    }

    @Bean
    public SpringContextTest.EventListenerDemo eventListenerDemo() {
        return new SpringContextTest.EventListenerDemo();
    }

    public static void main(String[] args) {
        SpringApplication.run(CoreTestApplication.class, args);
    }
}

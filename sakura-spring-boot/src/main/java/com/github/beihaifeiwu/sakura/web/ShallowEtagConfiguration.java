package com.github.beihaifeiwu.sakura.web;

import com.github.beihaifeiwu.sakura.web.filter.ShallowEtagHeaderExtFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/**
 * Created by liupin on 2017/6/13.
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "sakura.web.shallow-etag", name = "enabled")
public class ShallowEtagConfiguration {

    private final WebProperties webProperties;

    public ShallowEtagConfiguration(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        WebProperties.ShallowEtag shallowEtag = webProperties.getShallowEtag();
        ShallowEtagHeaderExtFilter filter = new ShallowEtagHeaderExtFilter();
        filter.setWriteWeakETag(shallowEtag.isWriteWeakEtag());
        filter.setIncludePaths(shallowEtag.getIncludePaths());
        filter.setExcludePaths(shallowEtag.getExcludePaths());
        FilterRegistrationBean<ShallowEtagHeaderFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(Ordered.LOWEST_PRECEDENCE - 1000);
        bean.setAsyncSupported(true);
        return bean;
    }
}

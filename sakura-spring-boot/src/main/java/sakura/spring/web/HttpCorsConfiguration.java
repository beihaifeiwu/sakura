package sakura.spring.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import sakura.spring.web.filter.HttpOptionMethodFilter;

@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "sakura.web.cors", name = "enabled", matchIfMissing = true)
public class HttpCorsConfiguration {

    private final WebProperties webProperties;

    public HttpCorsConfiguration(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        WebProperties.Cors cors = webProperties.getCors();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(cors.getAllowCredentials());
        config.setAllowedOrigins(cors.getAllowedOrigins());
        config.setAllowedHeaders(cors.getAllowedHeaders());
        config.setAllowedMethods(cors.getAllowedMethods());
        config.setExposedHeaders(cors.getExposedHeaders());
        config.setMaxAge(cors.getMaxAge());
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 249);
        bean.setAsyncSupported(true);
        return bean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.mvc", value = "dispatch-options-request", havingValue = "false")
    public FilterRegistrationBean httpOptionMethodFilter() {
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new HttpOptionMethodFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 250);
        bean.setAsyncSupported(true);
        return bean;
    }
}
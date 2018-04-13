package sakura.spring.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import sakura.spring.web.HttpCorsConfiguration;
import sakura.spring.web.ShallowEtagConfiguration;
import sakura.spring.web.WebProperties;
import sakura.spring.web.exhandler.RestHandlerExceptionResolver;
import sakura.spring.web.exhandler.RestHandlerExceptionResolverBuilder;
import sakura.spring.web.param.RenamingModelAttributeMethodProcessor;

import java.util.List;

/**
 * Created by liupin on 2017/6/9.
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@AutoConfigureAfter({NiceFeatureAutoConfiguration.class, JacksonExtAutoConfiguration.class})
@Import({HttpCorsConfiguration.class, ShallowEtagConfiguration.class})
@EnableConfigurationProperties(WebProperties.class)
public class WebServletAutoConfiguration {

    private final HttpMessageConverters messageConverters;

    public WebServletAutoConfiguration(@Lazy HttpMessageConverters messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestHandlerExceptionResolverBuilder restExceptionResolverBuilder(ServerProperties serverProperties) {
        return RestHandlerExceptionResolver.builder()
                .defaultContentType(MediaType.APPLICATION_JSON)
                .errorProperties(serverProperties.getError())
                .httpMessageConverters(messageConverters.getConverters());
    }

    @Bean
    @ConditionalOnMissingBean
    public RestHandlerExceptionResolver restExceptionResolver(
            RestHandlerExceptionResolverBuilder builder) {
        return builder.build();
    }

    @Bean
    public RenamingModelAttributeMethodProcessor renamingModelAttributeProcessor() {
        return new RenamingModelAttributeMethodProcessor(true);
    }

    @Configuration
    @ConditionalOnClass({DispatcherServlet.class, HttpRequestHandler.class})
    static class WebMvcConfiguration implements WebMvcConfigurer {

        private final ThreadPoolTaskExecutor executor;
        private final RestHandlerExceptionResolver restHandlerExceptionResolver;
        private final RenamingModelAttributeMethodProcessor renamingModelAttributeMethodProcessor;

        WebMvcConfiguration(ThreadPoolTaskExecutor executor,
                            RestHandlerExceptionResolver restHandlerExceptionResolver,
                            RenamingModelAttributeMethodProcessor renamingModelAttributeMethodProcessor) {
            this.executor = executor;
            this.restHandlerExceptionResolver = restHandlerExceptionResolver;
            this.renamingModelAttributeMethodProcessor = renamingModelAttributeMethodProcessor;
        }

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setTaskExecutor(executor);
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(renamingModelAttributeMethodProcessor);
        }

        @Override
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
            resolvers.removeIf(resolver -> resolver instanceof DefaultHandlerExceptionResolver);
            resolvers.add(restHandlerExceptionResolver);
        }

    }
}

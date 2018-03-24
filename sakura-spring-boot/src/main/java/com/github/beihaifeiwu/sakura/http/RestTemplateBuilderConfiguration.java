package com.github.beihaifeiwu.sakura.http;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liupin on 2017/9/5.
 */
@Configuration
@ConditionalOnClass(RestTemplate.class)
public class RestTemplateBuilderConfiguration {

    private final ObjectProvider<HttpMessageConverters> messageConverters;
    private final ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers;
    private final ObjectProvider<ClientHttpRequestFactory> clientRequestFactory;

    @Autowired
    public RestTemplateBuilderConfiguration(ObjectProvider<HttpMessageConverters> messageConverters,
                                            ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers,
                                            ObjectProvider<ClientHttpRequestFactory> clientRequestFactory) {
        this.messageConverters = messageConverters;
        this.restTemplateCustomizers = restTemplateCustomizers;
        this.clientRequestFactory = clientRequestFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplateBuilder restTemplateBuilder() {
        RestTemplateBuilder builder = new RestTemplateBuilder();

        HttpMessageConverters converters = this.messageConverters.getIfUnique();
        if (converters != null) {
            builder = builder.messageConverters(converters.getConverters());
        }

        List<RestTemplateCustomizer> customizers = this.restTemplateCustomizers.getIfAvailable();
        if (!CollectionUtils.isEmpty(customizers)) {
            customizers = new ArrayList<>(customizers);
            AnnotationAwareOrderComparator.sort(customizers);
            builder = builder.customizers(customizers);
        }

        ClientHttpRequestFactory clientRequestFactory = this.clientRequestFactory.getIfAvailable();
        if (clientRequestFactory != null) {
            builder = builder.requestFactory(() -> clientRequestFactory);
        }

        return builder;
    }

}

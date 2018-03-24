package com.github.beihaifeiwu.sakura.http.okhttp;

import com.github.beihaifeiwu.sakura.core.SpringBeans;
import com.github.beihaifeiwu.sakura.common.http.HTTP;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liupin on 2017/9/7.
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttpClientConfiguration {

    private final ObjectProvider<List<OkHttpConfigurer>> configures;
    private final ObjectProvider<List<Interceptor>> interceptors;
    private final ObjectProvider<List<Interceptor>> networkInterceptors;

    private final OkHttpProperties properties;

    @Autowired
    public OkHttpClientConfiguration(OkHttpProperties properties,
                                     ObjectProvider<List<OkHttpConfigurer>> configures,
                                     @OkHttpInterceptor ObjectProvider<List<Interceptor>> interceptors,
                                     @OkHttpNetworkInterceptor ObjectProvider<List<Interceptor>> networkInterceptors) {
        this.properties = properties;
        this.configures = configures;
        this.interceptors = interceptors;
        this.networkInterceptors = networkInterceptors;
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(ObjectProvider<Cache> cache) throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(properties.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        builder.readTimeout(properties.getReadTimeout(), TimeUnit.MILLISECONDS);
        builder.writeTimeout(properties.getWriteTimeout(), TimeUnit.MILLISECONDS);
        builder.pingInterval(properties.getPingInterval(), TimeUnit.MILLISECONDS);

        builder.followRedirects(properties.isFollowRedirects());
        builder.followSslRedirects(properties.isFollowSslRedirects());
        builder.retryOnConnectionFailure(properties.isRetryOnConnectionFailure());

        if (properties.getProxy() != null) {
            SocketAddress address = new InetSocketAddress(
                    properties.getProxy().getHost(), properties.getProxy().getPort());
            builder.proxy(new Proxy(properties.getProxy().getType(), address));
        }

        Cache availableCache = cache.getIfAvailable();
        if (availableCache == null) {
            availableCache = createCacheIfPossible();
        }
        if (availableCache != null) {
            builder.cache(availableCache);
        }

        SpringBeans.getBean(CookieJar.class).ifPresent(builder::cookieJar);
        SpringBeans.getBean(Dns.class).ifPresent(builder::dns);

        SpringBeans.getBeans(interceptors).forEach(builder::addInterceptor);
        SpringBeans.getBeans(networkInterceptors).forEach(builder::addNetworkInterceptor);

        for (OkHttpConfigurer configurer : SpringBeans.getBeans(configures)) {
            configurer.configure(builder);
        }

        OkHttpClient okHttpClient = builder.build();
        HTTP.setOkHttpClient(okHttpClient);
        return okHttpClient;
    }

    @Nullable
    private Cache createCacheIfPossible() throws IOException {
        File cacheDir;
        String prefix = "okhttp3-cache";
        String directory = properties.getCache().getDirectory();
        switch (properties.getCache().getMode()) {
            case TEMPORARY:
                if (directory != null) {
                    cacheDir = Files.createTempDirectory(new File(directory).getAbsoluteFile().toPath(), prefix).toFile();
                } else {
                    cacheDir = Files.createTempDirectory(prefix).toFile();
                }
                cacheDir.deleteOnExit();
                break;
            case PERSISTENT:
                if (directory != null) {
                    cacheDir = new File(directory).getAbsoluteFile();
                } else {
                    cacheDir = new File(prefix).getAbsoluteFile();
                }
                break;
            case NONE:
            default:
                return null;
        }
        return new Cache(cacheDir, properties.getCache().getSize());
    }

    @Configuration
    @ConditionalOnClass(HttpLoggingInterceptor.class)
    public static class OkHttpLoggingConfiguration {

        @Bean
        @OkHttpInterceptor
        @ConditionalOnMissingBean(HttpLoggingInterceptor.class)
        public HttpLoggingInterceptor httpLoggingInterceptor(OkHttpProperties properties) {
            OkHttpProperties.Logging logging = properties.getLogging();
            Logger log = LoggerFactory.getLogger(logging.getLogName());
            HttpLoggingInterceptor interceptor =
                    new HttpLoggingInterceptor(m -> logMessage(log, logging.getLogLevel(), m));
            HttpLoggingInterceptor.Level level =
                    HttpLoggingInterceptor.Level.valueOf(logging.getLevel().name());
            interceptor.setLevel(level);
            return interceptor;
        }

        private void logMessage(Logger logger, Level level, String message) {
            switch (level) {
                case ERROR:
                    logger.error(message);
                    break;
                case WARN:
                    logger.warn(message);
                    break;
                case INFO:
                    logger.info(message);
                    break;
                case DEBUG:
                    logger.debug(message);
                    break;
                case TRACE:
                    logger.trace(message);
                    break;
                default:
                    break;
            }
        }
    }

    @Configuration
    @ConditionalOnClass({ OkHttp3ClientHttpRequestFactory.class })
    public static class OkHttpRestTemplateConfiguration {

        @Bean
        @Order(2)
        @ConditionalOnMissingBean(OkHttp3ClientHttpRequestFactory.class)
        public OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory(
                OkHttpClient okHttpClient) {
            return new OkHttp3ClientHttpRequestFactory(okHttpClient);
        }

    }

}

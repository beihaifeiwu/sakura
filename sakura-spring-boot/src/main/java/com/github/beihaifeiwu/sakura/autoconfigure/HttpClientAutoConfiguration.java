package com.github.beihaifeiwu.sakura.autoconfigure;

import com.github.beihaifeiwu.sakura.autoconfigure.core.JacksonExtAutoConfiguration;
import com.github.beihaifeiwu.sakura.http.RestTemplateBuilderConfiguration;
import com.github.beihaifeiwu.sakura.http.okhttp.OkHttpClientConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liupin on 2017/9/5.
 */
@Configuration
@Import({OkHttpClientConfiguration.class,
        RestTemplateBuilderConfiguration.class})
@AutoConfigureAfter({HttpMessageConvertersAutoConfiguration.class,
        JacksonExtAutoConfiguration.class})
@AutoConfigureBefore({RestTemplateAutoConfiguration.class})
public class HttpClientAutoConfiguration {
}

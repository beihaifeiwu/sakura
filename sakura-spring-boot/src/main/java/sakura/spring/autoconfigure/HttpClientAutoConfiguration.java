package sakura.spring.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import sakura.spring.http.RestTemplateBuilderConfiguration;
import sakura.spring.http.okhttp.OkHttpClientConfiguration;

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

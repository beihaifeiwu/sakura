package sakura.spring.template.thymeleaf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * Created by liupin on 2017/5/9.
 */
@Configuration
@ConditionalOnClass(SpringTemplateEngine.class)
public class ThymeleafConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThymeleafTemplateRender thymeleafTemplateRender(SpringTemplateEngine engine,
                                                           ThymeleafProperties properties) {
        return new ThymeleafTemplateRender(engine, properties);
    }

}

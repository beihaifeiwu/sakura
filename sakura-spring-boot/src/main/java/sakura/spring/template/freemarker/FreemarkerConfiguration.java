package sakura.spring.template.freemarker;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

/**
 * Created by liupin on 2017/5/9.
 */
@Configuration
@ConditionalOnClass({freemarker.template.Configuration.class, FreeMarkerConfigurationFactory.class})
public class FreemarkerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FreemarkerTemplateRender freemarkerTemplateRender() {
        return new FreemarkerTemplateRender();
    }
}

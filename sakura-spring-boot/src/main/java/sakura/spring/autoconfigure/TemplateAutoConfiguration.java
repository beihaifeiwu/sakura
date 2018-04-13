package sakura.spring.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import sakura.spring.template.TemplateConfiguration;
import sakura.spring.template.freemarker.FreemarkerConfiguration;
import sakura.spring.template.thymeleaf.ThymeleafConfiguration;
import sakura.spring.template.velocity.VelocityConfiguration;

/**
 * Created by liupin on 2017/5/3.
 */
@Configuration
@Import({TemplateConfiguration.class,
        ThymeleafConfiguration.class,
        FreemarkerConfiguration.class,
        VelocityConfiguration.class})
@AutoConfigureAfter({ThymeleafAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
public class TemplateAutoConfiguration {
}

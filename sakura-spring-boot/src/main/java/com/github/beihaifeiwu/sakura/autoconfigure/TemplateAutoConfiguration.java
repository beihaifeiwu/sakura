package com.github.beihaifeiwu.sakura.autoconfigure;

import com.github.beihaifeiwu.sakura.template.TemplateConfiguration;
import com.github.beihaifeiwu.sakura.template.freemarker.FreemarkerConfiguration;
import com.github.beihaifeiwu.sakura.template.thymeleaf.ThymeleafConfiguration;
import com.github.beihaifeiwu.sakura.template.velocity.VelocityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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

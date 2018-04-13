package sakura.spring.template.thymeleaf;

import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import sakura.spring.template.TemplateRender;

import java.util.Collections;
import java.util.Map;

/**
 * Created by liupin on 2017/5/9.
 */
@Order(200)
public class ThymeleafTemplateRender implements TemplateRender {

    private final SpringTemplateEngine thymeleafEngine;
    private final ThymeleafProperties properties;

    @Autowired
    public ThymeleafTemplateRender(SpringTemplateEngine engine,
                                   ThymeleafProperties properties) {
        this.thymeleafEngine = engine;
        this.properties = properties;
    }

    @Override
    public boolean support(@Nullable String template) {
        return !StringUtils.isEmpty(template) && template.endsWith(properties.getSuffix());
    }

    @Nullable
    @Override
    public String render(String template, @Nullable Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model == null ? Collections.emptyMap() : model);
        String templateName = Files.getNameWithoutExtension(template);
        return thymeleafEngine.process(templateName, context);
    }
}

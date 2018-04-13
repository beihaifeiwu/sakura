package sakura.spring.template.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import sakura.spring.template.TemplateRender;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

/**
 * Created by liupin on 2017/8/31.
 */
@Order(400)
public class VelocityTemplateRender implements TemplateRender {

    private final VelocityEngine velocityEngine;
    private final VelocityProperties properties;

    @Autowired
    public VelocityTemplateRender(VelocityEngine velocityEngine,
                                  VelocityProperties properties) {
        this.velocityEngine = velocityEngine;
        this.properties = properties;
    }

    @Override
    public boolean support(@Nullable String template) {
        return !StringUtils.isEmpty(template) && template.endsWith(properties.getSuffix());
    }

    @Nullable
    @Override
    public String render(String template, @Nullable Map<String, Object> model) {
        StringWriter result = new StringWriter();
        VelocityContext velocityContext =
                new VelocityContext(model == null ? Collections.emptyMap() : model);
        String templateLocation = properties.getPrefix() + template;
        velocityEngine.mergeTemplate(templateLocation, properties.getCharsetName(), velocityContext, result);
        return result.toString();
    }

}

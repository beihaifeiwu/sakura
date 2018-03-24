package com.github.beihaifeiwu.sakura.template.freemarker;


import com.github.beihaifeiwu.sakura.template.TemplateException;
import com.github.beihaifeiwu.sakura.template.TemplateRender;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by liupin on 2017/5/9.
 */
@Order(300)
public class FreemarkerTemplateRender implements TemplateRender {

    @Autowired
    private FreeMarkerProperties freeMarkerProperties;

    @Autowired
    private Configuration freemarkerConfiguration;

    @Override
    public boolean support(@Nullable String template) {
        return !StringUtils.isEmpty(template) && template.endsWith(freeMarkerProperties.getSuffix());
    }

    @Nullable
    @Override
    public String render(String template, @Nullable Map<String, Object> model) {
        try {
            Template t = freemarkerConfiguration.getTemplate(template, Charset.forName("UTF-8").name());
            return FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
        } catch (Exception e) {
            throw new TemplateException("Cannot render freemarker template " + template, e);
        }
    }
}

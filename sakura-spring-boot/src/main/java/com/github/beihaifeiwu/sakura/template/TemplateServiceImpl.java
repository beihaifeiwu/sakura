package com.github.beihaifeiwu.sakura.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liupin on 2017/5/9.
 */
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    private final List<TemplateRender> templateRenders;

    public TemplateServiceImpl(List<TemplateRender> templateRenders) {
        this.templateRenders = templateRenders;
    }

    @Override
    public String render(String template, @Nullable Map<String, Object> model) throws IOException {
        if (!CollectionUtils.isEmpty(templateRenders)) {
            for (TemplateRender templateRender : templateRenders) {
                if (templateRender.support(template)) {
                    log.debug("{} is supported by {}", template, templateRender);
                    return templateRender.render(template, model);
                }
            }
        }
        throw new TemplateException(template + " is not supported, Please check your config!");
    }
}

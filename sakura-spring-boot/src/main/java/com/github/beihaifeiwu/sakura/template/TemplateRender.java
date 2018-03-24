package com.github.beihaifeiwu.sakura.template;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * Created by liupin on 2017/1/9.
 */
public interface TemplateRender {

    boolean support(@Nullable String template);

    @Nullable
    String render(String template, @Nullable Map<String, Object> model);

}

package com.github.beihaifeiwu.sakura.web.exhandler.messages;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by liupin on 2017/6/29.
 */
@Slf4j
public class DefaultMessagePopulator implements MessagePopulator {

    public static final String STATUS = ".status";
    public static final String ERROR = ".error";
    public static final String MESSAGE = ".message";

    private final MessageSource messageSource;
    private final EvaluationContext evalContext;

    private final ErrorProperties properties;

    public DefaultMessagePopulator(MessageSource messageSource,
                                   ErrorProperties properties) {
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.addPropertyAccessor(new MapAccessor());
        this.evalContext = ctx;
        this.messageSource = messageSource;
        this.properties = properties;
    }

    @Override
    public <E extends Exception> ErrorMessage createAndPopulate(E ex,
                                                                HttpServletRequest req,
                                                                HttpStatus defaultStatus) {
        Map<String, Object> vars = new HashMap<>(2);
        vars.put("ex", ex);
        vars.put("req", req);

        ErrorMessage m = new ErrorMessage();
        m.setTimestamp(System.currentTimeMillis());
        m.setPath(req.getRequestURI());
        m.setStatus(defaultStatus);
        m.setError(defaultStatus.getReasonPhrase());
        addException(m, ex, req);

        for (Class<?> cls : getReversedClassHierarchy(ex)) {
            populateIfPresent(m, cls.getName(), vars);
        }

        if (StringUtils.isEmpty(m.getMessage())) {
            m.setMessage(ex.getMessage());
        }
        return m;

    }

    @Override
    public String interpolate(String messageTemplate, Map<String, Object> variables) {
        Assert.notNull(messageTemplate, "messageTemplate must not be null");
        try {
            Expression expression = new SpelExpressionParser()
                    .parseExpression(messageTemplate, new TemplateParserContext());
            return expression.getValue(evalContext, variables, String.class);
        } catch (ExpressionException ex) {
            log.error("Failed to interpolate message template: {}", messageTemplate, ex);
            return null;
        }
    }

    @Override
    public String getMessage(String name) {
        String message = "";
        if (!StringUtils.isEmpty(name) && messageSource != null) {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(name, null, null, locale);
        }
        return message;
    }

    private <E extends Exception> void addException(ErrorMessage m, E ex, HttpServletRequest req) {
        if (properties.isIncludeException()) {
            m.setException(ex.getClass().getName());
        }
        switch (properties.getIncludeStacktrace()) {
            case ALWAYS:
                m.setTrace(Throwables.getStackTraceAsString(ex));
                break;
            case ON_TRACE_PARAM:
                if (ServletRequestUtils.getBooleanParameter(req, "trace", false)) {
                    m.setTrace(Throwables.getStackTraceAsString(ex));
                }
                break;
            case NEVER:
            default:
                break;
        }
    }

    private void populateIfPresent(ErrorMessage m, String prefix, Map<String, Object> vars) {

        String message = getMessage(prefix + STATUS);
        if (StringUtils.hasText(message)) {
            String interpolated = interpolate(message, vars);
            if (StringUtils.hasText(interpolated)) {
                Integer status = Integer.valueOf(interpolated);
                m.setStatus(status);
                m.setError(HttpStatus.valueOf(status).getReasonPhrase());
            }
        }

        message = getMessage(prefix + ERROR);
        if (StringUtils.hasText(message)) {
            m.setError(interpolate(message, vars));
        }

        message = getMessage(prefix + MESSAGE);
        if (StringUtils.hasText(message)) {
            m.setMessage(interpolate(message, vars));
        }

    }

    private static <E extends Exception> List<Class<?>> getReversedClassHierarchy(E ex) {
        List<Class<?>> classes = getAllSuperclasses(ex.getClass());
        if (classes == null) {
            classes = Lists.newArrayList(ex.getClass());
        } else {
            classes.add(0, ex.getClass());
        }
        classes = Lists.reverse(classes);
        return classes;
    }

    @Nullable
    private static List<Class<?>> getAllSuperclasses(@Nullable Class<?> cls) {
        if (cls == null) {
            return null;
        }
        List<Class<?>> classes = new ArrayList<>();
        Class superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

}

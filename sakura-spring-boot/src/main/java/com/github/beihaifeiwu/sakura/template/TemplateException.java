package com.github.beihaifeiwu.sakura.template;

/**
 * Created by liupin on 2017/5/9.
 */
public class TemplateException extends RuntimeException {

    public TemplateException() {
        super();
    }

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(Throwable cause) {
        super(cause);
    }

    public TemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}

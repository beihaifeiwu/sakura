package com.github.beihaifeiwu.sakura.annotation;

import java.lang.annotation.*;

/**
 * Created by liupin on 2017/6/19.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpringCache {
    String value();
}

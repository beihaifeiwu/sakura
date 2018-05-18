package sakura.common.plugin;

import java.lang.annotation.*;

/**
 * 插件标识。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Plugin {

    int HIGHEST_PRIORITY = Integer.MIN_VALUE;

    int LOWEST_PRIORITY = Integer.MAX_VALUE;

    int DEFAULT_PRIORITY = 1000;

    int priority() default DEFAULT_PRIORITY;

}
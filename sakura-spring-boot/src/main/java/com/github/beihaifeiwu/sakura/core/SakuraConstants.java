package com.github.beihaifeiwu.sakura.core;

import lombok.experimental.UtilityClass;

/**
 * Created by liupin on 2017/9/1.
 */
@UtilityClass
public class SakuraConstants {

    public static final String DEFAULT_SPRING_CONFIG_SOURCE = "classpath:application-sakura-default.properties";
    public static final String DEFAULT_BEETL_CONFIG_SOURCE = "classpath:sakura-beetl-default.properties";
    public static final String DEFAULT_EXCEPTION_MESSAGE_SOURCE = "classpath*:/sakura-exception-mapping";

    public static final String SPRING_VELOCITY_MACROS = "spring-velocity-macros.vm";

    public static final String DEFAULT_PROP_PREFIX = "sakura";
    public static final String BEETL_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".beetl";
    public static final String COMMAND_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".command";
    public static final String CURATOR_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".curator";
    public static final String EXECUTOR_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".executor";
    public static final String FEIGN_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".feign";
    public static final String OKHTTP_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".okhttp";
    public static final String SCHEDULER_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".scheduler";
    public static final String SPRINGFOX_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".springfox";
    public static final String VELOCITY_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".velocity";
    public static final String WEB_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".web";
    public static final String ZOOKEEPER_PROP_PREFIX = DEFAULT_PROP_PREFIX + ".zookeeper";

}

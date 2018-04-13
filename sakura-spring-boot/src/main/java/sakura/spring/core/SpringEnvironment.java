package sakura.spring.core;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

/**
 * Created by liupin on 2017/6/10.
 */
@UtilityClass
public class SpringEnvironment {

    static Environment getEnvironment() {
        Environment environment = SpringStuffCollector.getEnvironment();
        Preconditions.checkNotNull(environment);
        return environment;
    }

    public static String[] getActiveProfiles() {
        return getEnvironment().getActiveProfiles();
    }

    public static String[] getDefaultProfiles() {
        return getEnvironment().getDefaultProfiles();
    }

    public static boolean acceptsProfiles(String... profiles) {
        return getEnvironment().acceptsProfiles(profiles);
    }

    public static boolean containsProperty(String key) {
        return getEnvironment().containsProperty(key);
    }

    @Nullable
    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }

    @Nullable
    public static <T> T getProperty(String key, Class<T> targetType) {
        return getEnvironment().getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return getEnvironment().getProperty(key, targetType, defaultValue);
    }

    public static String getRequiredProperty(String key) {
        return getEnvironment().getRequiredProperty(key);
    }

    public static <T> T getRequiredProperty(String key, Class<T> targetType) {
        return getEnvironment().getRequiredProperty(key, targetType);
    }

    public String resolvePlaceholders(String text) {
        return getEnvironment().resolvePlaceholders(text);
    }

    public String resolveRequiredPlaceholders(String text) {
        return getEnvironment().resolveRequiredPlaceholders(text);
    }
}

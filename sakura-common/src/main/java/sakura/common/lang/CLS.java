package sakura.common.lang;

import lombok.experimental.UtilityClass;
import sakura.common.util.ClassLoaderWrapper;

/**
 * Created by haomu on 2018/4/17.
 */
@UtilityClass
public class CLS {

    private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    public static ClassLoader getDefaultClassLoader() {
        return classLoaderWrapper.getDefaultClassLoader();
    }

    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        classLoaderWrapper.setDefaultClassLoader(defaultClassLoader);
    }

    public static boolean isPresent(String className) {
        return isPresent(className, null);
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        return forName(className, classLoader) != null;
    }

    public static Class<?> forName(String className) {
        return forName(className, null);
    }

    public static Class<?> forName(String className, ClassLoader classLoader) {
        try {
            return classLoaderWrapper.classForName(className, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}

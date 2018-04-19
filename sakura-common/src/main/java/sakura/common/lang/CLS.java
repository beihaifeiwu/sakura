package sakura.common.lang;

import lombok.experimental.UtilityClass;
import sakura.common.utils.ClassLoaderWrapper;

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
        try {
            classLoaderWrapper.classForName(className, classLoader);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

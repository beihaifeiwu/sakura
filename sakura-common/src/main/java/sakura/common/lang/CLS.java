package sakura.common.lang;

import com.google.common.primitives.Primitives;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.vfs2.AllFileSelector;
import sakura.common.lang.annotation.Nullable;
import sakura.common.util.AntPathMatcher;
import sakura.common.util.ClassLoaderWrapper;
import sakura.common.util.VFSUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by haomu on 2018/4/17.
 */
@Slf4j
@UtilityClass
public class CLS {

    private static final ClassLoaderWrapper WRAPPER = new ClassLoaderWrapper(CLS.class.getClassLoader());
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    public static ClassLoader getDefaultClassLoader() {
        return WRAPPER.getDefaultClassLoader();
    }

    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        WRAPPER.setDefaultClassLoader(defaultClassLoader);
    }

    public static boolean isPrimitiveWrapper(Class<?> type) {
        return Primitives.isWrapperType(type);
    }

    public static boolean isPrimitiveOrWrapper(@Nullable Class<?> type) {
        if (type == null) return false;
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    @Nullable
    public static Class<?> forName(String className) {
        return forName(className, null);
    }

    @Nullable
    public static Class<?> forName(String className, @Nullable ClassLoader classLoader) {
        try {
            return WRAPPER.classForName(className, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean isPresent(String className) {
        return isPresent(className, null);
    }

    public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
        return forName(className, classLoader) != null;
    }

    @Nullable
    public static URL getResourceAsURL(String resource) {
        return getResourceAsURL(resource, null);
    }

    @Nullable
    public static URL getResourceAsURL(String resource, @Nullable ClassLoader classLoader) {
        return WRAPPER.getResourceAsURL(resource, classLoader);
    }

    @Nullable
    public InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, null);
    }

    @Nullable
    public InputStream getResourceAsStream(String resource, @Nullable ClassLoader classLoader) {
        return WRAPPER.getResourceAsStream(resource, classLoader);
    }

    public static Set<URL> getResources(String name) {
        return getResources(name, null);
    }

    public static Set<URL> getResources(String name, @Nullable ClassLoader classLoader) {
        return WRAPPER.getResources(name, classLoader);
    }

    public static Set<URL> findResources(String pattern) {
        if (!MATCHER.isPattern(pattern)) return getResources(pattern);

        val resources = new LinkedHashSet<URL>();
        val rootDir = MATCHER.getRootDir(pattern);
        val subPattern = pattern.substring(rootDir.length());
        val rootResources = getResources(rootDir);

        for (URL rootResource : rootResources) {
            try {
                @Cleanup val dir = VFSUtils.getFile(rootResource);
                val files = dir.findFiles(new AllFileSelector());
                for (int i = 0; i < files.length; i++) {
                    @Cleanup val file = files[i];
                    val subName = dir.getName().getRelativeName(file.getName());
                    if (MATCHER.match(subPattern, subName)) {
                        resources.add(file.getURL());
                    }
                }
            } catch (Exception e) {
                log.error("Find resource failed under {}", rootResource, e);
            }
        }
        return resources;
    }

}

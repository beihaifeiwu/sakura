package sakura.common.lang;

import com.google.common.primitives.Primitives;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.vfs2.AllFileSelector;
import sakura.common.annotation.Nullable;
import sakura.common.util.AntPathMatcher;
import sakura.common.util.ClassLoaderWrapper;
import sakura.common.util.VFSUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by haomu on 2018/4/17.
 */
@Slf4j
@UtilityClass
public class CLS {

    private static final ClassLoaderWrapper WRAPPER = new ClassLoaderWrapper(CLS.class.getClassLoader());
    private static final Lazy<AntPathMatcher> MATCHER = Lazy.of(AntPathMatcher::new);

    public static ClassLoader getDefaultClassLoader() {
        return WRAPPER.getDefaultClassLoader();
    }

    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        WRAPPER.setDefaultClassLoader(defaultClassLoader);
    }

    // Primitive
    // ----------------------------------------------------------------------

    public static boolean isPrimitiveWrapper(Class<?> type) {
        return Primitives.isWrapperType(type);
    }

    public static boolean isPrimitiveOrWrapper(@Nullable Class<?> type) {
        if (type == null) return false;
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    // Name
    // ----------------------------------------------------------------------

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

    // Instance
    // ----------------------------------------------------------------------

    @SneakyThrows
    public static <T> T newInstance(Class<T> type, @Nullable Object... args) {
        T result;
        if (OBJ.isEmpty(args)) {
            result = type.newInstance();
        } else {
            result = ConstructorUtils.invokeConstructor(type, args);
        }
        return result;
    }

    // Assignable
    // ----------------------------------------------------------------------

    public static boolean isAssignable(@Nullable Class<?> cls, @Nullable Class<?> toClass) {
        return ClassUtils.isAssignable(cls, toClass);
    }

    public static boolean isAssignable(@Nullable Class<?> cls, @Nullable Class<?> toClass, final boolean autoboxing) {
        return ClassUtils.isAssignable(cls, toClass, autoboxing);
    }

    // Superclasses/Superinterfaces
    // ----------------------------------------------------------------------

    @Nullable
    public static List<Class<?>> getAllSuperclasses(@Nullable Class<?> cls) {
        return ClassUtils.getAllSuperclasses(cls);
    }

    @Nullable
    public static List<Class<?>> getAllInterfaces(@Nullable Class<?> cls) {
        return ClassUtils.getAllInterfaces(cls);
    }

    public static Iterable<Class<?>> hierarchy(Class<?> type) {
        return hierarchy(type, false);
    }

    /**
     * Get an {@link Iterable} that can iterate over a class hierarchy in ascending (subclass to superclass) order.
     *
     * @param type the type to get the class hierarchy from
     * @param includeInterfaces flag indicating whether to include or exclude interfaces
     * @return Iterable an Iterable over the class hierarchy of the given class
     */
    public static Iterable<Class<?>> hierarchy(Class<?> type, final boolean includeInterfaces) {
        ClassUtils.Interfaces interfaces = includeInterfaces
                ? ClassUtils.Interfaces.INCLUDE : ClassUtils.Interfaces.EXCLUDE;
        return ClassUtils.hierarchy(type, interfaces);
    }

    // Resource
    // ----------------------------------------------------------------------

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
        val matcher = MATCHER.get();
        if (!matcher.isPattern(pattern)) return getResources(pattern);

        val resources = new LinkedHashSet<URL>();
        val rootDir = matcher.getRootDir(pattern);
        val subPattern = pattern.substring(rootDir.length());
        val rootResources = getResources(rootDir);

        for (URL rootResource : rootResources) {
            try {
                @Cleanup val dir = VFSUtils.getFile(rootResource);
                val files = dir.findFiles(new AllFileSelector());
                for (int i = 0; i < files.length; i++) {
                    @Cleanup val file = files[i];
                    val subName = dir.getName().getRelativeName(file.getName());
                    if (matcher.match(subPattern, subName)) {
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

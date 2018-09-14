package sakura.common.lang;

import com.google.common.primitives.Primitives;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import sakura.common.Util;
import sakura.common.annotation.Nullable;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by haomu on 2018/4/17.
 */
@Slf4j
@UtilityClass
public class CLS {

    private static final char PACKAGE_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';

    // ClassLoader
    // ----------------------------------------------------------------------

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) {
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = CLS.class.getClassLoader();
            // getClassLoader() returning null indicates the bootstrap ClassLoader
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignore) {
                }
            }
        }
        Validate.notNull(cl, "Cannot access ClassLoader, why ?");
        return cl;
    }

    public static ClassLoader getClassLoader(@Nullable ClassLoader classLoader) {
        return classLoader == null ? getDefaultClassLoader() : classLoader;
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

    // Package
    // ----------------------------------------------------------------------
    public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
        if (clazz == null) return "";
        String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (packageEndIndex == -1) return "";
        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
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
            return Class.forName(className, true, getClassLoader(classLoader));
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
        if (Util.isEmpty(args)) {
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
     * @param type              the type to get the class hierarchy from
     * @param includeInterfaces flag indicating whether to include or exclude interfaces
     * @return Iterable an Iterable over the class hierarchy of the given class
     */
    public static Iterable<Class<?>> hierarchy(Class<?> type, final boolean includeInterfaces) {
        ClassUtils.Interfaces interfaces = includeInterfaces
                ? ClassUtils.Interfaces.INCLUDE
                : ClassUtils.Interfaces.EXCLUDE;
        return ClassUtils.hierarchy(type, interfaces);
    }

    // Resource
    // ----------------------------------------------------------------------

    @Nullable
    public static URL getResource(String resource) {
        return getResource(resource, null);
    }

    @Nullable
    public static URL getResource(String resource, @Nullable ClassLoader classLoader) {
        return getClassLoader(classLoader).getResource(resource);
    }

    public static Set<URL> getResources(String name) {
        return getResources(name, null);
    }

    @SneakyThrows
    public static Set<URL> getResources(String name, @Nullable ClassLoader classLoader) {
        Set<URL> urls = new LinkedHashSet<>();
        ClassLoader cl = getClassLoader(classLoader);
        if ("".equals(name)) {
            addAllClassLoaderJarRoots(cl, urls);
        } else {
            Enumeration<URL> resources = cl.getResources(name);
            while (resources.hasMoreElements()) {
                urls.add(resources.nextElement());
            }
        }
        return urls;
    }

    @Nullable
    public static InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, null);
    }

    @Nullable
    public static InputStream getResourceAsStream(String resource, @Nullable ClassLoader classLoader) {
        return getClassLoader(classLoader).getResourceAsStream(resource);
    }

    public static Set<URL> getJarRoots(@Nullable ClassLoader... classLoaders) {
        Set<URL> jarRoots = new LinkedHashSet<>();
        if (classLoaders != null && classLoaders.length > 0) {
            for (int i = 0; i < classLoaders.length; i++) {
                addAllClassLoaderJarRoots(classLoaders[i], jarRoots);
            }
        }
        return jarRoots;
    }

    private void addAllClassLoaderJarRoots(@Nullable ClassLoader classLoader, Set<URL> jarRoots) {
        if (classLoader == null) return;
        if (classLoader instanceof URLClassLoader) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        String str = url.toString();
                        if (str.endsWith(".jar")) {
                            jarRoots.add(new URL("jar:" + str + "!/"));
                        } else {
                            jarRoots.add(url);
                        }
                    } catch (MalformedURLException e) {
                        log.debug("Cannot search for matching files underneath [{}] " +
                                "because it cannot be converted to a valid 'jar:' URL: {}", url, e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.debug("Cannot introspect jar files since ClassLoader [{}] " +
                        "does not support 'getURLs()': {}", classLoader, e.getMessage());
            }
        }
        if (classLoader == ClassLoader.getSystemClassLoader()) {
            // "java.class.path" manifest evaluation...
            addClassPathManifestEntries(jarRoots);
        }
        try {
            addAllClassLoaderJarRoots(classLoader.getParent(), jarRoots);
        } catch (Exception e) {
            log.debug("Cannot introspect jar files in parent ClassLoader since [{}] " +
                    "does not support 'getParent()':", classLoader, e.getMessage());
        }
    }

    /**
     * Determine jar file references from the "java.class.path." manifest property and add them
     * to the given set of resources in the form of pointers to the root of the jar file content.
     */
    private static void addClassPathManifestEntries(Set<URL> jarRoots) {
        try {
            String javaClassPathProperty = System.getProperty("java.class.path", "");
            String[] paths = StringUtils.split(javaClassPathProperty, System.getProperty("path.separator"));
            for (String path : paths) {
                try {
                    jarRoots.add(convertClassLoaderURL(path));
                } catch (MalformedURLException ex) {
                    log.debug("Cannot search for matching files underneath [{}] " +
                            "because it cannot be converted to a valid 'jar:' URL: {}", path, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            log.debug("Failed to evaluate 'java.class.path' manifest entries: ", ex);
        }
    }

    private static URL convertClassLoaderURL(String filePath) throws MalformedURLException {
        filePath = new File(filePath).getAbsolutePath();
        filePath = FilenameUtils.normalizeNoEndSeparator(filePath, true);
        if (filePath.indexOf(':') == 1) {
            // Possibly "c:" drive prefix on Windows, to be upper-cased for proper duplicate detection
            filePath = StringUtils.capitalize(filePath);
        }
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        filePath = StringUtils.replace(filePath, " ", "%20");
        if (filePath.endsWith(".jar")) {
            return new URL("jar:file:" + filePath + "!/");
        } else {
            return new URL("file:" + filePath + "/");
        }
    }

}

package sakura.common.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import sakura.common.lang.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A class to wrap access to multiple class loaders making them work as one
 */
@Slf4j
@Getter
@Setter
public class ClassLoaderWrapper {

    private ClassLoader defaultClassLoader;
    private ClassLoader systemClassLoader;

    public ClassLoaderWrapper() {
        this(null);
    }

    public ClassLoaderWrapper(@Nullable ClassLoader defaultClassLoader) {
        this.defaultClassLoader = defaultClassLoader;
        try {
            this.systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException ignored) {
        }
    }

    public Set<URL> getResources(String name, @Nullable ClassLoader classLoader) {
        val urls = new LinkedHashSet<URL>();
        val classLoaders = getClassLoaders(classLoader);
        if ("".equals(name)) {
            for (ClassLoader loader : classLoaders) {
                addAllClassLoaderJarRoots(loader, urls);
            }
            return urls;
        }

        for (ClassLoader loader : classLoaders) {
            addAllClassLoaderResources(loader, name, urls);
        }
        if (systemClassLoader == null) {
            try {
                val resources = ClassLoader.getSystemResources(name);
                while (resources.hasMoreElements()) {
                    urls.add(resources.nextElement());
                }
            } catch (IOException ignored) {
            }
        }

        return urls;
    }

    @Nullable
    public URL getResourceAsURL(String resource, @Nullable ClassLoader classLoader) {
        URL url = null;
        for (ClassLoader loader : getClassLoaders(classLoader)) {
            if (loader == null) continue;
            url = loader.getResource(resource);
            if (url == null) url = loader.getResource("/" + resource);
            if (url != null) break;
        }
        if (systemClassLoader == null && url == null) {
            url = ClassLoader.getSystemResource(resource);
        }
        return url;
    }

    @Nullable
    public InputStream getResourceAsStream(String resource, @Nullable ClassLoader classLoader) {
        InputStream stream = null;
        for (ClassLoader loader : getClassLoaders(classLoader)) {
            if (loader == null) continue;
            stream = loader.getResourceAsStream(resource);
            if (stream == null) stream = loader.getResourceAsStream("/" + resource);
            if (stream != null) break;
        }
        if (systemClassLoader == null && stream == null) {
            stream = ClassLoader.getSystemResourceAsStream(resource);
        }
        return stream;
    }

    public Class<?> classForName(String name, @Nullable ClassLoader classLoader) throws ClassNotFoundException {
        for (ClassLoader cl : getClassLoaders(classLoader)) {
            if (null != cl) {
                try {
                    Class<?> c = Class.forName(name, true, cl);
                    if (null != c) return c;
                } catch (ClassNotFoundException ignore) {
                }
            }
        }
        throw new ClassNotFoundException("Cannot find class: " + name);
    }

    private ClassLoader[] getClassLoaders(@Nullable ClassLoader classLoader) {
        return new ClassLoader[]{
                classLoader,
                defaultClassLoader,
                Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(),
                systemClassLoader};
    }

    private void addAllClassLoaderResources(@Nullable ClassLoader classLoader, String name, Set<URL> resources) {
        if (classLoader == null) return;
        try {
            val enumeration = classLoader.getResources(name);
            while (enumeration.hasMoreElements()) {
                resources.add(enumeration.nextElement());
            }
        } catch (IOException ignore) {
        }
        try {
            val enumeration = classLoader.getResources("/" + name);
            while (enumeration.hasMoreElements()) {
                resources.add(enumeration.nextElement());
            }
        } catch (IOException ignore) {
        }
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
        try {
            addAllClassLoaderJarRoots(classLoader.getParent(), jarRoots);
        } catch (Exception e) {
            log.debug("Cannot introspect jar files in parent ClassLoader since [{}] " +
                    "does not support 'getParent()':", classLoader, e.getMessage());
        }
    }

}

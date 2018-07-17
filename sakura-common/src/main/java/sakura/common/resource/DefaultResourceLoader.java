package sakura.common.resource;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import sakura.common.annotation.Nullable;
import sakura.common.lang.CLS;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import static sakura.common.resource.ResourceUtils.*;

/**
 * Default implementation of the {@link ResourceLoader} interface.
 *
 * <p>Will return a {@link UrlResource} if the location value is a URL,
 * and a {@link ClassPathResource} if it is a non-URL path or a
 * "classpath:" pseudo-URL.
 */
@Slf4j
public class DefaultResourceLoader implements ResourceLoader {

    private ClassLoader classLoader;
    private PathMatcher pathMatcher;
    private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);

    public DefaultResourceLoader() {
        this(CLS.getDefaultClassLoader());
    }

    public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
        this(classLoader, new AntPathMatcher());
    }

    public DefaultResourceLoader(@Nullable ClassLoader classLoader, PathMatcher pathMatcher) {
        this.classLoader = classLoader;
        this.pathMatcher = pathMatcher;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return (this.classLoader != null ? this.classLoader : CLS.getDefaultClassLoader());
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public void addProtocolResolver(ProtocolResolver resolver) {
        Validate.notNull(resolver, "ProtocolResolver must not be null");
        this.protocolResolvers.add(resolver);
    }

    public Collection<ProtocolResolver> getProtocolResolvers() {
        return this.protocolResolvers;
    }


    @Override
    public Resource getResource(String location) {
        Validate.notNull(location, "Location must not be null");

        for (ProtocolResolver protocolResolver : this.protocolResolvers) {
            Resource resource = protocolResolver.resolve(location, this);
            if (resource != null) return resource;
        }

        if (location.startsWith("/")) return getResourceByPath(location);
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = location.substring(CLASSPATH_URL_PREFIX.length());
            return new ClassPathResource(path, getClassLoader());
        }

        try {
            // Try to parse the location as a URL...
            URL url = new URL(location);
            return new UrlResource(url);
        } catch (MalformedURLException ex) {
            // No URL -> resolve as resource path.
            return getResourceByPath(location);
        }
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        Validate.notNull(locationPattern, "Location pattern must not be null");
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            // a class path resource (multiple resources for same name possible)
            String pattern = locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length());
            if (pathMatcher.isPattern(pattern)) {
                // a class path resource pattern
                return findPathMatchingResources(locationPattern);
            } else {
                // all class path resources with the given name
                return findAllClassPathResources(pattern);
            }
        } else {
            // Generally only look for a pattern after a prefix here,
            // and on Tomcat only after the "*/" separator for its "war:" protocol.
            int prefixEnd = (locationPattern.startsWith("war:")
                    ? locationPattern.indexOf("*/") + 1
                    : locationPattern.indexOf(':') + 1);
            if (pathMatcher.isPattern(locationPattern.substring(prefixEnd))) {
                // a file pattern
                return findPathMatchingResources(locationPattern);
            } else {
                // a single resource with the given name
                return new Resource[]{getResource(locationPattern)};
            }
        }
    }

    /**
     * Return a Resource handle for the resource at the given path.
     * <p>The default implementation supports class path locations. This should
     * be appropriate for standalone implementations but can be overridden,
     * e.g. for implementations targeted at a Servlet container.
     *
     * @param path the path to the resource
     * @return the corresponding Resource handle
     * @see ClassPathResource
     */
    protected Resource getResourceByPath(String path) {
        return new ClassPathContextResource(path, getClassLoader());
    }

    /**
     * Find all class location resources with the given location via the ClassLoader.
     * Delegates to {@link #doFindAllClassPathResources(String)}.
     *
     * @param location the absolute path within the classpath
     * @return the result as Resource array
     * @throws IOException in case of I/O errors
     * @see java.lang.ClassLoader#getResources
     * @see #convertClassLoaderURL
     */
    protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) path = path.substring(1);
        Set<Resource> result = doFindAllClassPathResources(path);
        log.debug("Resolved classpath location [" + location + "] to resources " + result);
        return result.toArray(new Resource[0]);
    }

    /**
     * Find all class location resources with the given path via the ClassLoader.
     * Called by {@link #findAllClassPathResources(String)}.
     *
     * @param path the absolute path within the classpath (never a leading slash)
     * @return a mutable Set of matching Resource instances
     */
    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        if ("".equals(path)) {
            // The above result is likely to be incomplete, i.e. only containing file system references.
            // We need to have pointers to each of the jar files on the classpath as well...
            addAllClassLoaderJarRoots(cl, result);
        }
        return result;
    }

    /**
     * Convert the given URL as returned from the ClassLoader into a {@link Resource}.
     * <p>The default implementation simply creates a {@link UrlResource} instance.
     *
     * @param url a URL as returned from the ClassLoader
     * @return the corresponding Resource object
     */
    protected Resource convertClassLoaderURL(URL url) {
        return new UrlResource(url);
    }

    /**
     * Search all {@link URLClassLoader} URLs for jar file references and add them to the
     * given set of resources in the form of pointers to the root of the jar file content.
     *
     * @param classLoader the ClassLoader to search (including its ancestors)
     * @param result      the set of resources to add jar roots to
     */
    protected void addAllClassLoaderJarRoots(@Nullable ClassLoader classLoader, Set<Resource> result) {
        if (classLoader instanceof URLClassLoader) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        UrlResource jarResource = new UrlResource(JAR_URL_PREFIX + url + JAR_URL_SEPARATOR);
                        if (jarResource.exists()) result.add(jarResource);
                    } catch (MalformedURLException ex) {
                        log.debug("Cannot search for matching files underneath [{}] " +
                                "because it cannot be converted to a valid 'jar:' URL: {}", url, ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                log.debug("Cannot introspect jar files since ClassLoader [{}] does not support 'getURLs()'", classLoader, ex);
            }
        }

        if (classLoader == ClassLoader.getSystemClassLoader()) {
            // "java.class.path" manifest evaluation...
            addClassPathManifestEntries(result);
        }

        if (classLoader != null) {
            try {
                // Hierarchy traversal...
                addAllClassLoaderJarRoots(classLoader.getParent(), result);
            } catch (Exception ex) {
                log.debug("Cannot introspect jar files in parent ClassLoader since [{}] does not support 'getParent()'", classLoader, ex);
            }
        }
    }

    /**
     * Determine jar file references from the "java.class.path." manifest property and add them
     * to the given set of resources in the form of pointers to the root of the jar file content.
     *
     * @param result the set of resources to add jar roots to
     */
    protected void addClassPathManifestEntries(Set<Resource> result) {
        try {
            String javaClassPathProperty = System.getProperty("java.class.path", "");
            String[] paths = StringUtils.split(javaClassPathProperty, System.getProperty("path.separator"));
            for (String path : paths) {
                try {
                    String filePath = new File(path).getAbsolutePath();
                    int prefixIndex = filePath.indexOf(':');
                    if (prefixIndex == 1) {
                        // Possibly "c:" drive prefix on Windows, to be upper-cased for proper duplicate detection
                        filePath = StringUtils.capitalize(filePath);
                    }
                    UrlResource jarResource = new UrlResource(JAR_URL_PREFIX + FILE_URL_PREFIX + filePath + JAR_URL_SEPARATOR);
                    // Potentially overlapping with URLClassLoader.getURLs() result above!
                    if (!result.contains(jarResource) && !hasDuplicate(filePath, result) && jarResource.exists()) {
                        result.add(jarResource);
                    }
                } catch (MalformedURLException ex) {
                    log.debug("Cannot search for matching files underneath [{}] " +
                            "because it cannot be converted to a valid 'jar:' URL: {}", path, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            log.debug("Failed to evaluate 'java.class.path' manifest entries: ", ex);
        }
    }

    /**
     * Check whether the given file path has a duplicate but differently structured entry
     * in the existing result, i.e. with or without a leading slash.
     *
     * @param filePath the file path (with or without a leading slash)
     * @param result   the current result
     * @return {@code true} if there is a duplicate (i.e. to ignore the given file path),
     * {@code false} to proceed with adding a corresponding resource to the current result
     */
    private boolean hasDuplicate(String filePath, Set<Resource> result) {
        if (result.isEmpty()) return false;
        String duplicatePath = (filePath.startsWith("/") ? filePath.substring(1) : "/" + filePath);
        try {
            UrlResource resource = new UrlResource(JAR_URL_PREFIX + FILE_URL_PREFIX + duplicatePath + JAR_URL_SEPARATOR);
            return result.contains(resource);
        } catch (MalformedURLException ex) {
            // Ignore: just for testing against duplicate.
            return false;
        }
    }

    /**
     * Find all resources that match the given location pattern via the
     * Ant-style PathMatcher. Supports resources in jar files and zip files
     * and in the file system.
     *
     * @param locationPattern the location pattern to match
     * @return the result as Resource array
     * @throws IOException in case of I/O errors
     * @see #doFindPathMatchingJarResources
     * @see #doFindPathMatchingFileResources
     */
    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource rootDirResource : rootDirResources) {
            rootDirResource = resolveRootDirResource(rootDirResource);
            URL rootDirUrl = rootDirResource.getURL();
            if (ResourceUtils.isJarURL(rootDirUrl) || isJarResource(rootDirResource)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource, rootDirUrl, subPattern));
            } else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        log.debug("Resolved location pattern [{}] to resources {}", locationPattern, result);
        return result.toArray(new Resource[0]);
    }

    /**
     * Determine the root directory for the given location.
     * <p>Used for determining the starting point for file matching,
     * resolving the root directory location to a {@code java.io.File}
     * and passing it into {@code retrieveMatchingFiles}, with the
     * remainder of the location as pattern.
     * <p>Will return "/WEB-INF/" for the pattern "/WEB-INF/*.xml",
     * for example.
     *
     * @param location the location to check
     * @return the part of the location that denotes the root directory
     * @see #retrieveMatchingFiles
     */
    protected String determineRootDir(String location) {
        int prefixEnd = location.indexOf(':') + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && pathMatcher.isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    /**
     * Resolve the specified resource for path matching.
     * <p>By default, Equinox OSGi "bundleresource:" / "bundleentry:" URL will be
     * resolved into a standard jar file URL that be traversed using Spring's
     * standard jar file traversal algorithm. For any preceding custom resolution,
     * override this method and replace the resource handle accordingly.
     *
     * @param original the resource to resolve
     * @return the resolved resource (may be identical to the passed-in resource)
     * @throws IOException in case of resolution failure
     */
    protected Resource resolveRootDirResource(Resource original) throws IOException {
        return original;
    }

    /**
     * Return whether the given resource handle indicates a jar resource
     * that the {@code doFindPathMatchingJarResources} method can handle.
     * <p>By default, the URL protocols "jar", "zip", "vfszip and "wsjar"
     * will be treated as jar resources. This template method allows for
     * detecting further kinds of jar-like resources, e.g. through
     * {@code instanceof} checks on the resource handle type.
     *
     * @param resource the resource handle to check
     *                 (usually the root directory to start path matching from)
     * @see #doFindPathMatchingJarResources
     */
    protected boolean isJarResource(Resource resource) throws IOException {
        return false;
    }

    /**
     * Find all resources in jar files that match the given location pattern
     * via the Ant-style PathMatcher.
     *
     * @param rootDirResource the root directory as Resource
     * @param rootDirURL      the pre-resolved root directory URL
     * @param subPattern      the sub pattern to match (below the root directory)
     * @return a mutable Set of matching Resource instances
     * @throws IOException in case of I/O errors
     * @see java.net.JarURLConnection
     */
    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource,
                                                           URL rootDirURL, String subPattern) throws IOException {
        URLConnection con = rootDirURL.openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean closeJarFile;

        if (con instanceof JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
            closeJarFile = !jarCon.getUseCaches();
        } else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootDirURL.getFile();
            try {
                int separatorIndex = urlFile.indexOf(ResourceUtils.WAR_URL_SEPARATOR);
                if (separatorIndex == -1) {
                    separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
                }
                if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + 2);  // both separators are 2 chars
                    jarFile = getJarFile(jarFileUrl);
                } else {
                    jarFile = new JarFile(urlFile);
                    jarFileUrl = urlFile;
                    rootEntryPath = "";
                }
                closeJarFile = true;
            } catch (ZipException ex) {
                log.debug("Skipping invalid jar classpath entry [{}]", urlFile);
                return Collections.emptySet();
            }
        }

        try {
            log.debug("Looking for matching resources in jar file [{}]", jarFileUrl);
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<Resource> result = new LinkedHashSet<>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (pathMatcher.match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            return result;
        } finally {
            if (closeJarFile) {
                jarFile.close();
            }
        }
    }

    /**
     * Resolve the given jar file URL into a JarFile object.
     */
    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }

    /**
     * Find all resources in the file system that match the given location pattern
     * via the Ant-style PathMatcher.
     *
     * @param rootDirResource the root directory as Resource
     * @param subPattern      the sub pattern to match (below the root directory)
     * @return a mutable Set of matching Resource instances
     * @throws IOException in case of I/O errors
     * @see #retrieveMatchingFiles
     */
    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
        File rootDir;
        try {
            rootDir = rootDirResource.getFile().getAbsoluteFile();
        } catch (IOException ex) {
            log.warn("Cannot search for matching files underneath {} " +
                    "because it does not correspond to a directory in the file system", rootDirResource, ex);
            return Collections.emptySet();
        }
        return doFindMatchingFileSystemResources(rootDir, subPattern);
    }

    /**
     * Find all resources in the file system that match the given location pattern
     * via the Ant-style PathMatcher.
     *
     * @param rootDir    the root directory in the file system
     * @param subPattern the sub pattern to match (below the root directory)
     * @return a mutable Set of matching Resource instances
     * @throws IOException in case of I/O errors
     * @see #retrieveMatchingFiles
     */
    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
        log.debug("Looking for matching resources in directory tree [{}]", rootDir.getPath());
        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
        Set<Resource> result = Sets.newLinkedHashSetWithExpectedSize(matchingFiles.size());
        for (File file : matchingFiles) {
            result.add(new FileSystemResource(file));
        }
        return result;
    }

    /**
     * Retrieve files that match the given path pattern,
     * checking the given directory and its subdirectories.
     *
     * @param rootDir the directory to start from
     * @param pattern the pattern to match against,
     *                relative to the root directory
     * @return a mutable Set of matching Resource instances
     * @throws IOException if directory contents could not be retrieved
     */
    protected Set<File> retrieveMatchingFiles(File rootDir, String pattern) throws IOException {
        if (!rootDir.exists()) {
            // Silently skip non-existing directories.
            log.debug("Skipping [{}] because it does not exist", rootDir.getAbsolutePath());
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            // Complain louder if it exists but is no directory.
            log.warn("Skipping [{}] because it does not denote a directory", rootDir.getAbsolutePath());
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            log.warn("Cannot search for matching files underneath directory [{}] " +
                    "because the application is not allowed to read the directory", rootDir.getAbsolutePath());
            return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        if (!pattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
        Set<File> result = new LinkedHashSet<>(8);
        doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
    }

    /**
     * Recursively retrieve files that match the given pattern,
     * adding them to the given result list.
     *
     * @param fullPattern the pattern to match against,
     *                    with prepended root directory path
     * @param dir         the current directory
     * @param result      the Set of matching File instances to add to
     * @throws IOException if directory contents could not be retrieved
     */
    protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
        log.debug("Searching directory [{}] for files matching pattern [{}]", dir.getAbsolutePath(), fullPattern);
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            log.warn("Could not retrieve contents of directory [{}]", dir.getAbsolutePath());
            return;
        }
        Arrays.sort(dirContents);
        for (File content : dirContents) {
            String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
                if (!content.canRead()) {
                    log.debug("Skipping subdirectory [{}] because " +
                            "the application is not allowed to read the directory", dir.getAbsolutePath());
                } else {
                    doRetrieveMatchingFiles(fullPattern, content, result);
                }
            }
            if (getPathMatcher().match(fullPattern, currPath)) {
                result.add(content);
            }
        }
    }


}

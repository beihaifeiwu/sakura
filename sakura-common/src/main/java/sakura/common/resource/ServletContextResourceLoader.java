package sakura.common.resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static sakura.common.resource.ResourceUtils.*;

/**
 * Created by haomu on 2018/7/17.
 */
@Slf4j
public class ServletContextResourceLoader extends DefaultResourceLoader {

    private final ServletContext servletContext;

    public ServletContextResourceLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new ServletContextResource(this.servletContext, path);
    }

    /**
     * Overridden version which checks for ServletContextResource
     * and uses {@code ServletContext.getResourcePaths} to find
     * matching resources below the web application root directory.
     * In case of other resources, delegates to the superclass version.
     *
     * @see #doRetrieveMatchingServletContextResources
     * @see ServletContextResource
     * @see javax.servlet.ServletContext#getResourcePaths
     */
    @Override
    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource,
                                                            String subPattern) throws IOException {
        if (rootDirResource instanceof ServletContextResource) {
            ServletContextResource scResource = (ServletContextResource) rootDirResource;
            ServletContext sc = scResource.getServletContext();
            String fullPattern = scResource.getPath() + subPattern;
            Set<Resource> result = new LinkedHashSet<Resource>(8);
            doRetrieveMatchingServletContextResources(sc, fullPattern, scResource.getPath(), result);
            return result;
        } else {
            return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
        }
    }

    /**
     * Recursively retrieve ServletContextResources that match the given pattern,
     * adding them to the given result set.
     *
     * @param servletContext the ServletContext to work on
     * @param fullPattern    the pattern to match against,
     *                       with preprended root directory path
     * @param dir            the current directory
     * @param result         the Set of matching Resources to add to
     * @throws IOException if directory contents could not be retrieved
     * @see ServletContextResource
     * @see javax.servlet.ServletContext#getResourcePaths
     */
    protected void doRetrieveMatchingServletContextResources(ServletContext servletContext,
                                                             String fullPattern,
                                                             String dir, Set<Resource> result) throws IOException {
        Set<String> candidates = servletContext.getResourcePaths(dir);
        if (candidates == null || candidates.isEmpty()) return;

        boolean dirDepthNotFixed = fullPattern.contains("**");
        int jarFileSep = fullPattern.indexOf(JAR_URL_SEPARATOR);
        String jarFilePath = null;
        String pathInJarFile = null;
        if (jarFileSep > 0 && jarFileSep + JAR_URL_SEPARATOR.length() < fullPattern.length()) {
            jarFilePath = fullPattern.substring(0, jarFileSep);
            pathInJarFile = fullPattern.substring(jarFileSep + JAR_URL_SEPARATOR.length());
        }

        for (String currPath : candidates) {
            if (!currPath.startsWith(dir)) {
                // Returned resource path does not start with relative directory:
                // assuming absolute path returned -> strip absolute path.
                int dirIndex = currPath.indexOf(dir);
                if (dirIndex != -1) {
                    currPath = currPath.substring(dirIndex);
                }
            }
            if (currPath.endsWith("/") && (dirDepthNotFixed
                    || StringUtils.countMatches(currPath, "/") <= StringUtils.countMatches(fullPattern, "/"))) {
                // Search subdirectories recursively: ServletContext.getResourcePaths
                // only returns entries for one directory level.
                doRetrieveMatchingServletContextResources(servletContext, fullPattern, currPath, result);
            }
            if (jarFilePath != null && getPathMatcher().match(jarFilePath, currPath)) {
                // Base pattern matches a jar file - search for matching entries within.
                String absoluteJarPath = servletContext.getRealPath(currPath);
                if (absoluteJarPath != null) {
                    doRetrieveMatchingJarEntries(absoluteJarPath, pathInJarFile, result);
                }
            }
            if (getPathMatcher().match(fullPattern, currPath)) {
                result.add(new ServletContextResource(servletContext, currPath));
            }
        }
    }

    /**
     * Extract entries from the given jar by pattern.
     *
     * @param jarFilePath  the path to the jar file
     * @param entryPattern the pattern for jar entries to match
     * @param result       the Set of matching Resources to add to
     */
    private void doRetrieveMatchingJarEntries(String jarFilePath, String entryPattern, Set<Resource> result) {
        log.debug("Searching jar file [{}] for entries matching [{}]", jarFilePath, entryPattern);
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (getPathMatcher().match(entryPattern, entryPath)) {
                    result.add(new UrlResource(URL_PROTOCOL_JAR,
                            FILE_URL_PREFIX + jarFilePath + JAR_URL_SEPARATOR + entryPath));
                }
            }
        } catch (IOException ex) {
            log.warn("Cannot search for matching resources in jar file [{}] " +
                    "because the jar cannot be opened through the file system", jarFilePath, ex);
        }
    }

}

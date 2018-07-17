package sakura.common.resource;

import org.apache.commons.lang3.Validate;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * {@link Resource} implementation for
 * {@link javax.servlet.ServletContext} resources, interpreting
 * relative paths within the web application root directory.
 *
 * <p>Always supports stream access and URL access, but only allows
 * {@code java.io.File} access when the web application archive
 * is expanded.
 */
public class ServletContextResource extends AbstractFileResolvingResource implements ContextResource {

    private final ServletContext servletContext;

    private final String path;


    /**
     * Create a new ServletContextResource.
     * <p>The Servlet spec requires that resource paths start with a slash,
     * even if many containers accept paths without leading slash too.
     * Consequently, the given path will be prepended with a slash if it
     * doesn't already start with one.
     *
     * @param servletContext the ServletContext to load from
     * @param path           the path of the resource
     */
    public ServletContextResource(ServletContext servletContext, String path) {
        // check ServletContext
        Validate.notNull(servletContext, "Cannot resolve ServletContextResource without ServletContext");
        this.servletContext = servletContext;

        // check path
        Validate.notNull(path, "Path is required");
        String pathToUse = cleanPath(path);
        if (!pathToUse.startsWith("/")) {
            pathToUse = "/" + pathToUse;
        }
        this.path = pathToUse;
    }

    public final ServletContext getServletContext() {
        return this.servletContext;
    }

    public final String getPath() {
        return this.path;
    }

    @Override
    public boolean exists() {
        try {
            URL url = this.servletContext.getResource(this.path);
            return (url != null);
        } catch (MalformedURLException ex) {
            return false;
        }
    }

    /**
     * This implementation delegates to {@code ServletContext.getResourceAsStream},
     * which returns {@code null} in case of a non-readable resource (e.g. a directory).
     *
     * @see javax.servlet.ServletContext#getResourceAsStream(String)
     */
    @Override
    public boolean isReadable() {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is != null) {
            try {
                is.close();
            } catch (IOException ex) {
                // ignore
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This implementation delegates to {@code ServletContext.getResourceAsStream},
     * but throws a FileNotFoundException if no resource found.
     *
     * @see javax.servlet.ServletContext#getResourceAsStream(String)
     */
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is == null) {
            throw new FileNotFoundException("Could not open " + getDescription());
        }
        return is;
    }

    /**
     * This implementation delegates to {@code ServletContext.getResource},
     * but throws a FileNotFoundException if no resource found.
     *
     * @see javax.servlet.ServletContext#getResource(String)
     */
    @Override
    public URL getURL() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url == null) {
            throw new FileNotFoundException(
                    getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    /**
     * This implementation resolves "file:" URLs or alternatively delegates to
     * {@code ServletContext.getRealPath}, throwing a FileNotFoundException
     * if not found or not resolvable.
     *
     * @see javax.servlet.ServletContext#getResource(String)
     * @see javax.servlet.ServletContext#getRealPath(String)
     */
    @Override
    public File getFile() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url != null && ResourceUtils.isFileURL(url)) {
            // Proceed with file system resolution...
            return super.getFile();
        } else {
            String realPath = this.path;
            // Interpret location as relative to the web application root directory.
            if (!realPath.startsWith("/")) realPath = "/" + realPath;
            realPath = servletContext.getRealPath(realPath);
            if (realPath == null) {
                throw new FileNotFoundException(
                        "ServletContext resource [" + path + "] cannot be resolved to absolute file path - " +
                                "web application archive not expanded?");
            }
            return new File(realPath);
        }
    }

    @Override
    public Resource createRelative(String relativePath) {
        String pathToUse = applyRelativePath(this.path, relativePath);
        return new ServletContextResource(this.servletContext, pathToUse);
    }

    /**
     * This implementation returns the name of the file that this ServletContext
     * resource refers to.
     */
    @Override
    public String getName() {
        return getName(this.path);
    }

    /**
     * This implementation returns a description that includes the ServletContext
     * resource location.
     */
    @Override
    public String getDescription() {
        return "ServletContext resource [" + this.path + "]";
    }

    @Override
    public String getPathWithinContext() {
        return this.path;
    }


    /**
     * This implementation compares the underlying ServletContext resource locations.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ServletContextResource) {
            ServletContextResource otherRes = (ServletContextResource) obj;
            return (this.servletContext.equals(otherRes.servletContext) && this.path.equals(otherRes.path));
        }
        return false;
    }

    /**
     * This implementation returns the hash code of the underlying
     * ServletContext resource location.
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

}
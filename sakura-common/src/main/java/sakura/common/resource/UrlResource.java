package sakura.common.resource;


import org.apache.commons.lang3.Validate;
import sakura.common.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * {@link Resource} implementation for {@code java.net.URL} locators.
 * Supports resolution as a {@code URL} and also as a {@code File} in
 * case of the {@code "file:"} protocol.
 */
public class UrlResource extends AbstractFileResolvingResource {

    /**
     * Original URI, if available; used for URI and File access.
     */
    private final URI uri;

    /**
     * Original URL, used for actual access.
     */
    private final URL url;

    /**
     * Cleaned URL (with normalized path), used for comparisons.
     */
    private final URL cleanedUrl;


    public UrlResource(URI uri) throws MalformedURLException {
        Validate.notNull(uri, "URI must not be null");
        this.uri = uri;
        this.url = uri.toURL();
        this.cleanedUrl = getCleanedUrl(this.url, uri.toString());
    }

    public UrlResource(URL url) {
        Validate.notNull(url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = getCleanedUrl(this.url, url.toString());
        this.uri = null;
    }

    public UrlResource(String path) throws MalformedURLException {
        Validate.notNull(path, "Path must not be null");
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = getCleanedUrl(this.url, path);
    }

    public UrlResource(String protocol, String location) throws MalformedURLException {
        this(protocol, location, null);
    }

    /**
     * Create a new {@code UrlResource} based on a URI specification.
     * <p>The given parts will automatically get encoded if necessary.
     *
     * @param protocol the URL protocol to use (e.g. "jar" or "file" - without colon);
     *                 also known as "scheme"
     * @param location the location (e.g. the file path within that protocol);
     *                 also known as "scheme-specific part"
     * @param fragment the fragment within that location (e.g. anchor on an HTML page,
     *                 as following after a "#" separator)
     * @throws MalformedURLException if the given URL specification is not valid
     * @see java.net.URI#URI(String, String, String)
     */
    public UrlResource(String protocol, String location, @Nullable String fragment) throws MalformedURLException {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
            this.cleanedUrl = getCleanedUrl(this.url, this.uri.toString());
        } catch (URISyntaxException ex) {
            MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }

    private URL getCleanedUrl(URL originalUrl, String originalPath) {
        try {
            return new URL(cleanPath(originalPath));
        } catch (MalformedURLException ex) {
            // Cleaned URL path cannot be converted to URL
            // -> take original URL.
            return originalUrl;
        }
    }

    /**
     * This implementation opens an InputStream for the given URL.
     * <p>It sets the {@code useCaches} flag to {@code false},
     * mainly to avoid jar file locking on Windows.
     *
     * @see java.net.URL#openConnection()
     * @see java.net.URLConnection#setUseCaches(boolean)
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    @Override
    public URL getURL() throws IOException {
        return this.url;
    }

    @Override
    public URI getURI() throws IOException {
        return this.uri != null ? this.uri : super.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return this.uri != null ? super.getFile(this.uri) : super.getFile();
    }

    /**
     * This implementation creates a {@code UrlResource}, applying the given path
     * relative to the path of the underlying URL of this resource descriptor.
     *
     * @see java.net.URL#URL(java.net.URL, String)
     */
    @Override
    public Resource createRelative(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return new UrlResource(new URL(this.url, relativePath));
    }

    @Override
    public String getName() {
        return getName(this.cleanedUrl.getPath());
    }

    @Override
    public String getDescription() {
        return "URL [" + this.url + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof UrlResource && this.cleanedUrl.equals(((UrlResource) obj).cleanedUrl)));
    }

    @Override
    public int hashCode() {
        return this.cleanedUrl.hashCode();
    }

}

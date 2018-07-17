package sakura.common.resource;

import org.apache.commons.lang3.Validate;

import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * {@link Resource} implementation for {@code java.io.File} handles.
 * Supports resolution as a {@code File} and also as a {@code URL}.
 * Implements the extended {@link WritableResource} interface.
 */
public class FileSystemResource extends AbstractResource implements WritableResource {

    private final File file;
    private final String path;

    public FileSystemResource(File file) {
        Validate.notNull(file, "File must not be null");
        this.file = file;
        this.path = cleanPath(file.getPath());
    }

    public FileSystemResource(String path) {
        Validate.notNull(path, "Path must not be null");
        this.file = new File(path);
        this.path = cleanPath(path);
    }

    public final String getPath() {
        return this.path;
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public boolean isReadable() {
        return (this.file.canRead() && !this.file.isDirectory());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public boolean isWritable() {
        return (this.file.canWrite() && !this.file.isDirectory());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this.file);
    }

    @Override
    public URL getURL() throws IOException {
        return this.file.toURI().toURL();
    }

    @Override
    public URI getURI() throws IOException {
        return this.file.toURI();
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public long contentLength() throws IOException {
        return this.file.length();
    }

    @Override
    public Resource createRelative(String relativePath) {
        String pathToUse = applyRelativePath(this.path, relativePath);
        return new FileSystemResource(pathToUse);
    }

    @Override
    public String getName() {
        return this.file.getName();
    }

    @Override
    public String getDescription() {
        return "file [" + this.file.getAbsolutePath() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof FileSystemResource && this.path.equals(((FileSystemResource) obj).path)));
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

}

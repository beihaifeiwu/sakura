package sakura.common.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Created by haomu on 2018/7/16.
 */
public interface Resource {

    boolean exists();
    boolean isReadable();
    boolean isOpen();

    String getName();
    URL getURL() throws IOException;
    URI getURI() throws IOException;
    InputStream getInputStream() throws IOException;

    File getFile() throws IOException;
    long contentLength() throws IOException;
    long lastModified() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    String getDescription();

}

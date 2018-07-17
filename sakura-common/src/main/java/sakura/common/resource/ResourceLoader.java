package sakura.common.resource;

import sakura.common.annotation.Nullable;

import java.io.IOException;

public interface ResourceLoader {

    /**
     * Pseudo URL prefix for loading from the class path: "classpath:"
     */
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    /**
     * Pseudo URL prefix for all matching resources from the class path: "classpath*:"
     * This differs from ResourceLoader's classpath URL prefix in that it
     * retrieves all matching resources for a given name (e.g. "/beans.xml"),
     * for example in the root of all deployed JAR files.
     */
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";


    /**
     * Return a Resource handle for the specified resource location.
     * <p>The handle should always be a reusable resource descriptor,
     * allowing for multiple {@link Resource#getInputStream()} calls.
     * <p><ul>
     * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
     * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
     * <li>Should support relative file paths, e.g. "WEB-INF/test.dat".
     * (This will be implementation-specific, typically provided by an
     * ApplicationContext implementation.)
     * </ul>
     * <p>Note that a Resource handle does not imply an existing resource;
     * you need to invoke {@link Resource#exists} to check for existence.
     *
     * @param location the resource location
     * @return a corresponding Resource handle (never {@code null})
     * @see #CLASSPATH_URL_PREFIX
     * @see Resource#exists()
     * @see Resource#getInputStream()
     */
    @Nullable
    Resource getResource(String location);

    /**
     * Resolve the given location pattern into Resource objects.
     * <p>Overlapping resource entries that point to the same physical
     * resource should be avoided, as far as possible. The result should
     * have set semantics.
     *
     * @param locationPattern the location pattern to resolve
     * @return the corresponding Resource objects
     * @throws IOException in case of I/O errors
     */
    Resource[] getResources(String locationPattern) throws IOException;

    /**
     * Expose the ClassLoader used by this ResourceLoader.
     * <p>Clients which need to access the ClassLoader directly can do so
     * in a uniform manner with the ResourceLoader, rather than relying
     * on the thread context ClassLoader.
     *
     * @return the ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     */
    @Nullable
    ClassLoader getClassLoader();

}
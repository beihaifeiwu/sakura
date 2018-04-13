package sakura.common.vfs;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link VFS} implementation that works with the VFS API provided by JBoss 6.
 */
@Slf4j
public class JBoss6VFS extends VFS {

    /**
     * Flag that indicates if this VFS is valid for the current environment.
     */
    private static Boolean valid;

    static {
        initialize();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public List<String> list(URL url, String path) throws IOException {
        VirtualFile directory;
        directory = VFS.getChild(url);
        if (directory == null) {
            return Collections.emptyList();
        }
        if (!path.endsWith("/")) {
            path += "/";
        }
        List<VirtualFile> children = directory.getChildren();
        List<String> names = new ArrayList<String>(children.size());
        for (VirtualFile vf : children) {
            String relative = vf.getPathNameRelativeTo(directory);
            names.add(path + relative);
        }
        return names;
    }

    /**
     * Find all the classes and methods that are required to access the JBoss 6 VFS.
     */
    private static synchronized void initialize() {
        if (valid == null) {
            // Assume valid. It will get flipped later if something goes wrong.
            valid = true;

            // Look up and verify required classes
            VFS.vfsClass = checkNotNull(getClass("org.jboss.vfs.VFS"));
            VirtualFile.virtualFileClass = checkNotNull(getClass("org.jboss.vfs.VirtualFile"));

            // Look up and verify required methods
            VFS.getChild = checkNotNull(getMethod(VFS.vfsClass, "getChild", URL.class));
            VirtualFile.getChildrenRecursively = checkNotNull(getMethod(VirtualFile.virtualFileClass,
                    "getChildrenRecursively"));
            VirtualFile.getPathNameRelativeTo = checkNotNull(getMethod(VirtualFile.virtualFileClass,
                    "getPathNameRelativeTo", VirtualFile.virtualFileClass));

            // Verify that the API has not changed
            checkReturnType(VFS.getChild, VirtualFile.virtualFileClass);
            checkReturnType(VirtualFile.getChildrenRecursively, List.class);
            checkReturnType(VirtualFile.getPathNameRelativeTo, String.class);
        }
    }

    /**
     * Verifies that the provided object reference is null. If it is null, then this VFS is marked
     * as invalid for the current environment.
     *
     * @param object The object reference to check for null.
     */
    private static <T> T checkNotNull(T object) {
        if (object == null) {
            setInvalid();
        }
        return object;
    }

    private static void checkReturnType(Method method, Class<?> expected) {
        if (method != null && !expected.isAssignableFrom(method.getReturnType())) {
            log.error("Method " + method.getClass().getName() + "." + method.getName()
                    + "(..) should return " + expected.getName() + " but returns " //
                    + method.getReturnType().getName() + " instead.");
            setInvalid();
        }
    }

    /**
     * Mark this {@link VFS} as invalid for the current environment.
     */
    private static void setInvalid() {
        if (JBoss6VFS.valid != null && JBoss6VFS.valid) {
            log.debug("JBoss 6 VFS API is not available in this environment.");
            JBoss6VFS.valid = false;
        }
    }

    /**
     * A class that mimics a tiny subset of the JBoss VirtualFile class.
     */
    static class VirtualFile {
        static Class<?> virtualFileClass;
        static Method   getPathNameRelativeTo;
        static Method   getChildrenRecursively;

        Object virtualFile;

        VirtualFile(Object virtualFile) {
            this.virtualFile = virtualFile;
        }

        String getPathNameRelativeTo(VirtualFile parent) {
            try {
                return invoke(getPathNameRelativeTo, virtualFile, parent.virtualFile);
            } catch (IOException e) {
                // This exception is not thrown by the called method
                log.error("This should not be possible. VirtualFile.getPathNameRelativeTo() threw IOException.");
                return null;
            }
        }

        List<VirtualFile> getChildren() throws IOException {
            List<?> objects = invoke(getChildrenRecursively, virtualFile);
            List<VirtualFile> children = new ArrayList<VirtualFile>(objects.size());
            for (Object object : objects) {
                children.add(new VirtualFile(object));
            }
            return children;
        }
    }

    /**
     * A class that mimics a tiny subset of the JBoss VFS class.
     */
    static class VFS {
        static Class<?> vfsClass;
        static Method   getChild;

        static VirtualFile getChild(URL url) throws IOException {
            Object o = invoke(getChild, vfsClass, url);
            return o == null ? null : new VirtualFile(o);
        }
    }

}

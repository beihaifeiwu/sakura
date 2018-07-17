package sakura.common.resource;

import sakura.common.annotation.Nullable;

/**
 * ClassPathResource that explicitly expresses a context-relative path
 * through implementing the ContextResource interface.
 */
public class ClassPathContextResource extends ClassPathResource implements ContextResource {

    public ClassPathContextResource(String path, @Nullable ClassLoader classLoader) {
        super(path, classLoader);
    }

    @Override
    public String getPathWithinContext() {
        return getPath();
    }

    @Override
    public Resource createRelative(String relativePath) {
        String pathToUse = applyRelativePath(getPath(), relativePath);
        return new ClassPathContextResource(pathToUse, getClassLoader());
    }

}

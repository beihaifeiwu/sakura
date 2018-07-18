package sakura.common.resource;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultResourceLoaderTests {

    private static final String[] CLASSES_IN_ANNOTATION =
            new String[]{"NonNull.class", "NonNullApi.class", "NonNullFields.class", "Nullable.class"};

    private static final String[] TEST_CLASSES_IN_RESOURCE =
            new String[]{"DefaultResourceLoaderTests.class"};

    private static final String[] CLASSES_IN_REACTIVESTREAMS =
            new String[]{"Processor.class", "Publisher.class", "Subscriber.class", "Subscription.class"};

    private DefaultResourceLoader loader = new DefaultResourceLoader();


    @Test(expected = FileNotFoundException.class)
    public void invalidPrefixWithPatternElementInIt() throws IOException {
        loader.getResources("xx**:**/*.xy");
    }

    @Test
    public void singleResourceOnFileSystem() throws IOException {
        Resource[] resources = loader.getResources("sakura/common/resource/DefaultResourceLoaderTests.class");
        assertEquals(1, resources.length);
        assertProtocolAndFilenames(resources, "file", "DefaultResourceLoaderTests.class");
    }

    @Test
    public void singleResourceInJar() throws IOException {
        Resource[] resources = loader.getResources("org/reactivestreams/Publisher.class");
        assertEquals(1, resources.length);
        assertProtocolAndFilenames(resources, "jar", "Publisher.class");
    }

    @Test
    public void classpathStarWithPatternOnFileSystem() throws IOException {
        Resource[] resources = loader.getResources("classpath*:sakura/common/anno*/*.class");
        // Have to exclude Clover-generated class files here,
        // as we might be running as part of a Clover test run.
        List<Resource> noCloverResources = new ArrayList<>();
        for (Resource resource : resources) {
            if (!resource.getName().contains("$__CLOVER_")) {
                noCloverResources.add(resource);
            }
        }
        resources = noCloverResources.toArray(new Resource[0]);
        assertProtocolAndFilenames(resources, "file", CLASSES_IN_ANNOTATION);
    }

    @Test
    public void classpathWithPatternInJar() throws IOException {
        Resource[] resources = loader.getResources("classpath:org/reactivestreams/*.class");
        assertProtocolAndFilenames(resources, "jar", CLASSES_IN_REACTIVESTREAMS);
    }

    @Test
    public void classpathStarWithPatternInJar() throws IOException {
        Resource[] resources = loader.getResources("classpath*:org/reactivestreams/*.class");
        assertProtocolAndFilenames(resources, "jar", CLASSES_IN_REACTIVESTREAMS);
    }

    @Test
    public void rootPatternRetrievalInJarFiles() throws IOException {
        Resource[] resources = loader.getResources("classpath*:*.dtd");
        boolean found = false;
        for (Resource resource : resources) {
            if (resource.getName().equals("Log4j-events.dtd")) {
                found = true;
            }
        }
        assertTrue("Could not find Log4j-events.dtd in the root of the lo4j-core jar", found);
    }


    private void assertProtocolAndFilenames(Resource[] resources,
                                            String protocol, String... filenames) throws IOException {
        assertEquals("Correct number of files found", filenames.length, resources.length);
        for (Resource resource : resources) {
            String actualProtocol = resource.getURL().getProtocol();
            assertEquals(protocol, actualProtocol);
            assertFilenameIn(resource, filenames);
        }
    }

    private void assertFilenameIn(Resource resource, String... filenames) {
        String filename = resource.getName();
        assertTrue(resource + " does not have a filename that matches any of the specified names",
                Arrays.stream(filenames).anyMatch(filename::endsWith));
    }

}
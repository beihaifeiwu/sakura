package sakura.common.resource;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * Unit tests for various {@link Resource} implementations.
 */
public class ResourceTests {

    @Test
    public void testByteArrayResource() throws IOException {
        Resource resource = new ByteArrayResource("testString".getBytes());
        assertTrue(resource.exists());
        assertFalse(resource.isOpen());
        String content = IOUtils.toString(new InputStreamReader(resource.getInputStream()));
        assertEquals("testString", content);
        assertEquals(resource, new ByteArrayResource("testString".getBytes()));
    }

    @Test
    public void testByteArrayResourceWithDescription() throws IOException {
        Resource resource = new ByteArrayResource("testString".getBytes(), "my description");
        assertTrue(resource.exists());
        assertFalse(resource.isOpen());
        String content = IOUtils.toString(new InputStreamReader(resource.getInputStream()));
        assertEquals("testString", content);
        assertTrue(resource.getDescription().contains("my description"));
        assertEquals(resource, new ByteArrayResource("testString".getBytes()));
    }

    @Test
    public void testInputStreamResource() throws IOException {
        InputStream is = new ByteArrayInputStream("testString".getBytes());
        Resource resource = new InputStreamResource(is);
        assertTrue(resource.exists());
        assertTrue(resource.isOpen());
        String content = IOUtils.toString(new InputStreamReader(resource.getInputStream()));
        assertEquals("testString", content);
        assertEquals(resource, new InputStreamResource(is));
    }

    @Test
    public void testInputStreamResourceWithDescription() throws IOException {
        InputStream is = new ByteArrayInputStream("testString".getBytes());
        Resource resource = new InputStreamResource(is, "my description");
        assertTrue(resource.exists());
        assertTrue(resource.isOpen());
        String content = IOUtils.toString(new InputStreamReader(resource.getInputStream()));
        assertEquals("testString", content);
        assertTrue(resource.getDescription().contains("my description"));
        assertEquals(resource, new InputStreamResource(is));
    }

    @Test
    public void testClassPathResource() throws IOException {
        Resource resource = new ClassPathResource("sakura/common/resource/Resource.class");
        doTestResource(resource);
        Resource resource2 = new ClassPathResource("sakura/common/../common/resource/./Resource.class");
        assertEquals(resource, resource2);
        Resource resource3 = new ClassPathResource("sakura/common/").createRelative("../common/resource/./Resource.class");
        assertEquals(resource, resource3);

        // Check whether equal/hashCode works in a HashSet.
        HashSet<Resource> resources = new HashSet<>();
        resources.add(resource);
        resources.add(resource2);
        assertEquals(1, resources.size());
    }

    @Test
    public void testClassPathResourceWithClassLoader() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Resource resource = new ClassPathResource("sakura/common/resource/Resource.class", classLoader);
        doTestResource(resource);
        assertEquals(resource, new ClassPathResource("sakura/common/../common/resource/./Resource.class", classLoader));
    }

    @Test
    public void testClassPathResourceWithClass() throws IOException {
        Resource resource = new ClassPathResource("Resource.class", getClass());
        doTestResource(resource);
        assertEquals(resource, new ClassPathResource("Resource.class", getClass()));
    }

    @Test
    public void testFileSystemResource() throws IOException {
        Resource resource = new FileSystemResource(getClass().getResource("Resource.class").getFile());
        doTestResource(resource);
        assertEquals(new FileSystemResource(getClass().getResource("Resource.class").getFile()), resource);
        Resource resource2 = new FileSystemResource("common/resource/Resource.class");
        assertEquals(resource2, new FileSystemResource("common/../common/resource/./Resource.class"));
    }

    @Test
    public void testUrlResource() throws IOException {
        Resource resource = new UrlResource(getClass().getResource("Resource.class"));
        doTestResource(resource);
        assertEquals(new UrlResource(getClass().getResource("Resource.class")), resource);

        Resource resource2 = new UrlResource("file:common/resource/Resource.class");
        assertEquals(resource2, new UrlResource("file:common/../common/resource/./Resource.class"));

        assertEquals("test.txt", new UrlResource("file:/dir/test.txt?argh").getName());
        assertEquals("test.txt", new UrlResource("file:\\dir\\test.txt?argh").getName());
        assertEquals("test.txt", new UrlResource("file:\\dir/test.txt?argh").getName());
    }

    private void doTestResource(Resource resource) throws IOException {
        assertEquals("Resource.class", resource.getName());
        assertTrue(resource.getURL().getFile().endsWith("Resource.class"));

        Resource relative1 = resource.createRelative("ClassPathResource.class");
        assertEquals("ClassPathResource.class", relative1.getName());
        assertTrue(relative1.getURL().getFile().endsWith("ClassPathResource.class"));
        assertTrue(relative1.exists());
    }

    @Test
    public void testClassPathResourceWithRelativePath() throws IOException {
        Resource resource = new ClassPathResource("dir/");
        Resource relative = resource.createRelative("subdir");
        assertEquals(new ClassPathResource("dir/subdir"), relative);
    }

    @Test
    public void testFileSystemResourceWithRelativePath() throws IOException {
        Resource resource = new FileSystemResource("dir/");
        Resource relative = resource.createRelative("subdir");
        assertEquals(new FileSystemResource("dir/subdir"), relative);
    }

    @Test
    public void testUrlResourceWithRelativePath() throws IOException {
        Resource resource = new UrlResource("file:dir/");
        Resource relative = resource.createRelative("subdir");
        assertEquals(new UrlResource("file:dir/subdir"), relative);
    }

    @Test
    public void testNonFileResourceExists() throws Exception {
        Resource resource = new UrlResource("http://www.baidu.com");
        assertTrue(resource.exists());
    }

    @Test
    public void testAbstractResourceExceptions() throws Exception {
        final String name = "test-resource";

        Resource resource = new AbstractResource() {
            @Override
            public String getDescription() {
                return name;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                throw new FileNotFoundException();
            }
        };

        try {
            resource.getURL();
            fail("FileNotFoundException should have been thrown");
        } catch (FileNotFoundException ex) {
            assertTrue(ex.getMessage().contains(name));
        }
        try {
            resource.getFile();
            fail("FileNotFoundException should have been thrown");
        } catch (FileNotFoundException ex) {
            assertTrue(ex.getMessage().contains(name));
        }
        try {
            resource.createRelative("/testing");
            fail("FileNotFoundException should have been thrown");
        } catch (FileNotFoundException ex) {
            assertTrue(ex.getMessage().contains(name));
        }

        assertThat(resource.getName(), nullValue());
    }

    @Test
    public void testContentLength() throws IOException {
        AbstractResource resource = new AbstractResource() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(new byte[]{'a', 'b', 'c'});
            }

            @Override
            public String getDescription() {
                return "";
            }
        };
        assertThat(resource.contentLength(), is(3L));
    }

    @Test(expected = FileNotFoundException.class)
    public void testInputStreamNotFoundOnFileSystemResource() throws IOException {
        new FileSystemResource(getClass().getResource("Resource.class").getFile()).createRelative("X").getInputStream();
    }

    @Test(expected = FileNotFoundException.class)
    public void testInputStreamNotFoundOnClassPathResource() throws IOException {
        new ClassPathResource("Resource.class", getClass()).createRelative("X").getInputStream();
    }

}
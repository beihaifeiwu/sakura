package sakura.common.lang;

import lombok.Cleanup;
import lombok.val;
import org.junit.Test;
import sakura.common.util.VFSUtils;

import static org.junit.Assert.*;

/**
 * Created by haomu on 2018/4/25.
 */
public class CLSTest {

    @Test
    public void test() {
        val classLoader = CLS.getDefaultClassLoader();
        assertEquals(classLoader, CLS.class.getClassLoader());

        val files = CLS.getResources("META-INF/services/com.fasterxml.jackson.databind.Module");
        files.forEach(System.out::println);
        System.out.println();

        val roots = CLS.getResources("");
        roots.forEach(System.out::println);
    }

    @Test
    public void testVFS() throws Exception {
        val dirs = CLS.getResources("org/apache/commons/vfs2/events");
        assertNotNull(dirs);
        assertTrue(dirs.size() > 0);
        val dir = dirs.iterator().next();
        assertNotNull(dir);

        @Cleanup val file = VFSUtils.getFile(dir);
        assertTrue(file.isFolder());
        val name = file.getName();
        val children = file.getChildren();
        for (val child : children) {
            System.out.println(name.getRelativeName(child.getName()));
        }
    }

    @Test
    public void testFindResources() {
        val xmls = CLS.findResources("**/*.xml");
        xmls.forEach(System.out::println);
        System.out.println();

        val classes = CLS.findResources("sakura/common/lang/*.class");
        classes.forEach(System.out::println);
    }

}

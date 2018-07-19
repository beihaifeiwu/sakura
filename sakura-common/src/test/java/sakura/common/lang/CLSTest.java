package sakura.common.lang;

import com.google.common.primitives.Primitives;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by haomu on 2018/4/25.
 */
public class CLSTest {

    @Test
    public void testPrimitive() {
        for (Class<?> type : Primitives.allWrapperTypes()) {
            assertTrue(CLS.isPrimitiveWrapper(type));
        }
        for (Class<?> type : Primitives.allPrimitiveTypes()) {
            assertFalse(CLS.isPrimitiveWrapper(type));
        }
        assertFalse(CLS.isPrimitiveWrapper(String.class));
        assertFalse(CLS.isPrimitiveWrapper(int.class));

        for (Class<?> type : Primitives.allWrapperTypes()) {
            assertTrue(CLS.isPrimitiveOrWrapper(type));
        }
        for (Class<?> type : Primitives.allPrimitiveTypes()) {
            assertTrue(CLS.isPrimitiveOrWrapper(type));
        }
        assertFalse(CLS.isPrimitiveWrapper(String.class));
        assertFalse(CLS.isPrimitiveWrapper(CLSTest.class));
    }

    @Test
    public void testGetResources() {
        val classLoader = CLS.getDefaultClassLoader();
        assertEquals(classLoader, CLS.class.getClassLoader());

        val files = CLS.getResources("META-INF/services/com.fasterxml.jackson.databind.Module");
        files.forEach(System.out::println);
        System.out.println();

        val roots = CLS.getResources("");
        roots.forEach(System.out::println);
        assertEquals(roots, CLS.getJarRoots(classLoader));
    }

}

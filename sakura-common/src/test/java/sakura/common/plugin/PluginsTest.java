package sakura.common.plugin;

import org.junit.Test;
import sakura.common.AbstractTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by haomu on 2018/5/18.
 */
public class PluginsTest extends AbstractTest {

    @Test
    public void testGetPlugin() {
        Animal dog = Plugins.getPlugin(Animal.class, "dog");
        assertNotNull(dog);
        assertThat(dog, instanceOf(Animal.Dog.class));

        Animal lion = Plugins.getPlugin(Animal.class, "lion");
        assertNotNull(dog);
        assertThat(lion, instanceOf(AnimalFactory.Lion.class));

        Animal elephant = Plugins.getPlugin(Animal.class, "elephant");
        assertNull(elephant);
    }

    @Test
    public void testGetPlugins() {
        List<Animal> plugins = Plugins.getPlugins(Animal.class, false);
        assertNotNull(plugins);
        assertEquals(plugins.size(), 5);
        assertThat(plugins, hasItem(instanceOf(Animal.Dog.class)));

        plugins = Plugins.getPlugins(Animal.class, true);
        assertNotNull(plugins);
        assertEquals(plugins.size(), 5);
        assertThat(plugins.get(0), instanceOf(Animal.Dog.class));
        assertThat(plugins.get(plugins.size() - 1), instanceOf(Animal.Bird.class));

        List<AutoCloseable> closeables = Plugins.getPlugins(AutoCloseable.class, false);
        assertNotNull(closeables);
        assertTrue(closeables.isEmpty());
    }

    @Test
    public void testGetPluginNames() {
        List<String> names = Plugins.getPluginNames(Animal.class);
        assertNotNull(names);
        assertEquals(names.size(), 5);
        assertTrue(names.contains("dog"));
        assertTrue(names.contains("cat"));
        assertTrue(names.contains("bird"));
        assertTrue(names.contains("lion"));
        assertTrue(names.contains("tiger"));

        names = Plugins.getPluginNames(AutoCloseable.class);
        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

}

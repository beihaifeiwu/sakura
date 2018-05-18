package sakura.common.plugin;

import org.junit.Test;
import sakura.common.AbstractTest;
import sakura.common.plugin.factory.SpiPluginLoader;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by haomu on 2018/5/18.
 */
public class SpiPluginLoaderTest extends AbstractTest {

    @Test
    public void testGetPlugins() {
        SpiPluginLoader<Animal> loader = new SpiPluginLoader<>(Animal.class);
        List<String> names = loader.getSupportedPlugins();
        assertThat(names, hasItems("cat", "bird", "dog"));

        Animal cat = loader.getPlugin("cat");
        assertNotNull(cat);
        assertThat(cat, instanceOf(Animal.Cat.class));

        Animal lion = loader.getPlugin("lion");
        assertNull(lion);
    }

}

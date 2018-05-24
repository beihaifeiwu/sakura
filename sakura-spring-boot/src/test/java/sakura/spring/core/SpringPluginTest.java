package sakura.spring.core;

import org.junit.Test;
import sakura.common.plugin.Plugins;
import sakura.common.plugin.factory.PluginFactory;
import sakura.spring.test.SakuraSpringTest;
import sakura.spring.test.TestBean;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by haomu on 2018/5/21.
 */
public class SpringPluginTest extends SakuraSpringTest {

    @Test
    public void testPluginFactory() {
        List<String> names = Plugins.getPluginNames(PluginFactory.class);
        assertNotNull(names);
        assertThat(names, hasItems("spring", "spi"));

        PluginFactory spring = Plugins.getPlugin(PluginFactory.class, "spring");
        assertNotNull(spring);
        assertThat(spring, instanceOf(SpringPluginFactory.class));
    }

    @Test
    public void testPlugin() {
        TestPlugin test = Plugins.getPlugin(TestPlugin.class, "test_plugin");
        assertNotNull(test);
        assertEquals(SpringBeans.getBean("test_plugin").orElse(null), test);
    }

    @TestBean("test_plugin")
    public static class TestPlugin {

    }

}

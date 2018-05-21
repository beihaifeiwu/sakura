package sakura.spring.core;

import sakura.common.lang.OBJ;
import sakura.common.plugin.Plugin;
import sakura.common.plugin.factory.PluginFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by haomu on 2018/5/20.
 */
@Plugin(priority = Plugin.HIGHEST_PRIORITY + 100)
public class SpringPluginFactory implements PluginFactory {

    @Override
    public <T> List<String> getPluginNames(Class<T> type) {
        String[] names = SpringBeans.getBeanNames(type);
        return OBJ.isEmpty(names) ? Collections.emptyList() : Arrays.asList(names);
    }

    @Override
    public <T> List<T> getPlugins(Class<T> type) {
        return SpringBeans.getBeans(type);
    }

    @Override
    public <T> T getPlugin(Class<T> type, String name) {
        return SpringBeans.getBean(name, type).orElse(null);
    }

}

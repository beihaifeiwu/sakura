package sakura.common.plugin.factory;

import lombok.val;
import sakura.common.plugin.Plugins;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haomu on 2018/5/17.
 */
public class PluginFactoryWrapper implements PluginFactory {

    private final List<PluginFactory> pluginFactories;

    public PluginFactoryWrapper() {
        pluginFactories = new ArrayList<>();

        val loader = new SpiPluginLoader<PluginFactory>(PluginFactory.class);
        for (String name : loader.getSupportedPlugins()) {
            PluginFactory plugin = loader.getPlugin(name);
            if (plugin != null) {
                pluginFactories.add(plugin);
            }
        }
        Plugins.sort(pluginFactories);
    }

    @Override
    public <T> List<String> getPluginNames(Class<T> type) {
        val names = new ArrayList<String>();
        for (PluginFactory pluginFactory : pluginFactories) {
            names.addAll(pluginFactory.getPluginNames(type));
        }
        return names;
    }

    @Override
    public <T> List<T> getPlugins(Class<T> type) {
        val plugins = new ArrayList<T>();
        for (PluginFactory pluginFactory : pluginFactories) {
            plugins.addAll(pluginFactory.getPlugins(type));
        }
        return plugins;
    }

    @Override
    public <T> T getPlugin(Class<T> type, String name) {
        T plugin = null;
        for (PluginFactory pluginFactory : pluginFactories) {
            plugin = pluginFactory.getPlugin(type, name);
            if (plugin != null) {
                break;
            }
        }
        return plugin;
    }

}

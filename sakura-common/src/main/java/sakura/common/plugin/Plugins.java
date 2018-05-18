package sakura.common.plugin;

import lombok.val;
import sakura.common.lang.Objects;
import sakura.common.lang.annotation.Nullable;
import sakura.common.plugin.factory.PluginFactoryWrapper;

import java.util.List;

/**
 * Created by haomu on 2018/5/17.
 */
public class Plugins {

    private static final PluginComparator COMPARATOR = new PluginComparator();
    private static final PluginFactoryWrapper FACTORY = new PluginFactoryWrapper();

    @Nullable
    public static <T> T getPlugin(Class<T> type, String name) {
        return FACTORY.getPlugin(type, name);
    }

    public static <T> List<String> getPluginNames(Class<T> type) {
        return FACTORY.getPluginNames(type);
    }

    public static <T> List<T> getPlugins(Class<T> type, boolean sort) {
        val plugins = FACTORY.getPlugins(type);
        if (sort) sort(plugins);
        return plugins;
    }

    public static <T> void sort(List<T> plugins) {
        if (Objects.isNotEmpty(plugins)) {
            plugins.sort(COMPARATOR);
        }
    }

}

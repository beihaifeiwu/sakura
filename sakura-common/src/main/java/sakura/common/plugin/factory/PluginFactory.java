package sakura.common.plugin.factory;

import sakura.common.lang.annotation.Nullable;

import java.util.List;

/**
 * Created by haomu on 2018/5/17.
 */
public interface PluginFactory {

    <T> List<String> getPluginNames(Class<T> type);

    <T> List<T> getPlugins(Class<T> type);

    @Nullable
    <T> T getPlugin(Class<T> type, String name);

}

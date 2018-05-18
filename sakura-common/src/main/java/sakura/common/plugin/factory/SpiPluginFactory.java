package sakura.common.plugin.factory;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import sakura.common.cache.Cache;
import sakura.common.cache.Caches;
import sakura.common.lang.annotation.Nullable;
import sakura.common.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p/>
 * 插件声明配置文件格式：<br/>
 * 以Plugin示例，配置文件META-INF/plugins/com.xxx.Plugin内容：<br/>
 * 由<br/>
 * <pre><code>
 *     com.foo.XxxPlugin
 *     com.foo.YyyPlugin
 * </code></pre><br/>
 * 改成使用KV格式<br/>
 * <pre><code>
 *     xxx=com.foo.XxxPlugin
 *     yyy=com.foo.YyyPlugin
 * </code></pre>
 * <br/>
 * 原因：<br/>
 * 当插件的static字段或方法签名上引用了三方库，
 * 如果三方库不存在，会导致类初始化失败，Plugin标识就拿不到了，异常信息就和配置对应不起来。
 * <br/>
 * 比如:
 * Plugin("mina")加载失败，
 * 当用户配置使用mina时，就会报找不到插件，而不是报加载插件失败，以及失败原因。
 * <p>
 * Created by haomu on 2018/5/17.
 */
@Plugin
@Slf4j
public class SpiPluginFactory implements PluginFactory {

    private Cache<Class<?>, SpiPluginLoader<?>> loaderCache = Caches.newLRUCache(256);

    @Override
    public <T> List<String> getPluginNames(Class<T> type) {
        val loader = getLoader(type);
        return loader == null ? Collections.emptyList() : loader.getSupportedPlugins();
    }

    @Override
    public <T> List<T> getPlugins(Class<T> type) {
        val loader = getLoader(type);
        if (loader == null) return Collections.emptyList();
        val plugins = new ArrayList<T>();
        for (String name : loader.getSupportedPlugins()) {
            T plugin = loader.getPlugin(name);
            if (plugin != null) {
                plugins.add(plugin);
            }
        }
        return plugins;
    }

    @Override
    public <T> T getPlugin(Class<T> type, String name) {
        val loader = getLoader(type);
        return loader == null ? null : loader.getPlugin(name);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T> SpiPluginLoader<T> getLoader(Class<T> type) {
        return (SpiPluginLoader<T>) loaderCache.get(type, SpiPluginLoader::new);
    }

}

package sakura.common.plugin.factory;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;
import sakura.common.annotation.Nullable;
import sakura.common.lang.CLS;
import sakura.common.lang.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haomu on 2018/5/18.
 */
@Slf4j
public class SpiPluginLoader<T> {

    private static final String PLUGINS_DIRECTORY = "META-INF/plugins/";

    private final Lazy<Map<String, Class<? extends T>>> cachedClasses = Lazy.of(this::loadPlugins);

    private final Class<T> type;

    public SpiPluginLoader(Class<T> type) {
        this.type = type;
    }

    public List<String> getSupportedPlugins() {
        return new ArrayList<>(cachedClasses.get().keySet());
    }

    @Nullable
    public T getPlugin(String name) {
        Class<? extends T> clazz = cachedClasses.get().get(name);
        if (clazz != null) {
            try {
                return CLS.newInstance(clazz);
            } catch (Exception e) {
                log.warn("Plugin {} new instance failed!", clazz, e);
            }
        }
        return null;
    }

    private Map<String, Class<? extends T>> loadPlugins() {
        val file = PLUGINS_DIRECTORY + type.getName();
        val urls = CLS.getResources(file);
        val pluginClasses = new HashMap<String, Class<? extends T>>();
        for (URL url : urls) {
            try (InputStream input = url.openStream()) {
                doLoadPlugins(pluginClasses, input);
            } catch (Exception e) {
                log.warn("Load plugins {} from {} failed!", type, url, e);
            }
        }
        return pluginClasses;
    }

    @SuppressWarnings("unchecked")
    private void doLoadPlugins(Map<String, Class<? extends T>> classes, InputStream input) throws IOException {
        List<String> lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
        for (String line : lines) {
            int ci = line.indexOf('#');
            if (ci >= 0) line = line.substring(0, ci).trim();
            if (line.length() <= 0) continue;
            try {
                int i = line.indexOf('=');
                if (i <= 0) continue;
                String name = line.substring(0, i).trim();
                line = line.substring(i + 1).trim();
                Class<? extends T> clazz = (Class<? extends T>) CLS.forName(line);
                if (clazz == null) continue;
                if (!type.isAssignableFrom(clazz)) {
                    throw new IllegalStateException(String.format(
                            "Error when load plugin class(type: %s, class line: %s), class %s is not subtype of plugin type.",
                            type, clazz.getName(), clazz.getName()));
                }
                classes.put(name, clazz);
            } catch (Exception e) {
                log.warn("Parse {} plugin line {} failed!", type, line, e);
            }
        }
    }

}

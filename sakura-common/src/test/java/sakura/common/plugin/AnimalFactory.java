package sakura.common.plugin;

import sakura.common.plugin.factory.PluginFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by haomu on 2018/5/18.
 */
public class AnimalFactory implements PluginFactory {

    @Override
    public <T> List<String> getPluginNames(Class<T> type) {
        if (type.equals(Animal.class)) return Arrays.asList("lion", "tiger");
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getPlugins(Class<T> type) {
        if (type.equals(Animal.class)) {
            List<T> plugins = new ArrayList<>();
            plugins.add((T) new Lion());
            plugins.add((T) new Tiger());
            return plugins;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPlugin(Class<T> type, String name) {
        if (!type.equals(Animal.class)) return null;
        switch (name) {
            case "lion":
                return (T) new Lion();
            case "tiger":
                return (T) new Tiger();
        }
        return null;
    }

    @Plugin(priority = Plugin.LOWEST_PRIORITY - 1000)
    public static class Lion implements Animal {
        @Override
        public String say() {
            return "aao...";
        }
    }

    @Plugin(priority = Plugin.LOWEST_PRIORITY - 2000)
    public static class Tiger implements Animal {
        @Override
        public String say() {
            return "hao...";
        }
    }

}

package sakura.common.plugin;

import sakura.common.annotation.Nullable;

import java.util.Comparator;

/**
 * Created by haomu on 2018/5/18.
 */
public class PluginComparator implements Comparator<Object> {

    @Override
    public int compare(@Nullable Object o1, @Nullable Object o2) {
        int p1 = getPriority(o1);
        int p2 = getPriority(o2);
        return Integer.compare(p1, p2);
    }

    protected int getPriority(@Nullable Object obj) {
        if (obj != null) {
            Integer order = findOrder(obj);
            if (order != null) {
                return order;
            }
        }
        return Plugin.LOWEST_PRIORITY;
    }

    @Nullable
    protected Integer findOrder(Object obj) {
        Plugin plugin = obj.getClass().getAnnotation(Plugin.class);
        return plugin != null ? plugin.priority() : null;
    }

}

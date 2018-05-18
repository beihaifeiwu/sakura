package sakura.common.plugin;

/**
 * Created by haomu on 2018/5/18.
 */
public interface Animal {

    String say();

    @Plugin(priority = Plugin.HIGHEST_PRIORITY)
    class Dog implements Animal {

        @Override
        public String say() {
            return "Wang Wang...";
        }
    }

    @Plugin
    class Cat implements Animal {

        @Override
        public String say() {
            return "Miao ~";
        }
    }

    @Plugin(priority = Plugin.LOWEST_PRIORITY)
    class Bird implements Animal {

        @Override
        public String say() {
            return "$%$^$^...";
        }
    }


}

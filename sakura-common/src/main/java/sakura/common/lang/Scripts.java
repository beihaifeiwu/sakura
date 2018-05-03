package sakura.common.lang;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.Validate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by haomu on 2018/5/3.
 */
@UtilityClass
public class Scripts {

    private static final Lazy<ScriptEngineManager> MANAGER = Lazy.of(ScriptEngineManager::new);

    public static ScriptEngine getEngineByName(String shortName) {
        return MANAGER.get().getEngineByName(shortName);
    }

    public static ScriptEngine getEngineByExtension(String extension) {
        return MANAGER.get().getEngineByExtension(extension);
    }

    public static ScriptEngine getEngineByMimeType(String mimeType) {
        return MANAGER.get().getEngineByMimeType(mimeType);
    }

    @SneakyThrows
    public static Invocable getInvocable(String engineName, String script) {
        val engine = getEngineByName(engineName);
        engine.eval(script);
        Validate.isInstanceOf(Invocable.class, engine);
        return (Invocable) engine;
    }

}

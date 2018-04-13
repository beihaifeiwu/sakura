package sakura.spring.template;

import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liupin on 2017/5/9.
 */
public interface TemplateService {

    @Nullable
    String render(String template, @Nullable Map<String, Object> model) throws IOException;

}

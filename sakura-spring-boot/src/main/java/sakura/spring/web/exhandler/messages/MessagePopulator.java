package sakura.spring.web.exhandler.messages;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by liupin on 2017/6/29.
 */
public interface MessagePopulator {

    <E extends Exception> ErrorMessage createAndPopulate(E ex,
                                                         HttpServletRequest req,
                                                         HttpStatus defaultStatus);

    /**
     * Interpolates the message template using the given variables.
     *
     * @param messageTemplate The message to interpolate.
     * @param variables       Map of variables that will be accessible for the template.
     * @return An interpolated message.
     */
    @Nullable
    String interpolate(String messageTemplate, Map<String, Object> variables);

    @Nullable
    String getMessage(String name);
}

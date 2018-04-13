package sakura.spring.web.param;

import java.lang.annotation.*;

/**
 * Overrides parameter name
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamName {

    /**
     * The name of the request parameter to bind to.
     */
    String value();

}
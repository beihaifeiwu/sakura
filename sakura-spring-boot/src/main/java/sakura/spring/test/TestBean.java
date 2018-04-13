package sakura.spring.test;

import java.lang.annotation.*;

/**
 * Created by haomu on 2018/4/13.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestBean {

    String value() default "";

}

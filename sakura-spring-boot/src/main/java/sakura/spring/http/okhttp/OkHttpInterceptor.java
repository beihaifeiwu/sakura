package sakura.spring.http.okhttp;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A {@link Qualifier} annotation for {@link okhttp3.Interceptor OkHttp3-Interceptors}.
 *
 * @author liupin
 * @see OkHttpNetworkInterceptor
 */
@Target({TYPE, METHOD, FIELD, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Qualifier
public @interface OkHttpInterceptor {
}
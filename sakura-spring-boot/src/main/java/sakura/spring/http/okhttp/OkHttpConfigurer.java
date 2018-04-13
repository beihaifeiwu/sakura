package sakura.spring.http.okhttp;

import okhttp3.OkHttpClient;
import sakura.spring.core.Configurer;

/**
 * Created by liupin on 2017/3/30.
 */
@FunctionalInterface
public interface OkHttpConfigurer extends Configurer<OkHttpClient.Builder> {
    @Override
    void configure(OkHttpClient.Builder builder);
}
package com.github.beihaifeiwu.sakura.http.okhttp;

import com.github.beihaifeiwu.sakura.core.Configurer;
import okhttp3.OkHttpClient;

/**
 * Created by liupin on 2017/3/30.
 */
@FunctionalInterface
public interface OkHttpConfigurer extends Configurer<OkHttpClient.Builder> {
    @Override
    void configure(OkHttpClient.Builder builder);
}
package com.github.beihaifeiwu.sakura.core;

import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by liupin on 2017/9/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTemplateTest {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private OkHttpClient okHttpClient;

    @Test
    public void testRestTemplate() throws IOException {
        assertThat(restTemplateBuilder)
                .isNotNull()
                .extracting("requestFactory")
                .isNotEmpty()
                .extracting("client")
                .containsExactly(okHttpClient);
    }

}

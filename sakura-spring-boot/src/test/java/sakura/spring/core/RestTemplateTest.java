package sakura.spring.core;

import okhttp3.OkHttpClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import sakura.common.http.HTTP;
import sakura.spring.test.SakuraSpringWebTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by liupin on 2017/9/5.
 */
public class RestTemplateTest extends SakuraSpringWebTest {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private OkHttpClient okHttpClient;

    @Test
    public void testRestTemplate() {
        assertThat(restTemplateBuilder).isNotNull();
        assertThat(okHttpClient).isNotNull();
        assertThat(okHttpClient).isEqualTo(HTTP.getOkHttpClient());
    }

}

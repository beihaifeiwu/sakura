package sakura.spring.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

/**
 * Created by haomu on 2018/4/13.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SakuraSpringWebTest extends SakuraSpringTest {

    @LocalServerPort
    protected int port;

}

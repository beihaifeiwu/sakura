package com.github.beihaifeiwu.sakura.jackson;

import com.github.beihaifeiwu.sakura.SakuraTestPlainApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by liupin on 2017/4/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SakuraTestPlainApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class JacksonNonWebEnvironmentTest extends JacksonWebEnvironmentTest {

}

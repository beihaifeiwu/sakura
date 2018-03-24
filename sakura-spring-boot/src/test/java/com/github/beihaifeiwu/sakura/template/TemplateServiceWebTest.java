package com.github.beihaifeiwu.sakura.template;

import com.github.beihaifeiwu.sakura.test.MySpringTestPlainApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by liupin on 2017/5/17.
 */
@SpringBootTest(classes = MySpringTestPlainApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TemplateServiceWebTest extends TemplateServiceNoWebTest {
}

package sakura.spring.template;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sakura.spring.test.SakuraSpringTest;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liupin on 2017/5/9.
 */
public class TemplateNoWebTest extends SakuraSpringTest {

    @Autowired
    private TemplateService templateService;

    @Test
    public void testFreemarker() throws IOException {
        System.out.println("******************** Freemarker *****************");
        Map<String, Object> latestProduct = Maps.newHashMap();
        latestProduct.put("name", "sakura");
        latestProduct.put("url", "https://github.com/beihaifeiwu/sakura");

        Map<String, Object> model = Maps.newHashMap();
        model.put("user", "Freemarker");
        model.put("latestProduct", latestProduct);

        String result = templateService.render("welcome.ftl", model);
        System.out.println(result);
    }

    @Test
    public void testThymeleaf() throws IOException {
        System.out.println("******************** Thymeleaf *****************");
        Map<String, Object> model = Maps.newHashMap();
        model.put("content", "I am using thymeleaf");
        String result = templateService.render("home.html", model);
        System.out.println(result);
    }

    @Test
    public void testVelocity() throws IOException {
        System.out.println("******************** Velocity *****************");
        Map<String, Object> model = Maps.newHashMap();
        model.put("fullName", "沃德天·梅天理·维森莫·拉莫帅·帅徳布耀布耀徳·玛德·甄踏马帅");
        model.put("anotherName", "我的天，没天理，为什么，那么帅，帅的不要不要的，MD，真TM帅");
        String result = templateService.render("profile.vm", model);
        System.out.println(result);
    }

}

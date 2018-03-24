package com.github.beihaifeiwu.sakura.template;

import com.github.beihaifeiwu.sakura.SakuraTestPlainApplication;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liupin on 2017/5/9.
 */
@SpringBootTest(classes = SakuraTestPlainApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TemplateServiceNoWebTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private TemplateService templateService;

    @Test
    public void testBeetl() throws IOException {
        System.out.println("********************** Beetl *******************");
        Map<String, Object> model = Maps.newHashMap();
        model.put("title", "Beetl");
        model.put("greeting", "Hi Beetl");
        String result = templateService.render("greeting.btl", model);
        System.out.println(result);
    }

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

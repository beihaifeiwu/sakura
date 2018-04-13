package sakura.spring.core;

import com.google.common.util.concurrent.Uninterruptibles;
import org.hibernate.validator.HibernateValidatorFactory;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.junit.Test;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import sakura.spring.test.SakuraSpringWebTest;

import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by liupin on 2017/7/21.
 */
public class SpringContextTest extends SakuraSpringWebTest {

    @Test
    public void webRelated() {
        Integer port = SpringContext.serverPort();
        System.out.println("Server port: " + port);
        assertThat(port).isNotNull().isNotEqualTo(0);

        String contextPath = SpringContext.servletContextPath();
        System.out.println("Servlet context path: " + contextPath);
        assertThat(contextPath).isNotNull().isEqualTo("");

        boolean sslEnabled = SpringContext.isSslEnabled();
        System.out.println("Ssl enabled: " + sslEnabled);
        assertThat(sslEnabled).isFalse();

        String baseUrl = SpringContext.baseUrl();
        System.out.println("Base url: " + baseUrl);
        assertThat(baseUrl).isNotEmpty()
                .isEqualTo((sslEnabled ? "https" : "http") + "://localhost:" + port + contextPath);
    }

    @Test
    public void autoConfigureRelated() {
        List<String> basePackages = SpringContext.basePackages();
        System.out.println("Base packages: " + basePackages);
//        assertThat(basePackages)
//                .isNotEmpty()
//                .contains(ClassUtils.getPackageName(CoreTestApplication.class));
    }

    @Test
    public void propertyRelated() {
        String[] activeProfiles = SpringContext.activeProfiles();
        System.out.println("Active profiles: " + Arrays.toString(activeProfiles));
        assertThat(activeProfiles).isNotEmpty().contains("default");

        String template = "${spring.jackson.default-property-inclusion}";
        String resolvedPlaceHolder = SpringContext.resolvePlaceHolder(template);
        System.out.println("Resolve placeholder " + template + " : " + resolvedPlaceHolder);
        assertThat(resolvedPlaceHolder).isNotEmpty().isEqualTo("non_null");
    }

    @Test
    public void beanValidation() {
        ValidatorFactory validatorFactory = SpringContext.validatorFactory();
        System.out.println("ValidatorFactory: " + validatorFactory);
        assertThat(validatorFactory)
                .isNotNull()
                .isInstanceOf(LocalValidatorFactoryBean.class);
        assertThat(validatorFactory.unwrap(HibernateValidatorFactory.class))
                .isNotNull()
                .isInstanceOf(ValidatorFactoryImpl.class);

        Validator validator = SpringContext.springValidator();
        System.out.println("Validator: " + validator);
        assertThat(validator).isNotNull().isEqualTo(validatorFactory);
    }

    @Test
    public void getResource() {
        Resource resource = SpringContext.getResource("classpath:/banner.txt");
        assertThat(resource)
                .isNotNull()
                .isInstanceOf(ClassPathResource.class)
                .extracting(Resource::getFilename)
                .containsExactly("banner.txt");

        Resource[] resources = SpringContext.getResources("classpath*:/application-*.properties");
        assertThat(resources)
                .isNotEmpty()
                .allMatch(Resource::exists)
                .anySatisfy(Resource::isFile)
                .extracting(Resource::getFilename)
                .allSatisfy(r -> StringUtils.startsWithIgnoreCase(r, "application-"))
                .contains("application-data.properties", "application-security.properties");
    }

    @Test
    public void eventPublish() {
        SpringContext.publishEvent("One");
        SpringContext.publishEvent("Two");
        SpringContext.publishEvent("Three");
    }

    @Test
    public void taskSchedule() throws Exception {
        CountDownLatch l1 = new CountDownLatch(2);
        SpringContext.execute(() -> print(l1, "execute"));
        SpringContext.submitListenable(() -> print(l1, "submitListenable task"))
                .addCallback(o -> print("submitListenable success"), o -> print("submitListenable failed"));
        Uninterruptibles.awaitUninterruptibly(l1);

        SpringContext.submitListenable(() -> {
            print("submitListenable callable");
            return "result";
        }).get();

        CountDownLatch l2 = new CountDownLatch(3);
        SpringContext.schedule(() -> print(l2, "schedule trigger"), ctx -> ctx.lastCompletionTime() == null ? new Date() : null);
        SpringContext.scheduleOnce(() -> print(l2, "schedule date"), new Date());
        SpringContext.scheduleOnce(() -> print(l2, "schedule time"), 10);
        Uninterruptibles.awaitUninterruptibly(l2);

        List<ScheduledFuture<?>> futures = new ArrayList<>();
        ScheduledFuture<?> future = SpringContext.scheduleAtFixedRate(() -> print("scheduleAtFixedRate1"), new Date(), 10);
        futures.add(future);
        future = SpringContext.scheduleAtFixedRate(() -> print("scheduleAtFixedRate2"), 10);
        futures.add(future);
        future = SpringContext.scheduleWithFixedDelay(() -> print("scheduleWithFixedDelay1"), new Date(), 10);
        futures.add(future);
        future = SpringContext.scheduleWithFixedDelay(() -> print("scheduleWithFixedDelay2"), 10);
        futures.add(future);

        Uninterruptibles.sleepUninterruptibly(20, TimeUnit.MILLISECONDS);
        futures.forEach(f -> f.cancel(false));
        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.MILLISECONDS);
    }

    private void print(String info) {
        print(null, info);
    }

    private void print(@Nullable CountDownLatch latch, String info) {
        System.out.format("[%d][%12s] %s\n", System.currentTimeMillis(), Thread.currentThread().getName(), info);
        if (latch != null) {
            latch.countDown();
        }
    }

    public static class EventListenerDemo {

        @EventListener
        public void onString(String event) {
            System.out.println("Received event: " + event);
            assertThat(event).isNotBlank()
                    .isIn("One", "Two", "Three");
        }

    }

}

package sakura.spring.core;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.Test;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import sakura.spring.test.SakuraSpringTest;
import sakura.spring.test.TestBean;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by liupin on 2017/2/6.
 */
public class AsyncFeatureTest extends SakuraSpringTest {

    @Test
    public void testAsync() throws Exception {
        AsyncTestDemo bean = SpringBeans.getBean(AsyncTestDemo.class).orElse(null);
        assertNotNull(bean);
        bean.errorTask();
        String threadName = bean.getThreadName().get();
        assertNotNull(threadName);
        assertTrue(threadName.startsWith("executor"));
    }

    @TestBean
    public static class AsyncTestDemo {

        @Async
        public Future<String> getThreadName() {
            Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
            return new AsyncResult<>(Thread.currentThread().getName());
        }

        @Async
        public void errorTask() {
            throw new RuntimeException("I am thrown from " + this);
        }

    }

}

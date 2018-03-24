package com.github.beihaifeiwu.sakura.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by liupin on 2017/2/6.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AsyncFeatureTest {

    @Test
    public void testAsync() throws ExecutionException, InterruptedException {
        AsyncTestDemo bean = SpringBeans.getBean(AsyncTestDemo.class).orElse(null);
        assertNotNull(bean);
        bean.errorTask();
        String threadName = bean.getThreadName().get();
        assertNotNull(threadName);
        assertTrue(threadName.startsWith("executor"));
    }

    public static class AsyncTestDemo {

        @Async
        @Nullable
        public Future<String> getThreadName() {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                return new AsyncResult<>(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Async
        public void errorTask() {
            throw new RuntimeException("I am thrown from " + this);
        }
    }

}

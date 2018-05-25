package sakura.common.lang;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ThreadUtils;
import sakura.common.lang.annotation.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.*;

/**
 * Created by liupin on 2016/12/9.
 */
@Slf4j
@UtilityClass
public class Threads {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static int processors() {
        return AVAILABLE_PROCESSORS;
    }

    public static String currentName() {
        return Thread.currentThread().getName();
    }

    // ThreadFactory/ThreadPool
    // ----------------------------------------------------------------------

    public static ThreadFactory newFactory(String name) {
        val builder = new ThreadFactoryBuilder();
        builder.setDaemon(true);
        builder.setUncaughtExceptionHandler((t, e) -> log.error("Caught an Exception in {}", t, e));
        builder.setNameFormat(name + "-%d");
        return builder.build();
    }

    public static ExecutorService newDirect() {
        return MoreExecutors.newDirectExecutorService();
    }

    public static ExecutorService newSingle(String name) {
        val factory = newFactory(name);
        return Executors.newSingleThreadExecutor(factory);
    }

    public static ExecutorService newElastic(String name, int max) {
        val factory = newFactory(name);
        val size = max < 0 ? Integer.MAX_VALUE : max;
        val queue = new SynchronousQueue<Runnable>();
        val handler = new ThreadPoolExecutor.CallerRunsPolicy();
        return new ThreadPoolExecutor(0, size, 60L, TimeUnit.SECONDS, queue, factory, handler);
    }

    public static ExecutorService newParallel(String name, int parallelism) {
        val size = Math.max(parallelism, 2);
        val queueSize = Math.max(16, 2 * size);
        val factory = newFactory(name);
        val queue = new LinkedBlockingDeque<Runnable>(queueSize);
        val handler = new ThreadPoolExecutor.AbortPolicy();
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, queue, factory, handler);
    }

    public static ScheduledExecutorService newScheduled(String name, int min) {
        val factory = newFactory(name);
        val core = Math.max(1, min);
        return Executors.newScheduledThreadPool(core, factory);
    }

    // Shutdown
    // ----------------------------------------------------------------------

    public static void shutdown(ExecutorService service, long timeout, TimeUnit unit) {
        MoreExecutors.shutdownAndAwaitTermination(service, timeout, unit);
    }

    public static void shutdown(@Nullable ExecutorService... executors) {
        if (OBJ.isEmpty(executors)) return;

        for (ExecutorService executor : executors) {
            if (executor == null) continue;
            if (executor.isTerminated()) continue;
            executor.shutdown();
        }

        for (ExecutorService executor : executors) {
            if (executor == null) continue;
            if (executor.isTerminated()) continue;
            shutdown(executor, 5, TimeUnit.SECONDS);
        }
    }

    // Sleep
    // ----------------------------------------------------------------------

    public static void sleepQuietly(Duration duration) {
        sleepQuietly(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    public static void sleepQuietly(long sleepFor, @Nullable TimeUnit unit) {
        if (sleepFor <= 0) return;
        if (unit == null) unit = TimeUnit.MILLISECONDS;
        try {
            unit.sleep(sleepFor);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sleepDeadly(Duration duration) {
        sleepDeadly(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    public static void sleepDeadly(long sleepFor, @Nullable TimeUnit unit) {
        if (sleepFor <= 0) return;
        if (unit == null) unit = TimeUnit.MILLISECONDS;
        Uninterruptibles.sleepUninterruptibly(sleepFor, unit);
    }

    // find
    // ----------------------------------------------------------------------

    public static Collection<Thread> find(String threadName, String groupName) {
        return ThreadUtils.findThreadsByName(threadName, groupName);
    }

}

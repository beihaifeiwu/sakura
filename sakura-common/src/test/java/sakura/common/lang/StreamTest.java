package sakura.common.lang;

import lombok.val;
import org.junit.Test;
import sakura.common.AbstractTest;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by haomu on 2018/5/18.
 */
public class StreamTest extends AbstractTest {

    @Test
    public void testParallel() {
        val data = new HashSet<Integer>();
        for (int i = 0; i < 10; i++) {
            data.add(i);
        }

        data.parallelStream()
                .map(s -> {
                    Threads.sleepDeadly(200, TimeUnit.MILLISECONDS);
                    return Threads.currentName() + ": " + s;
                })
                .forEach(System.out::println);
    }

}

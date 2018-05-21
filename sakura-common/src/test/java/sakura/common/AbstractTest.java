package sakura.common;

import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import sakura.common.lang.TIME;

/**
 * Created by haomu on 2018/5/18.
 */
public abstract class AbstractTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public final Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void succeeded(long nanos, Description description) {
            logInfo(description, "succeeded", nanos);
        }

        @Override
        protected void failed(long nanos, Throwable e, Description description) {
            logInfo(description, "failed", nanos);
        }

        @Override
        protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
            logInfo(description, "skipped", nanos);
        }
    };

    private static void logInfo(Description description, String status, long nanos) {
        String testName = description.getMethodName();
        System.out.println(String.format("Test %s %s, spent %s", testName, status, TIME.humanReadable(nanos)));
    }

}

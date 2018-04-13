package sakura.common.lang;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Created by haomu on 2018/4/12.
 */
@UtilityClass
public class Time {

    public static int dayOfYear() {
        return LocalDate.now().getDayOfYear();
    }

    public static int dayOfMonth() {
        return LocalDate.now().getDayOfMonth();
    }

    public static long seconds() {
        return Instant.now().getEpochSecond();
    }

    public static long milliseconds() {
        return System.currentTimeMillis();
    }

    public static long nanoseconds() {
        return System.nanoTime();
    }

}

package sakura.common.lang;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

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

    public static String abbreviate(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "\u03bcs"; // μs
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new AssertionError();
        }
    }

    public static TimeUnit chooseUnit(long nanos) {
        if (DAYS.convert(nanos, NANOSECONDS) > 0) return DAYS;
        if (HOURS.convert(nanos, NANOSECONDS) > 0) return HOURS;
        if (MINUTES.convert(nanos, NANOSECONDS) > 0) return MINUTES;
        if (SECONDS.convert(nanos, NANOSECONDS) > 0) return SECONDS;
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) return MILLISECONDS;
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) return MICROSECONDS;
        return NANOSECONDS;
    }

    public static TimeUnit chooseUnit(long time, TimeUnit unit) {
        return chooseUnit(unit.toNanos(time));
    }

    public static String humanReadable(long nanos) {
        TimeUnit unit = chooseUnit(nanos);
        double value = (double) nanos / NANOSECONDS.convert(1, unit);
        return String.format(Locale.ROOT, "%.4g", value) + " " + abbreviate(unit);
    }

    public static String humanReadable(long time, TimeUnit unit) {
        return humanReadable(unit.toNanos(time));
    }

}

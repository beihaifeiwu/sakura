package sakura.common.lang;

import lombok.experimental.UtilityClass;
import sakura.common.lang.exception.UncheckedException;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Created by liupin on 2017/9/14.
 */
@UtilityClass
public class EX {

    public static RuntimeException rethrow(Throwable t) {
        // Clients will not expect needing to handle this.
        if (t instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        throw unchecked(t);
    }

    public static RuntimeException unchecked(Throwable t) {
        if (t instanceof Error) throw (Error) t;
        if (t instanceof RuntimeException) return (RuntimeException) t;
        if (t instanceof IOException) return new UncheckedIOException((IOException) t);
        return new UncheckedException(t);
    }

    public static RuntimeException unchecked(String format, Object... args) {
        return unchecked(null, format, args);
    }

    public static RuntimeException unchecked(Throwable cause, String format, Object... args) {
        String message = format;
        if (format != null && args != null && args.length > 0) {
            message = String.format(format, args);
        }
        if (message == null && cause == null) return new UncheckedException();
        if (message == null) return unchecked(cause);
        if (cause == null) return new UncheckedException(message);
        return new UncheckedException(message, cause);
    }

}

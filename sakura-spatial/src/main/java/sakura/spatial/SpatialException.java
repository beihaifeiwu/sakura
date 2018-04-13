package sakura.spatial;

/**
 * Created by liupin on 2017/5/18.
 */
public class SpatialException extends RuntimeException {

    public SpatialException() {
        super();
    }

    public SpatialException(String message) {
        super(message);
    }

    public SpatialException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpatialException(Throwable cause) {
        super(cause);
    }

    protected SpatialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

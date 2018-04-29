package sakura.common.lang.exception;

/**
 * Created by liupin on 2017/6/9.
 */
public class UncheckedException extends jodd.exception.UncheckedException {

    public UncheckedException() {
        super();
    }

    public UncheckedException(String message) {
        super(message);
    }

    public UncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedException(Throwable cause) {
        super(cause);
    }
}
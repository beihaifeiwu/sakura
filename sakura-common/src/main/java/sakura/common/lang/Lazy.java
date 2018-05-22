package sakura.common.lang;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.jooq.lambda.fi.util.function.CheckedSupplier;

import java.io.Serializable;

/**
 * Created by liupin on 2017/6/9.
 */
public final class Lazy<T> {

    private static final NotInit NOT_INIT = new NotInit();

    @SuppressWarnings("unchecked")
    private volatile T object = (T) NOT_INIT;

    private final CheckedSupplier<T> supplier;

    private Lazy(@NonNull CheckedSupplier<T> supplier) {
        this.supplier = supplier;
    }

    @SneakyThrows
    public T get() {
        // use a temporary variable to reduce the number of reads of the
        // volatile field
        T result = object;

        if (result == NOT_INIT) {
            synchronized (this) {
                result = object;
                if (result == NOT_INIT) {
                    result = supplier.get();
                    object = result;
                }
            }
        }

        return result;
    }

    public static <V> Lazy<V> of(@NonNull CheckedSupplier<V> supplier) {
        return new Lazy<>(supplier);
    }

    private static class NotInit implements Serializable {

        private static final long serialVersionUID = 7092611880189329093L;

        private NotInit() {
            super();
        }

        private Object readResolve() {
            return NOT_INIT;
        }
    }

}

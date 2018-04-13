package sakura.common.lang;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.jooq.lambda.fi.util.function.CheckedSupplier;

/**
 * Created by liupin on 2017/6/9.
 */
public final class Lazy<T> {

    private static final Object NO_INIT = new Object();

    @SuppressWarnings("unchecked")
    private volatile T object = (T) NO_INIT;

    private final CheckedSupplier<T> supplier;

    private Lazy(@NonNull CheckedSupplier<T> supplier) {
        this.supplier = supplier;
    }

    @SneakyThrows
    public T get() {
        // use a temporary variable to reduce the number of reads of the
        // volatile field
        T result = object;

        if (result == NO_INIT) {
            synchronized (this) {
                result = object;
                if (result == NO_INIT) {
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
}

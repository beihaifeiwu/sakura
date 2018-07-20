package sakura.common.lang.iterator;

import com.google.common.collect.AbstractIterator;
import sakura.common.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by haomu on 2018/5/4.
 */
public abstract class AbstractBatchIterator<T> extends AbstractIterator<T> {

    private static final Iterable END_MARKER = new ArrayList(0);

    private Iterator<T> iterator;

    @Override
    protected final T computeNext() {
        while (iterator == null || !iterator.hasNext()) {
            Iterable<T> data = computeNextBatch();

            if (END_MARKER == data) {
                return endOfData();
            }

            if (data != null) {
                iterator = data.iterator();
            }
        }
        return iterator.next();
    }

    @SuppressWarnings("unchecked")
    protected final Iterable<T> endOfBatchData() {
        return END_MARKER;
    }

    @Nullable
    protected abstract Iterable<T> computeNextBatch();

}

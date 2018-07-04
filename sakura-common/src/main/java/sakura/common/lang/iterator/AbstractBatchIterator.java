package sakura.common.lang.iterator;

import com.google.common.collect.AbstractIterator;
import sakura.common.lang.Objects;
import sakura.common.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by haomu on 2018/5/4.
 */
public abstract class AbstractBatchIterator<T> extends AbstractIterator<T> {

    private static final Collection END_MARKER = new ArrayList(0);

    private Iterator<T> iterator;

    @Override
    protected final T computeNext() {
        if (iterator == null || !iterator.hasNext()) {
            Collection<T> data = computeNextBatch();

            while (END_MARKER != data && Objects.isEmpty(data)) {
                data = computeNextBatch();
            }

            if (END_MARKER == data) {
                return endOfData();
            }

            iterator = data.iterator();
        }
        return iterator.next();
    }

    @SuppressWarnings("unchecked")
    protected final Collection<T> endOfBatchData() {
        return END_MARKER;
    }

    @Nullable
    protected abstract Collection<T> computeNextBatch();

}

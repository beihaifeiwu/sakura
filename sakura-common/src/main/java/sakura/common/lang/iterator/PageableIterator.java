package sakura.common.lang.iterator;

import sakura.common.lang.OBJ;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * Created by haomu on 2018/5/20.
 */
public class PageableIterator<I, T> extends AbstractBatchIterator<T> {

    private final BiFunction<I, Integer, Collection<T>> fetcher;
    private final BiFunction<Collection<T>, I, I> selector;

    private I start;
    private int pageSize;

    public PageableIterator(BiFunction<I, Integer, Collection<T>> fetcher,
                            BiFunction<Collection<T>, I, I> selector, I start, int pageSize) {
        this.fetcher = fetcher;
        this.selector = selector;
        this.start = start;
        this.pageSize = pageSize;
    }

    @Override
    protected Collection<T> computeNextBatch() {
        Collection<T> values = fetcher.apply(start, pageSize);
        if (OBJ.isEmpty(values)) {
            return endOfBatchData();
        }
        start = selector.apply(values, start);
        return values;
    }

}

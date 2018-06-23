package sakura.common.string;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.AbstractIterator;
import sakura.common.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by haomu on 2018/6/22.
 */
public class StringSplitter {

    private Predicate<Character> trimmer;
    private boolean preserveAll;
    private int limit;
    private Strategy strategy;

    public StringSplitter on(char separator) {
        return on(CharMatcher.is(separator));
    }

    public StringSplitter on(Predicate<Character> separator) {
        CharMatcher separatorMatcher = toMatcher(separator);
        this.strategy = (splitter, toSplit) -> new SplittingIterator(splitter, toSplit) {
            @Override
            int separatorStart(int start) {
                return separatorMatcher.indexIn(toSplit, start);
            }

            @Override
            int separatorEnd(int separatorPosition) {
                return separatorPosition + 1;
            }
        };
        return this;
    }

    public StringSplitter on(String separator) {
        if (separator.length() == 1) return on(separator.charAt(0));
        this.strategy = (splitter, toSplit) -> new SplittingIterator(splitter, toSplit) {
            @Override
            public int separatorStart(int start) {
                int separatorLength = separator.length();
                positions:
                for (int p = start, last = toSplit.length() - separatorLength; p <= last; p++) {
                    for (int i = 0; i < separatorLength; i++) {
                        if (toSplit.charAt(i + p) != separator.charAt(i)) {
                            continue positions;
                        }
                    }
                    return p;
                }
                return -1;
            }

            @Override
            public int separatorEnd(int separatorPosition) {
                return separatorPosition + separator.length();
            }
        };
        return this;
    }

    public StringSplitter trim() {
        this.trimmer = CharMatcher.whitespace();
        return this;
    }

    public StringSplitter trim(Predicate<Character> trimmer) {
        this.trimmer = trimmer;
        return this;
    }

    public StringSplitter preserveAll() {
        this.preserveAll = true;
        return this;
    }

    public StringSplitter limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Iterable<String> split(@Nullable CharSequence sequence) {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return splittingIterator(sequence);
            }

            @Override
            public String toString() {
                return Joiner.on(", ")
                        .appendTo(new StringBuilder().append('['), this)
                        .append(']')
                        .toString();
            }
        };
    }

    public List<String> splitToList(@Nullable CharSequence sequence) {
        Iterator<String> iterator = splittingIterator(sequence);
        List<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public String[] splitToArray(@Nullable CharSequence sequence) {
        List<String> result = splitToList(sequence);
        return result.toArray(new String[0]);
    }

    public String[] split(@Nullable String str) {
        List<String> tokens = splitToList(str);
        return tokens.toArray(new String[0]);
    }

    private Iterator<String> splittingIterator(@Nullable CharSequence sequence) {
        if (sequence == null) return Collections.emptyIterator();
        if (trimmer == null) trim(CharMatcher.none());
        if (strategy == null) on(CharMatcher.whitespace());
        return strategy.iterator(this, sequence);
    }

    private CharMatcher toMatcher(Predicate<Character> separator) {
        CharMatcher charMatcher;
        if (separator instanceof CharMatcher) {
            charMatcher = (CharMatcher) separator;
        } else {
            charMatcher = new CharMatcher() {
                @Override
                public boolean matches(char c) {
                    return separator.test(c);
                }
            };
        }
        return charMatcher;
    }

    private interface Strategy {
        Iterator<String> iterator(StringSplitter splitter, CharSequence toSplit);
    }

    private static abstract class SplittingIterator extends AbstractIterator<String> {

        final CharSequence toSplit;
        final Predicate<Character> trimmer;
        final boolean preserveAll;

        /**
         * Returns the first index in {@code toSplit} at or after {@code start} that contains the
         * separator.
         */
        abstract int separatorStart(int start);

        /**
         * Returns the first index in {@code toSplit} after {@code separatorPosition} that does not
         * contain a separator. This method is only invoked after a call to {@code separatorStart}.
         */
        abstract int separatorEnd(int separatorPosition);

        int offset = 0;
        int limit;

        protected SplittingIterator(StringSplitter splitter, CharSequence toSplit) {
            this.toSplit = toSplit;
            this.trimmer = splitter.trimmer;
            this.preserveAll = splitter.preserveAll;
            this.limit = splitter.limit;
        }

        @Override
        protected String computeNext() {
            /*
             * The returned string will be from the end of the last match to the beginning of the next
             * one. nextStart is the start position of the returned substring, while offset is the place
             * to start looking for a separator.
             */
            int nextStart = offset;
            while (offset != -1) {
                int start = nextStart;
                int end;

                int separatorPosition = separatorStart(offset);
                if (separatorPosition == -1) {
                    end = toSplit.length();
                    offset = -1;
                } else {
                    end = separatorPosition;
                    offset = separatorEnd(separatorPosition);
                }
                if (offset == nextStart) {
                    /*
                     * This occurs when some pattern has an empty match, even if it doesn't match the empty
                     * string -- for example, if it requires lookahead or the like. The offset must be
                     * increased to look for separators beyond this point, without changing the start position
                     * of the next returned substring -- so nextStart stays the same.
                     */
                    offset++;
                    if (offset > toSplit.length()) {
                        offset = -1;
                    }
                    continue;
                }

                while (start < end && trimmer.test(toSplit.charAt(start))) {
                    start++;
                }
                while (end > start && trimmer.test(toSplit.charAt(end - 1))) {
                    end--;
                }

                if (!preserveAll && start == end) {
                    // Don't include the (unused) separator in next split string.
                    nextStart = offset;
                    continue;
                }

                if (limit == 1) {
                    // The limit has been reached, return the rest of the string as the
                    // final item. This is tested after empty string removal so that
                    // empty strings do not count towards the limit.
                    end = toSplit.length();
                    offset = -1;
                    // Since we may have changed the end, we need to trim it again.
                    while (end > start && trimmer.test(toSplit.charAt(end - 1))) {
                        end--;
                    }
                } else {
                    limit--;
                }

                return toSplit.subSequence(start, end).toString();
            }
            return endOfData();
        }
    }
}

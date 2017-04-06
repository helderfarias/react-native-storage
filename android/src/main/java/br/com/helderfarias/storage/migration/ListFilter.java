package br.com.helderfarias.storage.migration;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

public final class ListFilter<T> extends AbstractList<T> {

    private final List<T> origin;

    public ListFilter(@NonNull final List<T> source, @NonNull final Predicate<T> predicate) {
        origin = filter(source, predicate);
    }

    @Override
    public T get(int index) {
        return origin.get(index);
    }

    @Override
    public int size() {
        return origin.size();
    }

    private static <T> List<T> filter(
        @NonNull final List<T> origin,
        @NonNull final Predicate<T> predicate
    ) {
        final List<T> res = new LinkedList<>();
        for (T item : origin) {
            if (predicate.apply(item)) {
                res.add(item);
            }
        }
        return res;
    }
}

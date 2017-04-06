package br.com.helderfarias.storage.migration;

import android.support.annotation.NonNull;
import java.util.Enumeration;
import java.util.Iterator;

public final class IterableEnum<T> implements Enumeration<T> {

    private final Iterator<? extends T> iterator;

    private IterableEnum(@NonNull final Iterator<? extends T> iterator) {
        this.iterator = iterator;
    }

    public IterableEnum(@NonNull final Iterable<? extends T> iterable) {
        this(iterable.iterator());
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public T nextElement() {
        return iterator.next();
    }
}

package br.com.helderfarias.storage.migration;

import android.support.annotation.NonNull;

import com.android.internal.util.Predicate;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class MigrationsFilter implements Iterable<MigrationFile> {

    private final List<MigrationFile> origin;

    MigrationsFilter(
        @NonNull final Iterable<? extends MigrationFile> origin,
        final int versionStart,
        final int versionEnd
    ) {
        this.origin = new ListFilter<>(
            Collections.list(new IterableEnum<>(origin)),
            new MigrationFileVersionPredicate(versionStart, versionEnd)
        );
    }

    @Override
    public Iterator<MigrationFile> iterator() {
        return origin.iterator();
    }

    private static final class MigrationFileVersionPredicate implements Predicate<MigrationFile> {

        private final int vStart;
        private final int vEnd;

        private MigrationFileVersionPredicate(int vStart, int vEnd) {
            this.vStart = vStart;
            this.vEnd = vEnd;
        }

        @Override
        public boolean apply(MigrationFile migrationFile) {
            return apply(migrationFile.version());
        }

        private boolean apply(final int version) {
            return version > vStart && version <= vEnd;
        }
    }

}

package br.com.helderfarias.storage.migration;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class OrderedByVersionFiles implements Iterable<MigrationFileAsset> {

    @SuppressWarnings("Convert2Lambda")
    private static final Comparator<MigrationFileAsset> COMPARATOR
        = new Comparator<MigrationFileAsset>() {
        @Override
        public int compare(MigrationFileAsset o1, MigrationFileAsset o2) {
            return o1.version() - o2.version();
        }
    };

    private final List<MigrationFileAsset> files;

    OrderedByVersionFiles(
        @NonNull final AssetManager assets,
        @NonNull final String folder
    ) throws IOException {
        files = orderByVersion(
            toMigrationFiles(
                assets,
                folder,
                Arrays.asList(assets.list(folder))
            )
        );
    }

    @Override
    public Iterator<MigrationFileAsset> iterator() {
        return files.iterator();
    }

    private static List<MigrationFileAsset> orderByVersion(final List<MigrationFileAsset> files) {
        final List<MigrationFileAsset> out = new ArrayList<>(files);
        Collections.sort(out, COMPARATOR);
        return out;
    }

    private static List<MigrationFileAsset> toMigrationFiles(
        final AssetManager assets,
        final String folder,
        final List<String> names
    ) {
        List<MigrationFileAsset> files = new LinkedList<>();
        for (String name : names) {
            files.add(new MigrationFileAsset(assets, new File(folder, name)));
        }
        return files;
    }
}

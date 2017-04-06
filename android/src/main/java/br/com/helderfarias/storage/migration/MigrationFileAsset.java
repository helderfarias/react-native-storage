package br.com.helderfarias.storage.migration;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import br.com.helderfarias.storage.util.FileUtils;

final class MigrationFileAsset implements MigrationFile {

    private final int version;
    private final AssetManager assets;
    private final File file;

    MigrationFileAsset(@NonNull final AssetManager assets, @NonNull final File file) {
        this.version = version(file.getName());
        this.assets = assets;
        this.file = file;
    }

    public int version() {
        return version;
    }

    public List<Migration> migrations() throws IOException {
        final InputStream stream = assets.open(file.getPath());
        try {
            return migrations(FileUtils.toString(stream));
        } finally {
            stream.close();
        }
    }

    private static List<Migration> migrations(@NonNull final String content) {
        final LinkedList<Migration> migrations = new LinkedList<>();
        for (String stm : content.split(";")) {
            final String line = stm.trim();
            if (line.length() > 0) {
                migrations.addLast(new Migration(line));
            }
        }
        return migrations;
    }

    private static int version(String name) {
        final int s = name.indexOf('.');
        final String base = name.substring(0, s > 0 ? s : name.length());
        return Integer.parseInt(base);
    }
}

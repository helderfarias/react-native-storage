package br.com.helderfarias.storage.migration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

public final class SQLiteMigrations {

    private static final String DEFAULT_FOLDER = "migrations";

    private static final String LTAG = SQLiteMigrations.class.getName();

    private final Context context;
    private final String folder;

    /**
     * New migrations from default assets folder.
     *
     * @param context current application context.
     */
    public SQLiteMigrations(
        @NonNull final Context context
    ) {
        this(context, DEFAULT_FOLDER);
    }

    /**
     * New migrations from custom assets folder.
     *
     * @param context current application content.
     * @param folder  custom folder.
     */
    public SQLiteMigrations(
        @NonNull final Context context,
        @NonNull final String folder
    ) {
        this.context = context;
        this.folder = folder;
    }

    /**
     * Apply migrations to provided database.
     *
     * @param database where migrations will be applied.
     * @param from     current db version
     * @param to       new db version
     * @throws MigrationException if migrations failed.
     */
    public void apply(
        @NonNull final SQLiteDatabase database,
        final int from,
        final int to
    ) throws MigrationException {
        Log.i(LTAG, String.format("Apply migration: %d -> %d", from, to));
        database.beginTransaction();
        try {
            final MigrationsFilter migrations;
            try {
                migrations = new MigrationsFilter(
                    new OrderedByVersionFiles(context.getAssets(), folder),
                    from,
                    to
                );
            } catch (IOException e) {
                throw new MigrationException("Failed to read migrations", e);
            }
            for (MigrationFile migrationFile : migrations) {
                try {
                    for (Migration migration : migrationFile.migrations()) {
                        migration.apply(database);
                    }
                } catch (IOException e) {
                    throw new MigrationException("Failed to apply migration", e);
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}

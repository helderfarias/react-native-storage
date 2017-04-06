package br.com.helderfarias.storage.migration;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

final class Migration {

    private final String statement;

    Migration(@NonNull final String statement) {
        this.statement = statement;
    }

    void apply(@NonNull final SQLiteDatabase database) throws MigrationException {
        database.execSQL(statement);
    }

}

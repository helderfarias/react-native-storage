package br.com.helderfarias.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import br.com.helderfarias.storage.migration.MigrationException;
import br.com.helderfarias.storage.migration.SQLiteMigrations;

public class DatabaseFactory extends DatabaseManager {

    private static final String TAG = DatabaseFactory.class.getName();

    public DatabaseFactory(Context context, String dbName, int dbVersion) {
        super(context, dbName, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating onCreate: " + db.getPath() + ", version: " + getVersion());

        try {
            SQLiteMigrations migrations = new SQLiteMigrations(getContext());

            migrations.apply(db, 0, getVersion());
        } catch (MigrationException e) {
            Log.e(TAG, "error on apply migrations",  e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Creating onUpgrade: " + db.getPath());

        try {
            SQLiteMigrations migrations = new SQLiteMigrations(getContext());

            migrations.apply(db, oldVersion, newVersion);
        } catch (MigrationException e) {
            Log.e(TAG, "error on upgrade migrations",  e);
        }
    }

}
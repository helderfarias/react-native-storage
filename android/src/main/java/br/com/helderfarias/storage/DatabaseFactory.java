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
        // nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing
    }

}
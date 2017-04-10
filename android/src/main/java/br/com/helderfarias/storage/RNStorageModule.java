
package br.com.helderfarias.storage;

import android.content.ContentValues;
import android.util.Log;

import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import br.com.helderfarias.storage.jdbc.JsonMapper;
import br.com.helderfarias.storage.jdbc.Session;

public class RNStorageModule extends ReactContextBaseJavaModule {

    private static final String TAG = RNStorageModule.class.getSimpleName();

    private final ReactApplicationContext reactContext;

    private DatabaseFactory factory;

    public RNStorageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNStorage";
    }

    @ReactMethod
    public void open(String name, int version, Promise promise) {
        this.factory = new DatabaseFactory(this.reactContext, name, version);

        promise.resolve(this.factory.isOpen());
    }

    @ReactMethod
    public void query(String sql, ReadableMap args, boolean tx, Promise promise) {
        Log.d(TAG, "query for " + sql + " tx -> " + tx);

        try {
            Session session = new Session(factory.getDb());

            if (tx && !session.inTransaction()) {
                rejectIfTxRequiredWithoutActiveTransaction(promise);
                return;
            }

            if (!tx && session.inTransaction()) {
                rejectIfTxNotRequiredWithActiveTransaction(promise);
                return;
            }

            List<JSONObject> rows = session.rawQuery(sql, new JsonMapper());

            promise.resolve(ReactConverter.arraysToReact(rows));
        } catch (Exception ex) {
            promise.reject("error", ex);
        }
    }

    @ReactMethod
    public void execSQL(String sql, ReadableMap values, boolean tx, Promise promise) {
        Log.d(TAG, "execSQL " + sql + " tx -> " + tx);

        try {
            Session session = new Session(factory.getDb());

            if (tx && !session.inTransaction()) {
                rejectIfTxRequiredWithoutActiveTransaction(promise);
                return;
            }

            if (!tx && session.inTransaction()) {
                rejectIfTxNotRequiredWithActiveTransaction(promise);
                return;
            }

            String[] args = ReactConverter.reactToArrays(values);

            session.execSQL(sql, args);

            promise.resolve(true);
        } catch (Exception ex) {
            promise.reject("error", ex);
        }
    }

    @ReactMethod
    public void delete(String table, String whereClausule, ReadableArray whereArgs, boolean tx, Promise promise) {
        Log.d(TAG, "delete " + table + " tx -> " + tx);

        try {
            Session session = new Session(factory.getDb());

            if (tx && !session.inTransaction()) {
                rejectIfTxRequiredWithoutActiveTransaction(promise);
                return;
            }

            if (!tx && session.inTransaction()) {
                rejectIfTxNotRequiredWithActiveTransaction(promise);
                return;
            }

            String[] args = ReactConverter.reactToArrays(whereArgs);

            session.delete(table, whereClausule, args);

            promise.resolve(true);
        } catch (Exception ex) {
            promise.reject("error", ex);
        }
    }

    @ReactMethod
    public void insert(String table, ReadableMap whereArgs, boolean tx, Promise promise) {
        Log.d(TAG, "insert " + table + " tx -> " + tx);

        try {
            Session session = new Session(factory.getDb());

            if (tx && !session.inTransaction()) {
                rejectIfTxRequiredWithoutActiveTransaction(promise);
                return;
            }

            if (!tx && session.inTransaction()) {
                rejectIfTxNotRequiredWithActiveTransaction(promise);
                return;
            }

            ContentValues values = ReactConverter.reactToContentValues(whereArgs);

            session.insert(table, values);

            promise.resolve(true);
        } catch (Exception ex) {
            promise.reject("error", ex);
        }
    }

    @ReactMethod
    public void update(String table, ReadableMap values, String whereClausule, ReadableArray whereArgs, boolean tx, Promise promise) {
        Log.d(TAG, "update " + table + " tx -> " + tx);

        try {
            Session session = new Session(factory.getDb());

            if (tx && !session.inTransaction()) {
                rejectIfTxRequiredWithoutActiveTransaction(promise);
                return;
            }

            if (!tx && session.inTransaction()) {
                rejectIfTxNotRequiredWithActiveTransaction(promise);
                return;
            }

            ContentValues argsValues = ReactConverter.reactToContentValues(values);

            String[] args = ReactConverter.reactToArrays(whereArgs);

            session.update(table, argsValues, whereClausule, args);

            promise.resolve(true);
        } catch (Exception ex) {
            promise.reject("error", ex);
        }
    }

    @ReactMethod
    public void startTransaction(Promise promise) {
        Log.d(TAG, "startTransaction ");

        try {
            Session session = new Session(factory.getDb());

            session.beginTransaction();

            promise.resolve(session.inTransaction());
        } catch (Exception ex) {
            promise.reject("error", ex);
        }
    }

    @ReactMethod
    public void commit(Promise promise) {
        Log.d(TAG, "commit");

        Session session = new Session(factory.getDb());

        try {
            session.setTransactionSuccessful();

            promise.resolve(true);
        } catch (Exception ex) {
            session.endTransaction();
            promise.reject("error", ex);
        }
    }

    @ReactMethod
    public void rollback(Promise promise) {
        Log.d(TAG, "rollback");

        Session session = new Session(factory.getDb());

        try {
            session.endTransaction();

            promise.resolve(true);
        } catch (Exception ex) {
            promise.reject("error", ex);
        }
    }

    private void rejectIfTxNotRequiredWithActiveTransaction(Promise promise) {
        String message = "transaction opened, execute commit or rollback before using operation.";

        promise.reject("error", message, new IllegalStateException(message));
    }

    private void rejectIfTxRequiredWithoutActiveTransaction(Promise promise) {
        String message = "without transaction started";

        promise.reject("error", message, new IllegalStateException(message));
    }

}
import { NativeModules } from 'react-native';

const { RNStorage } = NativeModules;

class Database {

    constructor(db) {
        this.db = db;
    }

    get db() {
        return this.db;
    }

    query(sql, args) {
        return RNStorage.query(sql, args, false);
    }

    update(table, values, whereClausule, whereArgs) {
        return RNStorage.update(table, values, whereClausule, whereArgs, false);
    }

    insert(table, values) {
        return RNStorage.insert(table, values, false);
    }

    delete(table, whereClausule, whereArgs) {
        return RNStorage.delete(table, whereClausule, whereArgs, false);
    }

    execSQL(sql, args) {
        return RNStorage.execSQL(sql, args, false);
    }

    transaction(callback) {
        return RNStorage.startTransaction().then(ok => {
            if (!ok) {
                return null;
            }

            callback({
                query: (sql, args) => RNStorage.query(sql, args, true),
                update: (table, values, whereClausule, whereArgs) => RNStorage.update(sql, values, whereClausule, whereArgs, true),
                insert: (table, values) => RNStorage.insert(table, values, true),
                delete: (table, whereClausule, whereArgs) => RNStorage.delete(table, whereClausule, whereArgs, true),
                execSQL: (sql, args) => RNStorage.execSQL(sql, args, true),
                commit: () => RNStorage.commit(),
                rollback: () => RNStorage.rollback(),
            });
        });
    }

}

class DatabaseFactory {

    static open(name, version) {
        return RNStorage.open(name, version).then(ok => {
            if (!ok) {
                return null;
            }

            return new Database({ name: name, version: version });
        })
    }

}

export default DatabaseFactory;
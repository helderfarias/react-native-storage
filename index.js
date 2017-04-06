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
        return RNStorage.query(sql, args);
    }

    queryForCount(sql, args) {
        return RNStorage.queryForCount(sql, args);
    }

    execSQL(sql, args) {
        return RNStorage.queryForCount(sql, args);
    }

    update(table, values, whereClausule, whereArgs) {
        return RNStorage.update(sql, values, whereClausule, whereArgs);
    }

    insert(table, values) {
        return RNStorage.insert(table, values);
    }

    delete(table, whereClausule, whereArgs) {
        return RNStorage.delete(table, whereClausule, whereArgs);
    }

    transaction(callback) {
        return RNStorage.startTransaction().then(ok => {
            if (!ok) {
                return null;
            }

            callback({
                query: (sql, args) => RNStorage.query(sql, args),
                update: (table, values, whereClausule, whereArgs) => RNStorage.update(sql, values, whereClausule, whereArgs),
                insert: (table, values) => RNStorage.insert(table, values),
                delete: (table, whereClausule, whereArgs) => RNStorage.delete(table, whereClausule, whereArgs),
                queryForCount: (sql, args) => RNStorage.queryForCount(sql, args),
                execSQL: (sql, args) => RNStorage.execSQL(sql, args),
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

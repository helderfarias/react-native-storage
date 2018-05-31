import { NativeModules } from 'react-native';

const { RNStorage } = NativeModules;

class StatementTask {

    constructor() {
        this.poll = [];
    }

    query(sql, args) {
        this.poll.push(function task() {
            return RNStorage.query(sql, args, true);
        });
    }

    update (table, values, whereClausule, whereArgs) { 
        this.poll.push(function task() {
            return RNStorage.update(sql, values, whereClausule, whereArgs, true);
        });
    }

    insert(table, values) {
        this.poll.push(function task() {
            return RNStorage.insert(table, values, true);
        });
    }

    delete(table, whereClausule, whereArgs) {
        this.poll.push(function task() {
            return RNStorage.delete(table, whereClausule, whereArgs, true);
        });
    } 

    execSQL(sql, args) {
        this.poll.push(function task() {
            return RNStorage.execSQL(sql, args, true);
        });
    }

    invokeAll() {
        const tasks = this.poll.map(task => task());
        return Promise.all(tasks);
    }

}

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

    async transaction(commands) {
        const statement = new StatementTask();

        await commands(statement);
    
        return await RNStorage.startTransaction().then(ok => {
            if (ok) {
                return statement.invokeAll()
                                .then(() => RNStorage.commit())
                                .catch(() => RNStorage.rollback());
            }
        }).catch(e => RNStorage.rollback());
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
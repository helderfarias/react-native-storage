import { NativeModules } from "react-native";

const { RNStorage } = NativeModules;

export class StatementTask {
  constructor() {
    this.poll = [];
  }

  query(sql, args) {
    this.poll.push(() => RNStorage.query(sql, args, true));
  }

  update(table, values, whereClausule, whereArgs) {
    this.poll.push(() => RNStorage.update(sql, values, whereClausule, whereArgs, true));
  }

  insert(table, values) {
    this.poll.push(() => RNStorage.insert(table, values, true));
  }

  delete(table, whereClausule, whereArgs) {
    this.poll.push(() => RNStorage.delete(table, whereClausule, whereArgs, true));
  }

  execSQL(sql, args) {
    this.poll.push(() => RNStorage.execSQL(sql, args, true));
  }

  invokeAll() {
    const tasks = this.poll.map(task => task());
    return Promise.all(tasks);
  }
}

export class Database {
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

    return await RNStorage.startTransaction()
      .then((ok) => {
        if (ok) {
          return statement
            .invokeAll()
            .then(() => RNStorage.commit())
            .catch(() => RNStorage.rollback());
        }
      })
      .catch(e => RNStorage.rollback());
  }
}

export class DatabaseFactory {
  static async open(name, version) {
    return await RNStorage.open(name, version).then((ok) => {
      if (!ok) throw "Cold not open database";

      return new Database({ name, version });
    });
  }
}

export default DatabaseFactory;

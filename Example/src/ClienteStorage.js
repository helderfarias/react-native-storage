import React, { Component } from "react";
import { View } from "react-native";
import Storage from 'react-native-storage';

export default class ClienteStorage extends Component {

  async componentWillMount() {
    const db = await Storage.open("example.sqlite", 1);
    console.log(db);

    console.log(await db.query("SELECT * FROM clients"));

    await db.delete("clients");

    await db.insert("clients", { num: 1, id: 1, describe: "describe" });

    await db.update("clients", { num: 100 }, "num = ?", [1]);

    await db.insert("clients", { num: 400 });

    await db.transaction(async (tx) => {
      await tx.execSQL("INSERT INTO clients(id) VALUES(1)");
      await tx.execSQL("INSERT INTO clients(id) VALUES(2)");
      await tx.execSQL("INSERT INTO clients(id) VALUES(3)");
      await tx.commit();
    });
  }

  render() {
    return <View />;
  }
  
}
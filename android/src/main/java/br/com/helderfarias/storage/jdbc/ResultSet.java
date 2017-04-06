package br.com.helderfarias.storage.jdbc;

import java.util.Date;

public interface ResultSet {

	int SQLITE_INTEGER    =  1;
	int SQLITE_FLOAT      =  2;
	int SQLITE_TEXT       =  3;
	int SQLITE_BLOB       =  4;
	int SQLITE_NULL       =  5;

	String getColumnName(int index);

	int getColumnCount();

	int getType(int index);

	Object getObject(int col);

	Double getDouble(String columnIndex);

	Integer getInt(String columnIndex);

	Long getLong(String columnIndex);

	String getString(String columnIndex);

	Date getDate(String columnIndex);

	Date getDateTime(String columnName);

	Boolean getBoolean(String columnName);

}

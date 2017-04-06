package br.com.helderfarias.storage.jdbc;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Session {

	private SQLiteDatabase decorator;

	public Session(SQLiteDatabase decorator) {
		this.decorator = decorator;
	}

	public boolean inTransaction() {
		if (!this.decorator.isOpen()) {
			return false;
		}
		return this.decorator.inTransaction();
	}

	public void beginTransaction() {
		if (!this.decorator.isOpen()) {
			return;
		}
		this.decorator.beginTransaction();
	}

	public void endTransaction() {
		if (inTransaction()) {
			this.decorator.endTransaction();
		}
	}

	public void setTransactionSuccessful() {
		this.decorator.setTransactionSuccessful();
	}
	
	public void close() {
		try {
			if (inTransaction()) {
				return;
			}
			
			if (this.decorator.isOpen()) {
				this.decorator.close();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public <T> List<T> rawQuery(String sql, RowMapper<T> mapper) {
		return rawQuery(sql, null, mapper);
	}
	
	public <T> List<T> rawQuery(String sql, String[] args, RowMapper<T> mapper) {
		Cursor cursor = this.decorator.rawQuery(sql, args);
		List<T> results = new ArrayList<T>();
		try {
			while (cursor.moveToNext()) {
            	results.add(mapper.toMapper(new ResultSetImpl(cursor)));
			}
			return results;
        } finally {
            cursor.close();
        }		
	}
	
	public Long queryForLong(String sql) {
		return queryForLong(sql, null);
	}
	
	public Long queryForLong(String sql, String[] args) {
		Cursor cursor = this.decorator.rawQuery(sql, args);
		try {
            if (cursor.moveToFirst()) {
            	return cursor.getLong(0);
            }
        } finally {
            cursor.close();
        }		
		return null;
	}

	public void execSQL(String sql, String[] args) {
		this.decorator.execSQL(sql, args);
	}

	public Long insert(String table, ContentValues values) {
		return this.decorator.insert(table, null, values);
	}

	public Integer update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return this.decorator.update(table, values, whereClause, whereArgs);
	}

	public void delete(String table, String whereClause, String[] whereArgs) {
		this.decorator.delete(table, whereClause, whereArgs);
	}

	public boolean exists(Long value) {
		return value != null && value > 0;
	}

}

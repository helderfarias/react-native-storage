package br.com.helderfarias.storage.jdbc;

import java.util.Date;

import android.database.Cursor;


public class ResultSetImpl implements ResultSet {

	private Cursor cursor;

	public ResultSetImpl(Cursor cursor) {
		this.cursor = cursor;
	}

	@Override
	public String getColumnName(int index) {
		return this.cursor.getColumnName(index);
	}

	@Override
	public int getColumnCount() {
		return this.cursor.getColumnCount();
	}

	@Override
	public int getType(int index)  {
		return this.cursor.getType(index);
	}

	@Override
	public Object getObject(int col) {
		switch (getType(col)) {
			case SQLITE_INTEGER:
				long val = getLong(getColumnName(col));

				if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
					return new Long(val);
				} else {
					return new Integer((int) val);
				}
			case SQLITE_FLOAT:
				return new Double(getDouble(getColumnName(col)));
			case SQLITE_BLOB:
				return null;
			case SQLITE_NULL:
				return null;
			case SQLITE_TEXT:
			default:
				return getString(getColumnName(col));
		}
	}

	@Override
	public Double getDouble(String columnName) {
		if (isNull(columnName)) {
			return null;
		}
		
		int columnIndex = cursor.getColumnIndex(columnName);
		return cursor.getDouble(columnIndex);
	}

	@Override
	public Integer getInt(String columnName) {
		if (isNull(columnName)) {
			return null;
		}
		
		int columnIndex = cursor.getColumnIndex(columnName);
		return cursor.getInt(columnIndex);
	}
	
	@Override
	public Boolean getBoolean(String columnName) {
		if (isNull(columnName)) {
			return null;
		}
		
		int columnIndex = cursor.getColumnIndex(columnName);
		int value = cursor.getInt(columnIndex);
		return value != 0 ? true : false;
	}

	@Override
	public Long getLong(String columnName) {
		if (isNull(columnName)) {
			return null;
		}
		
		int columnIndex = cursor.getColumnIndex(columnName);
		return cursor.getLong(columnIndex);
	}

	@Override
	public String getString(String columnName) {
		if (isNull(columnName)) {
			return null;
		}
		
		int columnIndex = cursor.getColumnIndex(columnName);
		return cursor.getString(columnIndex);
	}

	@Override
	public Date getDate(String columnName) {
		if (isNull(columnName)) {
			return null;
		}
		
		int columnIndex = cursor.getColumnIndex(columnName);
		
		String time = cursor.getString(columnIndex);
		if (time == null || time.length() == 0) {
			return null;
		}
		
//		return DateUtil.date(time, DateUtil.anoMesDiaAmericano()).toDate();
		throw new IllegalArgumentException("invalid error");
	}
	
	@Override
	public Date getDateTime(String columnName) {
		if (isNull(columnName)) {
			return null;
		}
		
		int columnIndex = cursor.getColumnIndex(columnName);
		
		String time = cursor.getString(columnIndex);
		if (time == null || time.length() == 0) {
			return null;
		}
		
//		return DateUtil.date(time, DateUtil.anoMesDiaHoraMinutoSegundoAmericano()).toDateTime();
		throw new IllegalArgumentException("invalid error");
	}

	private boolean isNull(String columnName) {
		int columnIndex = cursor.getColumnIndex(columnName);
		return cursor.isNull(columnIndex);
	}

}

package br.com.helderfarias.storage.jdbc;


public interface RowMapper<T> {
	
	T toMapper(ResultSet cursor);

}

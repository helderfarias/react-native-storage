package br.com.helderfarias.storage.jdbc;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.helderfarias.storage.jdbc.ResultSet;
import br.com.helderfarias.storage.jdbc.RowMapper;

/**
 * Created by helder on 05/04/17.
 */

public class JsonMapper implements RowMapper<JSONObject> {

    @Override
    public JSONObject toMapper(ResultSet cursor) {
        JSONObject json = new JSONObject();

        for (int columnIndex = 0; columnIndex < cursor.getColumnCount(); columnIndex++) {
            try {
                json.put(cursor.getColumnName(columnIndex), cursor.getObject(columnIndex));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json;
    }

}

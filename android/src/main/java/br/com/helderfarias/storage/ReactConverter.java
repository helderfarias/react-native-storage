package br.com.helderfarias.storage;

import android.content.ContentValues;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ReactConverter {


    public static ContentValues reactToContentValues(ReadableMap readableMap) throws JSONException {
        if (readableMap == null) {
            return null;
        }

        ContentValues output = new ContentValues();

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()){
            String key = iterator.nextKey();
            ReadableType valueType = readableMap.getType(key);
            switch (valueType){
                case Null:
                    output.putNull(null);
                    break;

                case Boolean:
                    output.put(key, readableMap.getBoolean(key));
                    break;

                case Number:
                    output.put(key, readableMap.getDouble(key));
                    break;

                case String:
                    output.put(key, readableMap.getString(key));
                    break;
            }
        }

        return output;
    }

    public static String[] reactToArrays(ReadableMap readableMap) throws JSONException {
        if (readableMap == null) {
            return null;
        }

        ArrayList<String> output = new ArrayList<>();

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()){
            String key = iterator.nextKey();
            ReadableType valueType = readableMap.getType(key);
            switch (valueType){
                case Null:
                    output.add(null);
                    break;

                case Boolean:
                    output.add(String.valueOf(readableMap.getBoolean(key)));
                    break;

                case Number:
                    output.add(String.valueOf(readableMap.getDouble(key)));
                    break;

                case String:
                    output.add(readableMap.getString(key));
                    break;

                case Map:
                    String[] maps = reactToArrays(readableMap.getMap(key));
                    for(String item : maps) {
                        output.add(item);
                    }
                    break;

                case Array:
                    String[] arrays = reactToArrays(readableMap.getArray(key));
                    for(String item : arrays) {
                        output.add(item);
                    }
                    break;
            }
        }

        return output.toArray(new String[]{});
    }

    public static String[] reactToArrays(ReadableArray readableArray) throws JSONException {
        if (readableArray == null) {
            return null;
        }

        ArrayList<String> output = new ArrayList<>();

        for(int key = 0; key < readableArray.size(); key++) {
            ReadableType valueType = readableArray.getType(key);
            switch (valueType){
                case Null:
                    output.add(null);
                    break;

                case Boolean:
                    output.add(String.valueOf(readableArray.getBoolean(key)));
                    break;

                case Number:
                    output.add(String.valueOf(readableArray.getDouble(key)));
                    break;

                case String:
                    output.add(readableArray.getString(key));
                    break;

                case Map:
                    String[] maps = reactToArrays(readableArray.getMap(key));
                    for(String item : maps) {
                        output.add(item);
                    }
                    break;

                case Array:
                    String[] arrays = reactToArrays(readableArray.getArray(key));
                    for(String item : arrays) {
                        output.add(item);
                    }
                    break;
            }
        }

        return output.toArray(new String[]{});
    }

    public static JSONObject reactToJson(ReadableMap readableMap) throws JSONException {
        if (readableMap == null) {
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while(iterator.hasNextKey()){
            String key = iterator.nextKey();
            ReadableType valueType = readableMap.getType(key);
            switch (valueType){
                case Null:
                    jsonObject.put(key,JSONObject.NULL);
                    break;
                case Boolean:
                    jsonObject.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    jsonObject.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    jsonObject.put(key, readableMap.getString(key));
                    break;
                case Map:
                    jsonObject.put(key, reactToJson(readableMap.getMap(key)));
                    break;
                case Array:
                    jsonObject.put(key, reactToJson(readableMap.getArray(key)));
                    break;
            }
        }

        return jsonObject;
    }

    public static JSONArray reactToJson(ReadableArray readableArray) throws JSONException {
        if (readableArray == null) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for(int i=0; i < readableArray.size(); i++) {
            ReadableType valueType = readableArray.getType(i);
            switch (valueType){
                case Null:
                    jsonArray.put(JSONObject.NULL);
                    break;
                case Boolean:
                    jsonArray.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    jsonArray.put(readableArray.getDouble(i));
                    break;
                case String:
                    jsonArray.put(readableArray.getString(i));
                    break;
                case Map:
                    jsonArray.put(reactToJson(readableArray.getMap(i)));
                    break;
                case Array:
                    jsonArray.put(reactToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return jsonArray;
    }

    public static WritableMap jsonToReact(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return null;
        }

        WritableMap writableMap = Arguments.createMap();
        Iterator iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = jsonObject.get(key);

            if (value instanceof Float || value instanceof Double) {
                writableMap.putDouble(key, jsonObject.getDouble(key));
            } else if (value instanceof Number) {
                writableMap.putDouble(key, jsonObject.getLong(key));
            } else if (value instanceof String) {
                writableMap.putString(key, jsonObject.getString(key));
            } else if (value instanceof JSONObject) {
                writableMap.putMap(key, jsonToReact(jsonObject.getJSONObject(key)));
            } else if (value instanceof JSONArray) {
                writableMap.putArray(key, jsonToReact(jsonObject.getJSONArray(key)));
            } else if (value == JSONObject.NULL) {
                writableMap.putNull(key);
            }
        }
        return writableMap;
    }

    public static WritableArray jsonToReact(JSONArray jsonArray) throws JSONException {
        WritableArray writableArray = Arguments.createArray();

        for(int i=0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);

            if (value instanceof Float || value instanceof Double) {
                writableArray.pushDouble(jsonArray.getDouble(i));
            } else if (value instanceof Number) {
                writableArray.pushDouble(jsonArray.getLong(i));
            } else if (value instanceof String) {
                writableArray.pushString(jsonArray.getString(i));
            } else if (value instanceof JSONObject) {
                writableArray.pushMap(jsonToReact(jsonArray.getJSONObject(i)));
            } else if (value instanceof JSONArray){
                writableArray.pushArray(jsonToReact(jsonArray.getJSONArray(i)));
            } else if (value == JSONObject.NULL){
                writableArray.pushNull();
            }
        }

        return writableArray;
    }

}

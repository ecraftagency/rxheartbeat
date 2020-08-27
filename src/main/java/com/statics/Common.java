/*
 * author: public static <K, V extends hasKey<K>> HashMap<K, V> loadMap(String json, Class<V> object)
 *
 */

package com.statics;

import com.common.LOG;
import com.common.Utilities;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Common {
  public interface hasKey<T> {
    T mapKey();
  }

  public static void arraySubstitute(JSONObject jsonObject) throws JSONException {
    Iterator<String> keys = jsonObject.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      String value = (String)jsonObject.get(key);
      if (value.matches("\\[(.*)]")){
        JSONArray array = new JSONArray(value);
        jsonObject.put(key, array);
      }
    }
  }

  //dear god pls make this (parametric polymorphism) stop T____T
  public static <K, V extends hasKey<K>> HashMap<K, V> loadMap(String json, Class<V> object) {
    HashMap<K, V> result = new HashMap<>();
    JSONObject j = null;
    try {
      JSONArray rows = new JSONArray(json);
      for (int i = 0; i < rows.length(); i++){
        j = rows.getJSONObject(i);
        Common.arraySubstitute(rows.getJSONObject(i));
        V v = Utilities.gson.fromJson(j.toString(), object);
        result.put(v.mapKey(), v);
      }
    }
    catch (Exception e) {
      result.clear();
      String logJson = "";
      if (j != null)
        logJson = j.toString();
      LOG.globalException("node", "Common.loadMap", logJson);
    }
    return result;
  }

  public static<T> List<T> loadList(String json, Class<T> object) {
    List<T> result = new ArrayList<>();
    try {
      JSONArray rows = new JSONArray(json);
      for (int i = 0; i < rows.length(); i++){
        JSONObject row = rows.getJSONObject(i);
        Common.arraySubstitute(rows.getJSONObject(i));
        T t = Utilities.gson.fromJson(row.toString(), object);
        result.add(t);
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      result.clear();
    }
    return result;
  }
}

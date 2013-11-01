package com.nexj.bluetooth.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResultFactory
{
    public static HashMap<String, Object> JSONToMap(JSONObject obj)
    {
        Iterator<String> itr = obj.keys();
        HashMap<String, Object> map = new HashMap<String, Object>(8);

        while (itr.hasNext())
        {
            String key = itr.next();
            Object value = obj.opt(key);

            if (value != null)
            {
                map.put(key, value);
            }
        }

        return map;
    }

    public static JSONObject mapToJSON(Map<String, Object> map)
    {
        JSONObject obj = new JSONObject();

        try
        {
            for (Map.Entry<String, Object> entry : map.entrySet())
            {
                JSONArray array = new JSONArray();
                for (String type : (List<String>)entry.getValue())
                {
                    array.put(type);
                }

                obj.put(entry.getKey(), array);
            }
        }
        catch (Exception e)
        {
            return new JSONObject(map);
        }

        return obj;
    }

    public static JSONArray listToJSON(List<Object> list)
    {
        return new JSONArray(list);
    }

    public static PluginResult createResult(Status status, Map<String, Object> map)
    {
        PluginResult result = new PluginResult(status, mapToJSON(map));

        result.setKeepCallback(true);

        return result;
    }

    public static PluginResult createResult(Status status, JSONObject jsonObject)
    {
        PluginResult result = new PluginResult(status, jsonObject);

        result.setKeepCallback(true);

        return result;
    }

    public static PluginResult createResult(Status status, List<Object> list)
    {
        PluginResult result = new PluginResult(status, listToJSON(list));

        result.setKeepCallback(true);

        return result;
    }

    public static PluginResult createResult(Status status, String message)
    {
        PluginResult result = new PluginResult(status, message);

        result.setKeepCallback(true);

        return result;
    }

    public static PluginResult createResult(Status status, Boolean message)
    {
        PluginResult result = new PluginResult(status, message);

        result.setKeepCallback(true);

        return result;
    }

    public static PluginResult createResult(Status status)
    {
        PluginResult result = new PluginResult(status);

        result.setKeepCallback(true);

        return result;
    }
}

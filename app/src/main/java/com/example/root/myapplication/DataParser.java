package com.example.root.myapplication;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 8/27/17.
 */

public class DataParser {
    private HashMap<String, String> getpalce(JSONObject googleplacejson) {
        HashMap<String, String> googleplacemap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longtitude = "";
        String reference = "";
        try {
            if (!googleplacejson.isNull("Name")) {
                placeName = googleplacejson.getString("Name");
            }
            if (!googleplacejson.isNull("Vicinity")) {
                vicinity = googleplacejson.getString("Vicinity");
            }
            latitude = googleplacejson.getJSONObject("geomatry").getJSONObject("Location").getString("Lat");
            longtitude = googleplacejson.getJSONObject("geomatry").getJSONObject("Location").getString("Lng");
            reference = googleplacejson.getString("reference");
            googleplacemap.put("PlaceName", placeName);
            googleplacemap.put("Vicinity", vicinity);
            googleplacemap.put("Latitude", latitude);
            googleplacemap.put("Longtitude", longtitude);
            googleplacemap.put("Reference", reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleplacemap;

    }

    private List<HashMap<String, String>> getplaces(JSONArray jsonArray) {
        int count = jsonArray.length();
        List<HashMap<String, String>> placeList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        for (int i = 0; i < count; i++) {
            try {
                placeMap = getpalce((JSONObject) jsonArray.get(i));
                placeList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placeList;
    }

    List<HashMap<String, String>> parse(String jsondata)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getplaces(jsonArray);
    }


    }




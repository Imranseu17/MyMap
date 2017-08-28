package com.example.root.myapplication;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 8/27/17.
 */

public class GetNearbyPlaceData extends AsyncTask<Object,String,String> {
    String googleplaceData;
    GoogleMap  googleMap;
    String url;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplaceData = downloadUrl.readurl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplaceData;

    }



    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyplaceList = null;
        DataParser dataParser = new DataParser();
        nearbyplaceList = dataParser.parse(s);
        shownearbyplaces(nearbyplaceList);

    }
    private void shownearbyplaces(List<HashMap<String,String>> nearbyplaceList){
        for (int i = 0; i< nearbyplaceList.size();i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googleplace = nearbyplaceList.get(i);
            String placeName =  googleplace.get("PlaceName");
           String vicinity = googleplace.get("Vicinity");
            double lat = Double.parseDouble(googleplace.get("Latitude"));
            double lang = Double.parseDouble(googleplace.get("Longtitude"));
            LatLng latLng = new LatLng(lat,lang);
            markerOptions.position(latLng);
            markerOptions.title(placeName+" : "+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        }
    }
}

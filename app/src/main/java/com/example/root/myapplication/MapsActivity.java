package com.example.root.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

@SuppressWarnings("deprecation")

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener
{

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentlocationmarker;
    public static final int requestlocationcode = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longtitude;
    double endlatitude, endlongtitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checklocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case requestlocationcode:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                        if(apiClient == null){
                            buildgoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    }
                }

                else {
                    Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();

                }
                return;
        }
    }


    /**
     * Manipulates the map  available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//        }
         if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                buildgoogleApiClient();
                mMap.setMyLocationEnabled(true);

            }
        }
        else {
            buildgoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMarkerClickListener(this);

    }
    public void onClick(View view) {
        Object datatransfer[] = new Object[2];
        GetNearbyPlaceData getNearbyPlaceData = new GetNearbyPlaceData();
        switch (view.getId()) {
            case R.id.B_Search: {
                EditText tf_Location = (EditText) findViewById(R.id.TF_Location);
                String location = tf_Location.getText().toString();
                List<Address> addressList = null;
                MarkerOptions no = new MarkerOptions();
                if (!(location.equals(""))) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    for (int i = 0; i < addressList.size(); i++) {
                        Address myaddress = addressList.get(i);
                        LatLng newlatlong = new LatLng(myaddress.getLatitude(),myaddress.getLongitude());
                        no.position(newlatlong);
                        no.title("Your Search Result");
                        mMap.addMarker(no);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(newlatlong));
                    }

                }
            }
            break;
            case R.id.B_hospitas:
                mMap.clear();
                String hospitals = "hospitals";
                String url = geturl(latitude,longtitude,hospitals);
                datatransfer[0]=mMap;
                datatransfer[1]=url;
                getNearbyPlaceData.execute();
                Toast.makeText(MapsActivity.this,"Showing Nearby Hospitals",Toast.LENGTH_LONG).show();
                break;

            case R.id.B_resturent:
                mMap.clear();
                String restaurent = "restaurent";
                url = geturl(latitude,longtitude,restaurent);
                datatransfer[0]=mMap;
                datatransfer[1]=url;
                getNearbyPlaceData.execute();
                Toast.makeText(MapsActivity.this,"Showing Nearby restaurants",Toast.LENGTH_LONG).show();
                break;
            case R.id.B_school:
                mMap.clear();
                String school = "school";
                url = geturl(latitude,longtitude,school);
                datatransfer[0]=mMap;
                datatransfer[1]=url;
                getNearbyPlaceData.execute();
                Toast.makeText(MapsActivity.this,"Showing Nearby Schools",Toast.LENGTH_LONG).show();
                break;
            case R.id.B_to:
                mMap.clear();
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(endlatitude,endlongtitude));
                options.title("Destination");
                float result[] = new float[10];
                Location.distanceBetween(latitude,longtitude,endlatitude,endlongtitude,result);
                options.snippet("Distance: "+result[0]);
                mMap.addMarker(options);
                break;

        }
    }
    private String geturl(double latitude,double longtitude,String nearbyPlace){
        StringBuilder googleplaceurl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleplaceurl.append("Location: "+latitude+" , "+longtitude);
        googleplaceurl.append("&radius: "+PROXIMITY_RADIUS);
        googleplaceurl.append("&type: "+nearbyPlace);
        googleplaceurl.append("&sensor:true");
        googleplaceurl.append("&key:"+"AIzaSyBYi2f_zHI3GChfOSi1Gc27c9KW2MqfQBE");
        return googleplaceurl.toString();
    }
    protected  synchronized  void buildgoogleApiClient(){

        apiClient = new GoogleApiClient.Builder(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();

        apiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation = location;
        if(currentlocationmarker != null){
            currentlocationmarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentlocationmarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(19));
        if(apiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }


    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient,locationRequest,
                     MapsActivity.this);
        }

    }
    public boolean checklocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        requestlocationcode);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        requestlocationcode);

            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        endlatitude = marker.getPosition().latitude;
        endlongtitude = marker.getPosition().longitude;

    }


}

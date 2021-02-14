package com.example.maps_chetna_c0776254;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private final int REQUEST_CODE = 1;
    public Marker homeMarker;
    Polygon shape;

    public final int POLYGON_POINTS = 4;
    List<Marker> markers = new ArrayList<>();
    ArrayList<LatLng> locations = new ArrayList<>();

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                // set the home location
                setHomeLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!checkPermission())
            requestPermission();
        else
            getLocation();

        mMap.setOnMapLongClickListener(latLng -> {
            if (markers.size() == 4)
            {
                clearMap();
            }
            Location location = new Location("Your Destination");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            // set Marker
            setMarker(location);
        });
        final Context context = this;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                Geocoder gc = new Geocoder(MapsActivity.this);
                // List<Address> list = null;
                List<Address> list;
                LatLng ll = marker.getPosition();
                try
                {
                    list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
                } catch (IOException e)
                {
                    e.printStackTrace();
                    return false;
                }
                Address add = list.get(0);

                //custom method to set Title of marker in TextView
                // setValueInTextView(add.getLocality());
//                    Toast.makeText(context,"ADDRESS IS: "+ add.getPostalCode() + add.getLocality() + add.getThoroughfare() + add.getCountryName(),Toast.LENGTH_LONG).show();
                Toast.makeText(context,"ADDRESS IS: " + add.getLocality() ,Toast.LENGTH_LONG).show();
                //                   Toast.makeText(context,"YOU CLICKED ON "+ marker.getTitle(),Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
    private void setHomeLocation(Location location)
    {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("User Location")
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .snippet("user is  here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name));
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 8));
    }

    private void setMarker(Location location)
    {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        locations.add(userLatLng);

//            if (markers.size() == 4)
//            {
//                clearMap();
//            }

        if(locations.size() == 1)
        {
            LatLng locationofuser = new LatLng(-31, 150);
            float[] results = new float[1];
            Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, locationofuser.latitude,locationofuser.longitude, results);
            // Toast.makeText(this, results[0]/1000 + "Km", Toast.LENGTH_SHORT).show();
            MarkerOptions options = new MarkerOptions().position(userLatLng)
                    .title("A")
                    .snippet(results[0]/1000 + "Km")
                    .draggable(true);
            markers.add(mMap.addMarker(options));

        }
        if(locations.size() == 2)
        {
            MarkerOptions options = new MarkerOptions().position(userLatLng)
                    .title("B")
                    .snippet("Calgary")
                    .draggable(true);
            markers.add(mMap.addMarker(options));
        }
        if(locations.size() == 3)
        {
            MarkerOptions options = new MarkerOptions().position(userLatLng)
                    .title("C")
                    .snippet("Calgary")
                    .draggable(true);
            markers.add(mMap.addMarker(options));
        }
        if(locations.size() == 4)
        {
            MarkerOptions options = new MarkerOptions().position(userLatLng)
                    .title("D")
                    .snippet("Calgary")
                    .draggable(true);
            markers.add(mMap.addMarker(options));
            drawShape();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation()
    {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        Location lastknownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // set the known location as home location
        setHomeLocation(lastknownLocation);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean checkPermission() {
        int permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }
        }
    }

    private void clearMap()
    {

        for (Marker marker : markers)
            marker.remove();

        locations.clear();
        markers.clear();
        shape.remove();
        shape = null;
    }

    private void drawShape()
    {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x330000FF)
                .strokeWidth(5)
                .strokeColor(Color.RED);

        for (int i=0; i<POLYGON_POINTS; i++)
        {
            options.add(markers.get(i).getPosition());
        }
        shape = mMap.addPolygon(options);
    }
}


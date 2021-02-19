package com.example.maps_chetna_c0776254;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

       private static final String TAG = "MapsActivity" ;
        private GoogleMap mMap;

        private static final int REQUEST_CODE = 1;
        private Marker homeMarker;
        private Marker firstMarker;

        public  Location userLocation;

        private List<LatLng> latLngList = new ArrayList<>();
        List<Polyline> polylineList = new ArrayList<>();
        List<Marker> markers = new ArrayList();

        Polyline line;
        Polygon shape;

        private static final int POLYGON_SIDES = 4;

        LocationManager locationManager;
        LocationListener locationListener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */

        private void startUpdateLocations() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(Location location)
    {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("User is here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Location of user");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }

    @Override
        public void onMapReady(final GoogleMap googleMap)
        {
            mMap = googleMap;

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    setHomeMarker(location);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras)
                {

                }
                @Override
                public void onProviderEnabled(String provider)
                {

                }
                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if (!hasLocationPermission())
                requestLocationPermission();
            else
                startUpdateLocations();


            //apply long gesture
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
            {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.clear();
                }
            });


            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng)
                {
                    setMarker(latLng);
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    setMarker(marker.getPosition());

                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(Marker marker)
                {

                    marker.showInfoWindow();
                    Geocoder geoCoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    Address addressOfMarker = null;
                    //have to use list for many markers
                    //List<Address> addresses;

                    LatLng isLatitudeAndLongitude = marker.getPosition();

                    if(userLocation != null)
                    {
                        Location location = new Location(LocationManager.GPS_PROVIDER);

                        location.setLatitude( isLatitudeAndLongitude.latitude);
                        location.setLongitude(isLatitudeAndLongitude.longitude);

                        double distance = userLocation.distanceTo(location);
                        marker.setSnippet("Distance b/w marker and user :  " + distance );
                    }

                    try
                    {
                        List<Address> matches = geoCoder.getFromLocation(isLatitudeAndLongitude.latitude, isLatitudeAndLongitude.longitude, 1);
                        addressOfMarker = (matches.isEmpty() ? null : matches.get(0));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    ArrayList<String> addressString = new ArrayList<>();

                    if (addressOfMarker != null)
                    {
                        if (addressOfMarker.getThoroughfare() != null)
                        {
                            addressString.add(addressOfMarker.getThoroughfare());
                        }
                        if (addressOfMarker.getPostalCode() != null) {
                            addressString.add(addressOfMarker.getPostalCode());
                        }
                        if (addressString.isEmpty()) {
                            addressString.add("Unknown Location");
                        }
                        if (addressOfMarker.getLocality() != null) {
                            addressString.add(addressOfMarker.getLocality());
                        }
                        if (addressOfMarker.getAdminArea() != null) {
                            addressString.add(addressOfMarker.getAdminArea());
                        }
                    }
                    System.out.println("ADDRESS OF MARKER  IS" + "  " +  addressOfMarker.getThoroughfare() + "  " + addressOfMarker.getLocality() + "  " + addressOfMarker.getCountryName() + "  " + addressOfMarker.getPostalCode() + "\n");
                    Toast.makeText(MapsActivity.this,"ADDRESS OF MARKER IS:" + "  " +  addressOfMarker.getThoroughfare() + "  " +  addressOfMarker.getLocality() + "  " + addressOfMarker.getCountryName() + "  " +  addressOfMarker.getPostalCode() ,Toast.LENGTH_LONG).show();
                    return false;
                }
            });

            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                @Override
                public void onPolygonClick(Polygon polygon) {
                    Log.d(TAG, "onPolygonClick: " +polygon.getPoints());
                    float[] results = new float[1];
                    double distance = 0.0;

                    for (int i = 0; i<POLYGON_SIDES; i++) {

                        Location.distanceBetween(polygon.getPoints().get(i).latitude,polygon.getPoints().get(i).longitude,polygon.getPoints().get(i+1).latitude,polygon.getPoints().get(i+1).longitude,results);
                        distance +=  ((float) results[0])/1000;
                    }
                    Toast.makeText(MapsActivity.this, "Total Distance is: " +(String.format (Locale.CANADA,"%.2f KM",distance)) , Toast.LENGTH_SHORT).show();
                }
            });

            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {

//                Toast.makeText(MapsActivity.this, "onPolylineClick", Toast.LENGTH_SHORT).show();
                    LatLng location1 = polyline.getPoints().get(0);
                    LatLng location2 = polyline.getPoints().get(1);


//                double distance = getDistanceMeters(location1.latitude,location1.longitude,location2.latitude,location2.latitude);
                   // LatLng midValue = midPoint(location1.latitude, location1.longitude, location2.latitude, location2.longitude);

                    float[] results = new float[1];

                    Location.distanceBetween(location1.latitude, location1.longitude, location2.latitude, location2.longitude, results);

                    float distance = (results[0]) / 1000;

                    Toast.makeText(MapsActivity.this, "Distance is " + (String.format (Locale.CANADA,"%.2f KM",distance)), Toast.LENGTH_SHORT).show();

                }
            });

        }
        private void setMarker(LatLng latLng)
        {

            HashMap<String, String> markerPoint = new HashMap<>();
            markerPoint = geoCoder(latLng);

            Log.d(TAG, "setMarker: " +markerPoint.get("postalCode"));

            String letters = "A";
            if (markers.size() ==0) {
                letters = "A";
            }
            if (markers.size() ==1) {
                letters = "B";
            }
            if (markers.size() ==2) {
                letters = "C";
            }
            if (markers.size() ==3) {
                letters = "D";
            }

            MarkerOptions options = new MarkerOptions().position(latLng)
                    .draggable(true)
                    .title(letters);
                    //.title(markerPoint.get("thoroughfare") + "," + markerPoint.get("subThoroughfare" )+ "," + markerPoint.get("postalCode"))
            //.icon(BitmapDescriptorFactory.fromBitmap(makeBitmap(this,letters)));
                    //.snippet(markerPoint.get("locality") + "," + markerPoint.get("adminArea"));

            if(markers.size() == POLYGON_SIDES)
            {
                firstMarker = null;
                mMap.clear();
                clearMap();
            }

            Marker marker = mMap.addMarker(options);
            marker.getSnippet();
            drawLine(marker);
            firstMarker = marker;
            markers.add(marker);
            if(markers.size() == POLYGON_SIDES)
            {
                drawShape(); }
        }

        private void drawShape() {

            PolygonOptions options = new PolygonOptions()
                    .clickable(true)
                    .fillColor(0x3500FF00)
                    .strokeColor(Color.RED)
                    .strokeWidth(10);

            for(int i = 0; i < POLYGON_SIDES; i++){
                Marker marker = markers.get(i);
                latLngList.add(marker.getPosition());
            }

            for (Polyline polyline : polylineList){
                polyline.remove();
            }
            polylineList.clear();


            for (int i = 0; i < POLYGON_SIDES; i++) {

                int last;
                if (i+1 == POLYGON_SIDES){last = 0; }
                else {last = i+1;}

                drawLine(latLngList.get(i), latLngList.get(last));
            }

            for (LatLng latLng : latLngList)
            {
                options.add(latLng);
            }

            shape = mMap.addPolygon(options);
        }

        private void clearMap() {

            polylineList.clear();
            latLngList.clear();

            for (Marker marker : markers) {
                marker.remove();
            }

            markers.clear();
            shape.remove();
            line.remove();
            shape = null;
            line = null;

        }

        private void drawLine(LatLng latLng1, LatLng latLng2) {
            PolylineOptions options = new PolylineOptions()
                    .clickable(true)
                    .color(Color.RED)
                    .width(10)
                    .add(latLng1,latLng2);
            line = mMap.addPolyline(options);
        }

        private void drawLine(Marker marker)
        {
            if (firstMarker != null) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .clickable(true)
                        .color(Color.RED)
                        .width(20)
                        .add(marker.getPosition(), firstMarker.getPosition());
                polylineList.add(mMap.addPolyline(polylineOptions));
            }
        }

        public Bitmap makeBitmap(Context context, String text)
        {
            Resources resources = context.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_action_name);
            bitmap = bitmap.copy(ARGB_8888, true);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK); // Text color
            paint.setTextSize(20 * scale); // Text size
            paint.setShadowLayer(1f, 0f, 1f, Color.GRAY); // Text shadow
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            int x = bitmap.getWidth() - bounds.width() - 15 ; // 10 for padding from right
            int y = bounds.height();
            canvas.drawText(text, x, y, paint);

            return bitmap;
        }



        public HashMap<String, String> geoCoder(LatLng latLng) {

            HashMap<String, String> markerPoint = new HashMap<>();

            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    if (addresses.get(0).getAdminArea() != null)
                        markerPoint.put("adminArea", addresses.get(0).getAdminArea());

                    if (addresses.get(0).getLocality() != null)
                        markerPoint.put("locality", addresses.get(0).getLocality());

                    if (addresses.get(0).getPostalCode() != null)
                        markerPoint.put("postalCode", addresses.get(0).getPostalCode());

                    if (addresses.get(0).getThoroughfare() != null)
                        markerPoint.put("thoroughfare", addresses.get(0).getThoroughfare());

                    if (addresses.get(0).getSubThoroughfare() != null)
                        markerPoint.put("subThoroughfare", addresses.get(0).getSubThoroughfare());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "geoCoder: " +markerPoint.get("postalCode"));

            return markerPoint;
        }
//    private final int REQUEST_CODE = 1;
//    public Marker homeMarker;
//    Polygon shape;
//
//    public final int POLYGON_POINTS = 4;
//    List<Marker> markers = new ArrayList<>();
//    ArrayList<LatLng> locations = new ArrayList<>();
//
//    private GoogleMap mMap;
//
//    LocationManager locationManager;
//    LocationListener locationListener;
//
//    LatLng userLocation;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap)
//    {
//        mMap = googleMap;
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location)
//            {
//                // set the home location
//                setHomeLocation(location);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//            }
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//
//        if (!checkPermission())
//            requestPermission();
//        else
//            getLocation();
//
//        mMap.setOnMapLongClickListener(latLng -> {
//            if (markers.size() == 4)
//            {
//                clearMap();
//            }
//            Location location = new Location("Your Destination");
//            location.setLatitude(latLng.latitude);
//            location.setLongitude(latLng.longitude);
//            // set Marker
//            setMarker(location);
//        });
//        final Context context = this;
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
//        {
//            @Override
//            public boolean onMarkerClick(Marker marker)
//            {
//                Geocoder gc = new Geocoder(MapsActivity.this);
//                // List<Address> list = null;
//                List<Address> list;
//                LatLng ll = marker.getPosition();
//                try
//                {
//                    list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                    return false;
//                }
//                Address add = list.get(0);
//
//                //custom method to set Title of marker in TextView
//                // setValueInTextView(add.getLocality());
////                    Toast.makeText(context,"ADDRESS IS: "+ add.getPostalCode() + add.getLocality() + add.getThoroughfare() + add.getCountryName(),Toast.LENGTH_LONG).show();
//                Toast.makeText(context,"ADDRESS IS: " + add.getLocality() ,Toast.LENGTH_LONG).show();
//                //                   Toast.makeText(context,"YOU CLICKED ON "+ marker.getTitle(),Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
//    }
//    private void setHomeLocation(Location location)
//    {
//        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//
//        MarkerOptions options = new MarkerOptions().position(userLocation)
//                .title("User Location")
//                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//                .snippet("user is  here")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name));
//        homeMarker = mMap.addMarker(options);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 8));
//    }
//
//    private void setMarker(Location location)
//    {
//        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//        locations.add(userLatLng);
//
////            if (markers.size() == 4)
////            {
////                clearMap();
////            }
//
//        if(locations.size() == 1)
//        {
//            LatLng locationofuser = new LatLng(-31, 150);
//            float[] results = new float[1];
//            Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, locationofuser.latitude,locationofuser.longitude, results);
//            // Toast.makeText(this, results[0]/1000 + "Km", Toast.LENGTH_SHORT).show();
//            MarkerOptions options = new MarkerOptions().position(userLatLng)
//                    .title("A")
//                    .snippet(results[0]/1000 + "Km")
//                    .draggable(true);
//            markers.add(mMap.addMarker(options));
//
//        }
//        if(locations.size() == 2)
//        {
//            MarkerOptions options = new MarkerOptions().position(userLatLng)
//                    .title("B")
//                    .snippet("Calgary")
//                    .draggable(true);
//            markers.add(mMap.addMarker(options));
//        }
//        if(locations.size() == 3)
//        {
//            MarkerOptions options = new MarkerOptions().position(userLatLng)
//                    .title("C")
//                    .snippet("Calgary")
//                    .draggable(true);
//            markers.add(mMap.addMarker(options));
//        }
//        if(locations.size() == 4)
//        {
//            MarkerOptions options = new MarkerOptions().position(userLatLng)
//                    .title("D")
//                    .snippet("Calgary")
//                    .draggable(true);
//            markers.add(mMap.addMarker(options));
//            drawShape();
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private void getLocation()
//    {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
//        Location lastknownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        // set the known location as home location
//        setHomeLocation(lastknownLocation);
//    }
//
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//    }
//
//    private boolean checkPermission() {
//        int permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        return permissionStatus == PackageManager.PERMISSION_GRANTED;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (REQUEST_CODE == requestCode) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
//            }
//        }
//    }
//
//    private void clearMap()
//    {
//
//        for (Marker marker : markers)
//            marker.remove();
//
//        locations.clear();
//        markers.clear();
//        shape.remove();
//        shape = null;
//    }
//
//    private void drawShape()
//    {
//        PolygonOptions options = new PolygonOptions()
//                .fillColor(0x330000FF)
//                .strokeWidth(5)
//                .strokeColor(Color.RED);
//
//        for (int i=0; i<POLYGON_POINTS; i++)
//        {
//            options.add(markers.get(i).getPosition());
//        }
//        shape = mMap.addPolygon(options);
//    }
}


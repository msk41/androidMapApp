package com.example.msk.mapsample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.msk.mapsample.DB.SQLiteHelper;
import com.example.msk.mapsample.Model.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button addPostButton, postListButton;
    private String address;
    private double currentLatitude, currentLongitude;
    private ArrayList<Post> posts;

    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        init();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // create database
        mSQLiteHelper = new SQLiteHelper(this, "POSTDB.sqlite", null, 1);
        // create table in database
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS POST(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                    " comment VARCHAR, " +
                                                                    " image BLOB, " +
                                                                    " location VARCHAR, " +
                                                                    " latitude DOUBLE, " +
                                                                    " longitude DOUBLE, " +
                                                                    " postDate VARCHAR, " +
                                                                    " updatedDate VARCHAR) ");

        // get Post list
        posts = new ArrayList<>();
        Cursor cursor = mSQLiteHelper.getPost("SELECT * FROM POST");
        posts.clear();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String comment = cursor.getString(1);
                byte[] image = cursor.getBlob(2);
                String location = cursor.getString(3);
                double latitude = cursor.getDouble(4);
                double longitude = cursor.getDouble(5);
                String postDate = cursor.getString(6);
                String updatedDate = cursor.getString(7);
                posts.add(new Post(id, comment, image, location, latitude, longitude, postDate, updatedDate));
            }
        }

        // add Post record
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("address", address);
                intent.putExtra("latitude", currentLatitude);
                intent.putExtra("longitude", currentLongitude);
                startActivity(intent);
            }
        });

        // show Post list
        postListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostList.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLatitude  = location.getLatitude();
                currentLongitude = location.getLongitude();
                LatLng userLocation = new LatLng(currentLatitude, currentLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                // display markers of Post list
                for (Post post : posts) {
                    LatLng postLatLng = new LatLng(post.getLatitude(), post.getLongitude());
                    Bitmap postBitmap = BitmapFactory.decodeByteArray(post.getImage(), 0, post.getImage().length);
                    mMap.addMarker(new MarkerOptions()
                            .position(postLatLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(postBitmap)));
                }

                try {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (listAddresses != null && listAddresses.size() > 0) {
                        if (listAddresses.get(0).getCountryName() != null) {
                            address = "";
                            address += listAddresses.get(0).getCountryName() + ", ";
                        }
                        if (listAddresses.get(0).getAdminArea() != null) {
                            address += listAddresses.get(0).getAdminArea() + ", ";
                        }
                        if (listAddresses.get(0).getLocality() != null) {
                            address += listAddresses.get(0).getLocality();
                        }
                        Log.i("Location", address);
                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Register the listener with the Location Manager to receive location updates
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0, locationListener);
                }
            }
        }
    }

    private void init() {
        addPostButton = findViewById(R.id.addPostButton);
        postListButton = findViewById(R.id.postListButton);
    }
}

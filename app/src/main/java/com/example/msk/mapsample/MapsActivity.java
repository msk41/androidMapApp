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
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private ArrayList<Post> postList;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    public static SQLiteHelper mSQLiteHelper;

    public static final int REQUEST_CODE_ACCESS_FINE_LOCATION  = 1;
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_CODE_GALLERY = 3;
    public static final int REQUEST_CODE_CAMERA = 4;

    public final int THUMBNAIL_SIZE = 128;


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
                                                                    " image VARCHAR, " +
                                                                    " location VARCHAR, " +
                                                                    " latitude DOUBLE, " +
                                                                    " longitude DOUBLE, " +
                                                                    " postDate VARCHAR, " +
                                                                    " updatedDate VARCHAR) ");

        // get Post list
        postList = new ArrayList<>();
        Cursor cursor = mSQLiteHelper.getPost("SELECT image, " +
                                                         " latitude, " +
                                                         " longitude " +
                                                         " FROM POST ");
        postList.clear();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String image = cursor.getString(0);
                double latitude = cursor.getDouble(1);
                double longitude = cursor.getDouble(2);
                Post post = new Post();
                post.setImage(image);
                post.setLatitude(latitude);
                post.setLongitude(longitude);
                postList.add(post);
            }
        }

        // move to the Add Post page
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

        // move to the Post list page
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

        // check if API level is larger than 23
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_EXTERNAL_STORAGE);
            }
            else
            {
                // display markers of Post list
                for (Post post : postList) {
                    LatLng postLatLng = new LatLng(post.getLatitude(), post.getLongitude());
                    Uri imageUri = Uri.parse(post.getImage());
                    try {
                        Bitmap postBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                        Bitmap bitmap = getThumbnail(imageUri);
                        mMap.addMarker(new MarkerOptions()
                                .position(postLatLng));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // check if the map can be used
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
        else
        {
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            }

            // get the last location that the user is
            Location location = null;
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Log.d("Network", "Network Enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                }
            }

            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Register the listener with the Location Manager to receive location updates
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0, locationListener);
                    }
                }
                break;
            }
            case REQUEST_CODE_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do stuff
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    private void init() {
        addPostButton = findViewById(R.id.addPostButton);
        postListButton = findViewById(R.id.postListButton);
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream inputStream = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, onlyBoundsOptions);
        inputStream.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize  = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
                : onlyBoundsOptions.outWidth;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = originalSize / THUMBNAIL_SIZE;
        inputStream = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
        inputStream.close();
        return bitmap;
    }
}

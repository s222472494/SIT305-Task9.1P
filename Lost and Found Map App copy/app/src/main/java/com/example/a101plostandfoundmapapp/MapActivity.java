package com.example.a101plostandfoundmapapp;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.database.Cursor;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper db;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        db = new DatabaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map Fragment is null");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Cursor cursor = null;

        try {
            cursor = db.getAllAdverts();

            if (cursor == null || cursor.getCount() == 0) {
                Log.d(TAG, "No adverts found in database.");
                LatLng defaultLocation = new LatLng(-37.8136, 144.9631); // Melbourne
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
                return;
            }

            LatLng firstItemLocation = null;

            while (cursor.moveToNext()) {
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                LatLng itemLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(itemLocation)
                        .title(type + ": " + name));

                Log.d(TAG, "Added marker at: " + latitude + ", " + longitude + " - " + type + ": " + name);

                if (firstItemLocation == null) {
                    firstItemLocation = itemLocation;
                }
            }

            if (firstItemLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstItemLocation, 12));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error reading from database or adding markers", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // Prevent memory leaks
            }
        }
    }
}

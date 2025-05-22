package com.example.a101plostandfoundmapapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapPickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker selectedMarker;
    private LatLng selectedLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(-37.8136, 144.9631); // Melbourne
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
            }

            selectedLatLng = latLng;

            selectedMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location"));

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            sendResultBack();
        });
    }

    private void sendResultBack() {
        if (selectedLatLng == null) {
            Toast.makeText(this, "Please tap on the map to select a location.", Toast.LENGTH_SHORT).show();
            return;
        }

        String address = getAddressFromLatLng(selectedLatLng);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", selectedLatLng.latitude);
        resultIntent.putExtra("longitude", selectedLatLng.longitude);
        resultIntent.putExtra("address", address);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude;
    }
}

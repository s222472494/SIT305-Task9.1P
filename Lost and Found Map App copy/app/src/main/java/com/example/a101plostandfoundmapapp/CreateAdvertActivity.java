package com.example.a101plostandfoundmapapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class CreateAdvertActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2001;
    private static final int MAP_PICKER_REQUEST_CODE = 3003;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 4004;

    private static final String TAG = "CreateAdvertActivity";

    private EditText nameEditText, phoneEditText, descriptionEditText, dateEditText, locationEditText;
    private Spinner typeSpinner;
    private Button submitButton, useMyLocationButton, pickFromMapButton;

    private String selectedType = "";
    private double latitude = 0.0;
    private double longitude = 0.0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseHelper databaseHelper;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        Log.d(TAG, "onCreate: Initializing Places and UI elements");

        // Initialize Places SDK if not already initialized
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "YOUR API KEY");
            Log.d(TAG, "Places SDK initialized");
        }
        placesClient = Places.createClient(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // UI Elements
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        typeSpinner = findViewById(R.id.typeSpinner);
        submitButton = findViewById(R.id.submitButton);
        useMyLocationButton = findViewById(R.id.getLocationButton);
        pickFromMapButton = findViewById(R.id.pickFromMapButton);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.advert_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedType = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Spinner selected: " + selectedType);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                selectedType = "";
                Log.d(TAG, "Spinner nothing selected");
            }
        });

        databaseHelper = new DatabaseHelper(this);

        // Disable direct typing in locationEditText and enable clicking to launch Places Autocomplete
        locationEditText.setFocusable(false);
        locationEditText.setClickable(true);
        locationEditText.setOnClickListener(v -> {
            Log.d(TAG, "locationEditText clicked, launching autocomplete");
            launchPlaceAutocomplete();
        });

        useMyLocationButton.setOnClickListener(v -> {
            Log.d(TAG, "useMyLocationButton clicked, requesting location");
            requestCurrentLocation();
        });

        pickFromMapButton.setOnClickListener(v -> {
            Log.d(TAG, "pickFromMapButton clicked, launching MapPickerActivity");
            Intent intent = new Intent(CreateAdvertActivity.this, MapPickerActivity.class);
            startActivityForResult(intent, MAP_PICKER_REQUEST_CODE);
        });

        submitButton.setOnClickListener(v -> {
            Log.d(TAG, "submitButton clicked, validating and saving advert");
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();

            if (selectedType.isEmpty() || name.isEmpty() || phone.isEmpty() || description.isEmpty()
                    || date.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Validation failed: Some fields empty");
                return;
            }

            if (!phone.matches("\\d+")) {
                Toast.makeText(this, "Phone must be numeric.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Validation failed: Phone not numeric");
                return;
            }

            boolean success = databaseHelper.insertAdvert(
                    selectedType, name, phone, description, date, location, latitude, longitude);

            Toast.makeText(this, success ? "Advert saved successfully." : "Error saving advert.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Advert saved: " + success);
            if (success) clearForm();
        });
    }

    private void launchPlaceAutocomplete() {
        // Define the fields required from the Place object
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("AU")  // Optional: restrict to Australia
                .build(this);

        Log.d(TAG, "Starting Autocomplete Activity");
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void requestCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission not granted, requesting permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locationEditText.setText("Lat: " + latitude + ", Lng: " + longitude);
                Log.d(TAG, "Got current location: Lat=" + latitude + ", Lng=" + longitude);
            } else {
                Toast.makeText(this, "Could not get current location.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Location is null");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to get location", e);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult called with requestCode: " + requestCode);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted");
                requestCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Location permission denied");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult called with requestCode=" + requestCode + ", resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d(TAG, "Autocomplete place selected: " + place.getName() + ", " + place.getAddress());
                Toast.makeText(this, "Place: " + place.getName(), Toast.LENGTH_SHORT).show();

                locationEditText.setText(place.getAddress());
                if (place.getLatLng() != null) {
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    Log.d(TAG, "Place LatLng: " + latitude + ", " + longitude);
                } else {
                    Log.d(TAG, "Place LatLng is null");
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, "Autocomplete error: " + status.getStatusMessage());
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Autocomplete canceled by user");
            } else {
                Log.d(TAG, "Autocomplete returned unexpected resultCode or null data");
            }
        } else if (requestCode == MAP_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            latitude = data.getDoubleExtra("latitude", 0.0);
            longitude = data.getDoubleExtra("longitude", 0.0);
            String address = data.getStringExtra("address");
            locationEditText.setText(address);
            Log.d(TAG, "MapPicker location: Lat=" + latitude + ", Lng=" + longitude + ", address=" + address);
        } else {
            Log.d(TAG, "onActivityResult: Unhandled requestCode or resultCode");
        }
    }

    private void clearForm() {
        Log.d(TAG, "Clearing form");
        nameEditText.setText("");
        phoneEditText.setText("");
        descriptionEditText.setText("");
        dateEditText.setText("");
        locationEditText.setText("");
        latitude = 0.0;
        longitude = 0.0;
        typeSpinner.setSelection(0);
    }
}


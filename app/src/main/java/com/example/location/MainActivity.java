package com.example.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager lm;
    TextView tv;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        tv = findViewById(R.id.t1);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if permission is granted, otherwise request it
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request Location permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, start getting location updates
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);

            // Get the last known location from both providers (if available)
            Location lastKnownGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownNetwork = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastKnownGPS != null) {
                onLocationChanged(lastKnownGPS); // Use GPS location if available
            } else if (lastKnownNetwork != null) {
                onLocationChanged(lastKnownNetwork); // Fallback to network location if GPS is unavailable
            } else {
                tv.setText("Waiting for location...");
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Display the latitude and longitude in the TextView
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        tv.setText("Latitude: " + lat + " ** Longitude: " + lon);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), provider + " Disabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(), provider + " Enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // This method is deprecated, but required for backward compatibility
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start getting location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, this);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);

                    Location lastKnownGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location lastKnownNetwork = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (lastKnownGPS != null) {
                        onLocationChanged(lastKnownGPS); // Use GPS location if available
                    } else if (lastKnownNetwork != null) {
                        onLocationChanged(lastKnownNetwork); // Fallback to network location
                    }
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


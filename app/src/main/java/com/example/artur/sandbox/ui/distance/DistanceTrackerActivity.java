package com.example.artur.sandbox.ui.distance;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artur.sandbox.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DistanceTrackerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DistanceTrackerActivity.class.getSimpleName();

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private Button startTrackingButton, stopTrackingButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_tracker);

        startTrackingButton = findViewById(R.id.start_tracking_button);
        startTrackingButton.setOnClickListener(this);
        stopTrackingButton = findViewById(R.id.stop_tracking_button);
        stopTrackingButton.setOnClickListener(this);
        textView = findViewById(R.id.distance_tracker_text_view);

        createLocationCallback();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location currentLocation = locationResult.getLastLocation();
                String latitude = String.format(Locale.ENGLISH, "%s: %f", getString(R.string.latitude_label), currentLocation.getLatitude());
                String longitude = String.format(Locale.ENGLISH, "%s: %f", getString(R.string.longitude_label), currentLocation.getLongitude());
                String lastUpdateTime = String.format(Locale.ENGLISH, "%s: %s", getString(R.string.last_update_time_label), DateFormat.getTimeInstance().format(new Date()));
                String locationString = latitude + "\n" + longitude + "\n" + lastUpdateTime + "\n";

                textView.append(locationString);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_tracking_button:
                startLocationUpdates();
                break;
            case R.id.stop_tracking_button:
                stopLocationUpdates();
                break;
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        mSettingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");

                    boolean grantedFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    boolean grantedCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    if (grantedFine && grantedCoarse) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                    } else {
                        String noPermissions = "Call requires permission which may be rejected by user";
                        textView.setText(noPermissions);
                    }
                    showTracking(true);
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(DistanceTrackerActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);
                            Toast.makeText(DistanceTrackerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                    showTracking(false);
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, task -> showTracking(false));
    }

    private void showTracking(boolean show) {
        startTrackingButton.setVisibility(show ? View.GONE : View.VISIBLE);
        stopTrackingButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        showTracking(false);
                        break;
                }
                break;
        }
    }
}

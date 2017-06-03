package com.pbmchc.emzetka.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.managers.NearStopsMapManager;
import com.pbmchc.emzetka.managers.UserLocationManager;
import com.pbmchc.emzetka.mzkandroid.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearStopsMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        UserLocationManager.onLocationReceivedListener, NearStopsMapManager.OnMapReadyListener {

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 2;

    @BindView(R.id.progressBarLayout) FrameLayout progressBar;
    @BindView(R.id.fabLocation) FloatingActionButton fabLocation;
    @BindView(R.id.stopNumberTxt) TextView stopsNumberTxt;

    private UserLocationManager locationManager;
    private NearStopsMapManager mapManager;
    private SupportMapFragment mapFragment;
    private LatLng center;
    private Integer stopCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_stops_map);
        ButterKnife.bind(this);

        locationManager = new UserLocationManager(this, this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(NearStopsMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && locationManager.checkLocationServicesStatus()){
                    if (locationManager.isConnectedToGoogleApiClient())
                        mapManager.getNearStops(center);
                    else
                        locationManager.connectToGoogleApiClient();
                }
                else if (locationManager.checkLocationServicesStatus())
                    showToastMessage(getString(R.string.location_services_no_permission_toast_message));
                else
                    showToastMessage(getString(R.string.location_services_error_toast_message));
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart(){
        if (!locationManager.checkLocationServicesStatus())
            showToastMessage(getString(R.string.location_services_error_toast_message));
        if (ContextCompat.checkSelfPermission(NearStopsMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            requestLocationPermission();
        else
            locationManager.connectToGoogleApiClient();
        super.onStart();
    }

    @Override
    protected void onStop(){
        if (locationManager.isConnectedToGoogleApiClient())
            locationManager.disconnectFromGoogleApiClient();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapManager = new NearStopsMapManager(this, googleMap);
    }

    @Override
    public void onInfoWindowClicked(Marker marker){
        String stopLineId = (String) marker.getTag();
        Intent intent = new Intent(NearStopsMapActivity.this, SingleStopActivity.class);
        intent.putExtra("stopId", stopLineId);
        intent.putExtra("stopName", marker.getTitle());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == android.R.id.home)
            onBackPressed();
        return false;
    }

    public void onMapContentReady(int stopCount) {
        this.stopCount = stopCount;
        String stopNumber = String.valueOf(stopCount);
        stopsNumberTxt.setText(stopNumber);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onMapContentError() {
        NetworkHelper.checkErrorCause(NearStopsMapActivity.this);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLocationReady(Location location) {
        if (location != null && stopCount == null) {
            center = new LatLng(location.getLatitude(), location.getLongitude());
            mapManager.getNearStops(center);
        }
        else if (locationManager.checkLocationServicesStatus())
            locationManager.startLocationUpdates();
    }

    @Override
    public void onLocationUpdate(Location location) {
        center = new LatLng(location.getLatitude(), location.getLongitude());
        mapManager.updateUserMarkerPosition(center);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locationManager.connectToGoogleApiClient();
                else {
                    Snackbar.make(findViewById(R.id.map), getString(R.string.location_services_snackbar_info), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.location_services_turn_on_label), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestLocationPermission();
                                }
                            })
                            .setActionTextColor(Color.WHITE)
                            .show();
                }
                break;
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(NearStopsMapActivity.this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                PERMISSION_ACCESS_FINE_LOCATION);
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

package com.pbmchc.emzetka.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Piotrek on 2017-06-02.
 */
public class UserLocationManager implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final int LOCATION_REQUEST_INTERVAL = 10000;
    private static final int LOCATION_REQUEST_SHORT_INTERVAL = 5000;

    private Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private onLocationReceivedListener listener;

    public UserLocationManager(Context context, onLocationReceivedListener listener) {
        this.context = context;
        this.listener = listener;
        if (googleApiClient == null)
            googleApiClient = new GoogleApiClient.Builder(this.context, this, this)
                    .addApi(LocationServices.API).build();
    }

    public boolean isConnectedToGoogleApiClient() {
        return googleApiClient.isConnected();
    }

    public void connectToGoogleApiClient() {
        googleApiClient.connect();
    }

    public void disconnectFromGoogleApiClient() {
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            listener.onLocationReady(userLocation);
            if (checkLocationServicesStatus())
                startLocationUpdates();
        }

    }

    public void startLocationUpdates(){
        createLocationRequest();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    protected void createLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_REQUEST_SHORT_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(UserLocationManager.class.getSimpleName(), "Location services suspended");
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        listener.onLocationUpdate(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(UserLocationManager.class.getSimpleName(),
                "Location services connection failed with code " + connectionResult.getErrorCode());
    }

    public boolean checkLocationServicesStatus() {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    public interface onLocationReceivedListener {
        void onLocationReady(Location location);
        void onLocationUpdate(Location location);
    }
}

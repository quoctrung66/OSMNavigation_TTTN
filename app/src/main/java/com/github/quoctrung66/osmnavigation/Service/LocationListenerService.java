package com.github.quoctrung66.osmnavigation.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by QUOC TRUNG on 4/28/2016.
 */
public class LocationListenerService extends Service{
    //TAG name
    private static final String TAG = LocationListenerService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 1000;
    private static int FATEST_INTERVAL = 1000;
    private static int DISPLACEMENT = 0;

    private mLocationListener locationListener;
    private mConnectionCallbacks connectionCallbacks;
    private mOnConnectionFailedListener onConnectionFailedListener;

    private LocationChanged locationChanged;
    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public LocationListenerService getService() {
            return LocationListenerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationListener = new mLocationListener();
        connectionCallbacks = new mConnectionCallbacks();
        onConnectionFailedListener = new mOnConnectionFailedListener();
        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "checkSelfPermission");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
    }
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }

    private class mConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle arg0) {
            if (mGoogleApiClient.isConnected()){
                startLocationUpdates();
            }
        }

        @Override
        public void onConnectionSuspended(int arg0) {
            mGoogleApiClient.connect();
        }
    }
    private class mOnConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i(TAG, connectionResult.toString());
        }
    }
    private class mLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (LocationListenerService.this.locationChanged != null) {
                locationChanged.onLocationChanged(location);
            }
        }
    }

    public void addLocationListener(LocationChanged locationChanged){
        LocationListenerService.this.locationChanged = locationChanged;
    }

    public interface LocationChanged{
        void onLocationChanged(Location location);
    }
}

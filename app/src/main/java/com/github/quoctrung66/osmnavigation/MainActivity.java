package com.github.quoctrung66.osmnavigation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.quoctrung66.osmnavigation.Drawer.DrawIcon;
import com.github.quoctrung66.osmnavigation.Handler.HandleView;
import com.github.quoctrung66.osmnavigation.Handler.ReadFileLocation;
import com.github.quoctrung66.osmnavigation.Helper.Constant;
import com.github.quoctrung66.osmnavigation.Service.LocationListenerService;
import com.github.quoctrung66.osmnavigation.View.MapViewCustom;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

public class MainActivity extends AppCompatActivity {
    //TAG
    private static final String TAG = MainActivity.class.getSimpleName();

    //LocationService
    private Intent locationService;
    private ServiceConnection serviceConnection;
    private LocationListenerService locationListenerService;

    //LocationFile
    private ReadFileLocation readfile;

    //Drawer locationGPS
    DrawIcon drawerGPS;
    DrawIcon drawerFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Kiểm tra kết nối đến GPS và Network
        new HandleView(MainActivity.this).inicheck();

        //Setting map
        MapViewCustom mapView = (MapViewCustom) findViewById(R.id.map);
        assert mapView != null;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.addTapListener(new MapViewCustom.OnTapListener() {
            @Override
            public void onMapTapped(GeoPoint geoPoint) {

            }
        });
        //Map Controller
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        mapController.animateTo(Constant.HCMUT);

        //Drawer
        drawerGPS = new DrawIcon(MainActivity.this, mapView, null);
        drawerFile = new DrawIcon(MainActivity.this, mapView, null);

        //Location Service
        locationService = new Intent(MainActivity.this, LocationListenerService.class);
        startService(locationService);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                locationListenerService = ((LocationListenerService.LocalBinder) service).getService();
                LocationChanged locationChange = new LocationChanged();
                locationListenerService.addLocationListener(locationChange);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(MainActivity.this, LocationListenerService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        //Location File
        ReadFileListener readFileListener = new ReadFileListener();
        readfile = new ReadFileLocation(MainActivity.this, "TU HCMUT DEN IIG.txt");
        readfile.addReadFileListener(readFileListener);
        readfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LocationChanged implements LocationListenerService.LocationChanged {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG + this.getClass().getSimpleName(), location.getLatitude() + ", "  + location.getLongitude());
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            drawerGPS.updateLocation(geoPoint, 5f, 45f, new int[]{255, 0, 0});
        }
    }

    private class ReadFileListener implements ReadFileLocation.ReadFileListener {
        @Override
        public void onReadLine(Location location) {
            Log.i(TAG + this.getClass().getSimpleName(), location.getLatitude() + ", "  + location.getLongitude());
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            drawerFile.updateLocation(geoPoint, 5f, 45f, new int[]{0, 0, 255});
        }
    }

    protected void onClickFab(View view){
        readfile.toggle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        stopService(locationService);
    }
}

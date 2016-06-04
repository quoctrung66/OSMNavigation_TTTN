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
import com.github.quoctrung66.osmnavigation.Drawer.DrawPath;
import com.github.quoctrung66.osmnavigation.Handler.HandleView;
import com.github.quoctrung66.osmnavigation.Handler.NominatimAPI.StreetNominatimParser;
import com.github.quoctrung66.osmnavigation.Handler.OpenstreetmapAPI.MapDataParser;
import com.github.quoctrung66.osmnavigation.Handler.OpenstreetmapAPI.StreetDetailParser;
import com.github.quoctrung66.osmnavigation.Handler.OverpassAPI.OverPassBBoxParser;
import com.github.quoctrung66.osmnavigation.Handler.ReadFileLocation;
import com.github.quoctrung66.osmnavigation.Helper.Constant;
import com.github.quoctrung66.osmnavigation.Model.MyLocation;
import com.github.quoctrung66.osmnavigation.Model.WayStreet;
import com.github.quoctrung66.osmnavigation.Service.LocationListenerService;
import com.github.quoctrung66.osmnavigation.Utils.CalculateMap;
import com.github.quoctrung66.osmnavigation.Utils.OSMBounsPack;
import com.github.quoctrung66.osmnavigation.View.MapViewCustom;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;


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
    DrawPath drawPathGoal;

    //Handle Case
    ArrayList<MyLocation> locationHistory;
    MyLocation locationCurrent;
    GeoPoint mGeoPointGoal = null;

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
                mGeoPointGoal = geoPoint;
            }
        });
        //Map Controller
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        mapController.animateTo(Constant.HCMUT);

        //Drawer
        drawerGPS = new DrawIcon(MainActivity.this, mapView, null);
        drawerFile = new DrawIcon(MainActivity.this, mapView, null);
        drawPathGoal = new DrawPath(MainActivity.this, mapView);

        //Handle Case
        locationHistory = new ArrayList<>();

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
//            Log.i(TAG + this.getClass().getSimpleName(), location.getLatitude() + ", "  + location.getLongitude());
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            drawerGPS.updateLocation(geoPoint, 5f, 45f, new int[]{255, 0, 0});
        }
    }

    private class ReadFileListener implements ReadFileLocation.ReadFileListener {
        @Override
        public void onReadLine(Location location) {
            long time_start = System.currentTimeMillis();
            Log.i(TAG + this.getClass().getSimpleName(), location.getLatitude() + ", "  + location.getLongitude());
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            drawerFile.updateLocation(geoPoint, 5f, 45f, new int[]{0, 0, 255});

            locationCurrent = new MyLocation();
            locationCurrent.setmLocation(location);
            locationCurrent.setmGeoPoint(geoPoint);

            if (locationHistory.size() > 0){
                Road road_previous = OSMBounsPack.getRoad(MainActivity.this, locationHistory.get(0).getmGeoPoint(), locationCurrent.getmGeoPoint());
                locationCurrent.setmDistanceToPrevious(road_previous.mLength*1000);
                locationCurrent.setmAngleVehicle(CalculateMap.getAngle(locationHistory.get(0).getmGeoPoint(), locationCurrent.getmGeoPoint()));
                if (road_previous.mLength > 1){
                    locationCurrent.setmAngleRoad(CalculateMap.getAngle(road_previous.mRouteHigh.get(0), road_previous.mRouteHigh.get(1)));
                }
            }

            if (mGeoPointGoal != null){
                Road road_goal = OSMBounsPack.getRoad(MainActivity.this, locationCurrent.getmGeoPoint(), mGeoPointGoal);
                locationCurrent.setmDistanceToGoal(road_goal.mLength*1000);
                if (road_goal.mLength > 1){
                    locationCurrent.setmAngleRoad(CalculateMap.getAngle(road_goal.mRouteHigh.get(0), road_goal.mRouteHigh.get(1)));
                }
            }

            Log.i(TAG + " AngleVehicle", String.valueOf(locationCurrent.getmAngleVehicle()));
            Log.i(TAG + " DistanceToPrevious", String.valueOf(locationCurrent.getmDistanceToPrevious()));
            Log.i(TAG + " AngleRoad", String.valueOf(locationCurrent.getmAngleRoad()));
            Log.i(TAG + " DistanceToGoal", String.valueOf(locationCurrent.getmDistanceToGoal()));

            Log.i(TAG + this.getClass().getSimpleName(), String.valueOf(System.currentTimeMillis() - time_start));
            Log.i(TAG + this.getClass().getSimpleName(), "-----------------------------------------------------");
            locationHistory.add(0, locationCurrent);
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

package com.github.quoctrung66.osmnavigation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
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

    private MapViewCustom mapView;

    //LocationService



    private Intent locationService;
    private ServiceConnection serviceConnection;
    private LocationListenerService locationListenerService;

    //LocationFile
    private ReadFileLocation readfile;

    //Drawer locationGPS
    private DrawIcon drawerGPS;
    private DrawIcon drawerFile;
    private DrawPath drawPathGoal;
    private DrawPath drawPathPrevious;
    private DrawPath drawPathGoal_old;

    //Handle Case
    private ArrayList<MyLocation> locationHistory;
    private MyLocation locationCurrent;
    private GeoPoint mGeoPointGoal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Kiểm tra kết nối đến GPS và Network
        new HandleView(MainActivity.this).inicheck();

        //Setting map
        mapView = (MapViewCustom) findViewById(R.id.map);
        assert mapView != null;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.addTapListener(new MapViewCustom.OnTapListener() {
            @Override
            public void onMapTapped(GeoPoint geoPoint) {
                mGeoPointGoal = geoPoint;
            }
        });
        mGeoPointGoal = Constant.IIG;
        //Map Controller
        IMapController mapController = mapView.getController();
        mapController.setZoom(18);
        mapController.animateTo(Constant.HCMUT);

        //Drawer
        drawerGPS = new DrawIcon(MainActivity.this, mapView, null);
        drawerFile = new DrawIcon(MainActivity.this, mapView, null);
        drawPathGoal = new DrawPath(MainActivity.this, mapView);
        drawPathPrevious = new DrawPath(MainActivity.this, mapView);
        drawPathGoal_old = new DrawPath(MainActivity.this, mapView);

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
        readfile = new ReadFileLocation(MainActivity.this, "TU HCMUT VE NHA.txt");
        readfile.addReadFileListener(readFileListener);
        readfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LocationChanged implements LocationListenerService.LocationChanged {
        @Override
        public void onLocationChanged(Location location) {
            long time_start = System.currentTimeMillis();
            Log.i(TAG + " GeoPoint", location.getLatitude() + ", "  + location.getLongitude());
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            drawerFile.updateLocation(geoPoint, 5f, 45f, new int[]{0, 0, 255});

            mapView.getController().animateTo(geoPoint);

            locationCurrent = new MyLocation();
            locationCurrent.setmLocation(location);
            locationCurrent.setmGeoPoint(geoPoint);

            StreetNominatimParser streetNominatimParser = new StreetNominatimParser();
            WayStreet wayStreet_geo = streetNominatimParser.StreetIDParser(geoPoint.getLatitude(), geoPoint.getLongitude(), 16);
//            Log.i(TAG + " WayID_old", wayStreet_geo.getId());

            if (locationHistory.size() > 0){
                int position = CalculateMap.getSegmentContainGPS(wayStreet_geo.getmNodeStreet(), locationCurrent.getmGeoPoint());
                GeoPoint result = locationCurrent.getmGeoPoint();
                if (position != -1) {
                    result = CalculateMap.getGeoPointProjection(wayStreet_geo.getmNodeStreet().get(position).getGeoPoint(), wayStreet_geo.getmNodeStreet().get(position + 1).getGeoPoint(), locationCurrent.getmGeoPoint());
                }

                Road road_previous = OSMBounsPack.getRoad(MainActivity.this, locationHistory.get(0).getmGeoPointOnRoad(), result);
                locationCurrent.setmDistanceToPrevious(road_previous.mLength*1000);
                locationCurrent.setmAngleVehicle(CalculateMap.getAngle(locationHistory.get(0).getmGeoPoint(), locationCurrent.getmGeoPoint()));
                int size = road_previous.mRouteHigh.size();
                if (size > 1){
                    locationCurrent.setmAngleRoad(CalculateMap.getAngle(road_previous.mRouteHigh.get(size - 2), road_previous.mRouteHigh.get(size - 1)));
                }
                else{
                    locationCurrent.setmAngleRoad(locationHistory.get(0).getmAngleRoad());
                }
//                drawPathPrevious.updateDrawPath(road_previous.mRouteHigh, Color.GREEN, 7);
            }

            if (mGeoPointGoal != null){
                Road road_goal = OSMBounsPack.getRoad(MainActivity.this, locationCurrent.getmGeoPoint(), mGeoPointGoal);
                locationCurrent.setmDistanceToGoal(road_goal.mLength*1000);
                drawPathGoal_old.updateDrawPath(road_goal.mRouteHigh, Color.BLUE, 7);
            }


            if (locationHistory.size() > 0){
                //TH1 vị trị trước và hiện tại có cùng way_id -> vẫn đi trên một đường
                if (wayStreet_geo.getId().equals(locationHistory.get(0).getmWayStreet().getId())){
                    Log.i(TAG + " CASE", "TH1");
                    locationCurrent.setmWayStreet(wayStreet_geo);
                }
                else{
                    MapDataParser mapDataParser = new MapDataParser();
                    double offset = CalculateMap.getDistance(geoPoint, locationHistory.get(0).getmGeoPoint()) + 1;
                    ArrayList<WayStreet> listwayStreet_mapData = mapDataParser.ParserNode(geoPoint, offset/100000);
                    boolean check_current_1 = false;
                    boolean check_history_1 = false;
                    for (int i = 0; i < listwayStreet_mapData.size(); i++){
                        if (wayStreet_geo.getId().equals(listwayStreet_mapData.get(i).getId())) check_current_1 = true;
                        if (locationHistory.get(0).getmWayStreet().getId().equals(listwayStreet_mapData.get(i).getId())) check_history_1 = true;
                    }
                    //TH2 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
                    //      Không tìm ra điểm chung giữa hai vị trí
                    if (!check_current_1 && !check_history_1){
                        if (Math.abs(locationCurrent.getmAngleRoad() - locationCurrent.getmAngleVehicle()) < 10){
                            Log.i(TAG + " CASE", "TH2 - 1");
                            locationCurrent.setmWayStreet(wayStreet_geo);
                        }
                        else{
                            Log.i(TAG + " CASE", "TH2 - 2");
                            locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
                        }
                    }

                    //TH3 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
                    //      Không tìm ra điểm chung giữa hai vị trí
                    if (check_current_1 && !check_history_1){
                        if (Math.abs(locationCurrent.getmAngleRoad() - locationCurrent.getmAngleVehicle()) < 10){
                            Log.i(TAG + " CASE", "TH3 - 1");
                            locationCurrent.setmWayStreet(wayStreet_geo);
                        }
                        else{
                            Log.i(TAG + " CASE", "TH3 - 2");
                            locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
                        }
                    }
                    //TH4 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
                    //      Không tìm ra đường đi chung giữa hai vị trí
                    if (!check_current_1 && check_history_1){
                        Log.i(TAG + " CASE", "TH4");
                        locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
                    }
                    //TH5 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
                    //      Có đường đi chung giữa hai vị trí
                    if (check_current_1 && check_history_1){
                        if (Math.abs(locationCurrent.getmAngleRoad() - locationCurrent.getmAngleVehicle()) < 10){
                            Log.i(TAG + " CASE", "TH5 - 1");
                            locationCurrent.setmWayStreet(wayStreet_geo);
                        }
                        else{
                            Log.i(TAG + " CASE", "TH5 - 2");
                            locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
                        }
                    }
                }
            }
            else {
                locationCurrent.setmWayStreet(wayStreet_geo);
            }
            Log.i(TAG + " WayID_new", locationCurrent.getmWayStreet().getId());
            int position = CalculateMap.getSegmentContainGPS(locationCurrent.getmWayStreet().getmNodeStreet(), locationCurrent.getmGeoPoint());
            Log.i(TAG + " POSITION", String.valueOf(position));
            if (position != -1) {
                GeoPoint result = CalculateMap.getGeoPointProjection(locationCurrent.getmWayStreet().getmNodeStreet().get(position).getGeoPoint(), locationCurrent.getmWayStreet().getmNodeStreet().get(position + 1).getGeoPoint(), locationCurrent.getmGeoPoint());
                locationCurrent.setmGeoPointOnRoad(result);
                Log.i(TAG + " GeoPointOnRoad", result.getLatitude() + "," + result.getLongitude());
            }
            else {
                locationCurrent.setmGeoPointOnRoad(locationCurrent.getmGeoPoint());
            }

            if (mGeoPointGoal != null) {
                Road roadToGoal = OSMBounsPack.getRoad(MainActivity.this, locationCurrent.getmGeoPointOnRoad(), mGeoPointGoal);
                drawPathGoal.updateDrawPath(roadToGoal.mRouteHigh, Color.RED, 7);
            }

            Log.i(TAG + " AngleVehicle", String.valueOf(locationCurrent.getmAngleVehicle()));
            Log.i(TAG + " AngleRoad", String.valueOf(locationCurrent.getmAngleRoad()));

            Log.i(TAG + " Time", String.valueOf(System.currentTimeMillis() - time_start));
            Log.i(TAG, "-----------------------------------------------------");
            locationHistory.add(0, locationCurrent);
        }
    }

    private class ReadFileListener implements ReadFileLocation.ReadFileListener {
        @Override
        public void onReadLine(Location location) {
//            long time_start = System.currentTimeMillis();
//            Log.i(TAG + " GeoPoint", location.getLatitude() + ", "  + location.getLongitude());
//            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//            drawerFile.updateLocation(geoPoint, 5f, 45f, new int[]{0, 0, 255});
//
//            mapView.getController().animateTo(geoPoint);
//
//            locationCurrent = new MyLocation();
//            locationCurrent.setmLocation(location);
//            locationCurrent.setmGeoPoint(geoPoint);
//
//            StreetNominatimParser streetNominatimParser = new StreetNominatimParser();
//            WayStreet wayStreet_geo = streetNominatimParser.StreetIDParser(geoPoint.getLatitude(), geoPoint.getLongitude(), 16);
//            Log.i(TAG + " WayID_old", wayStreet_geo.getId());
//
//            if (locationHistory.size() > 0){
//                int position = CalculateMap.getSegmentContainGPS(wayStreet_geo.getmNodeStreet(), locationCurrent.getmGeoPoint());
//                GeoPoint result = locationCurrent.getmGeoPoint();
//                if (position != -1) {
//                    result = CalculateMap.getGeoPointProjection(wayStreet_geo.getmNodeStreet().get(position).getGeoPoint(), wayStreet_geo.getmNodeStreet().get(position + 1).getGeoPoint(), locationCurrent.getmGeoPoint());
//                }
//
//                Road road_previous = OSMBounsPack.getRoad(MainActivity.this, locationHistory.get(0).getmGeoPointOnRoad(), result);
//                locationCurrent.setmDistanceToPrevious(road_previous.mLength*1000);
//                locationCurrent.setmAngleVehicle(CalculateMap.getAngle(locationHistory.get(0).getmGeoPoint(), locationCurrent.getmGeoPoint()));
//                int size = road_previous.mRouteHigh.size();
//                if (size > 1){
//                    locationCurrent.setmAngleRoad(CalculateMap.getAngle(road_previous.mRouteHigh.get(size - 2), road_previous.mRouteHigh.get(size - 1)));
//                }
//                else{
//                    locationCurrent.setmAngleRoad(locationHistory.get(0).getmAngleRoad());
//                }
////                drawPathPrevious.updateDrawPath(road_previous.mRouteHigh, Color.GREEN, 7);
//            }
//
//            if (mGeoPointGoal != null){
//                Road road_goal = OSMBounsPack.getRoad(MainActivity.this, locationCurrent.getmGeoPoint(), mGeoPointGoal);
//                locationCurrent.setmDistanceToGoal(road_goal.mLength*1000);
//                drawPathGoal_old.updateDrawPath(road_goal.mRouteHigh, Color.BLUE, 7);
//            }
//
//
//            if (locationHistory.size() > 0){
//                //TH1 vị trị trước và hiện tại có cùng way_id -> vẫn đi trên một đường
//                if (wayStreet_geo.getId().equals(locationHistory.get(0).getmWayStreet().getId())){
//                    Log.i(TAG + " CASE", "TH1");
//                    locationCurrent.setmWayStreet(wayStreet_geo);
//                }
//                else{
//                    MapDataParser mapDataParser = new MapDataParser();
//                    double offset = CalculateMap.getDistance(geoPoint, locationHistory.get(0).getmGeoPoint()) + 1;
//                    ArrayList<WayStreet> listwayStreet_mapData = mapDataParser.ParserNode(geoPoint, offset/100000);
//                    boolean check_current_1 = false;
//                    boolean check_history_1 = false;
//                    for (int i = 0; i < listwayStreet_mapData.size(); i++){
//                        if (wayStreet_geo.getId().equals(listwayStreet_mapData.get(i).getId())) check_current_1 = true;
//                        if (locationHistory.get(0).getmWayStreet().getId().equals(listwayStreet_mapData.get(i).getId())) check_history_1 = true;
//                    }
//                    //TH2 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
//                    //      Không tìm ra điểm chung giữa hai vị trí
//                    if (!check_current_1 && !check_history_1){
//                        if (Math.abs(locationCurrent.getmAngleRoad() - locationCurrent.getmAngleVehicle()) < 10){
//                            Log.i(TAG + " CASE", "TH2 - 1");
//                            locationCurrent.setmWayStreet(wayStreet_geo);
//                        }
//                        else{
//                            Log.i(TAG + " CASE", "TH2 - 2");
//                            locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
//                        }
//                    }
//
//                    //TH3 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
//                    //      Không tìm ra điểm chung giữa hai vị trí
//                    if (check_current_1 && !check_history_1){
//                        if (Math.abs(locationCurrent.getmAngleRoad() - locationCurrent.getmAngleVehicle()) < 10){
//                            Log.i(TAG + " CASE", "TH3 - 1");
//                            locationCurrent.setmWayStreet(wayStreet_geo);
//                        }
//                        else{
//                            Log.i(TAG + " CASE", "TH3 - 2");
//                            locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
//                        }
//                    }
//                    //TH4 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
//                    //      Không tìm ra đường đi chung giữa hai vị trí
//                    if (!check_current_1 && check_history_1){
//                        Log.i(TAG + " CASE", "TH4");
//                        locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
//                    }
//                    //TH5 vị trí trước và vị hiện tại không cùng way_id -> không đi cùng đường
//                    //      Có đường đi chung giữa hai vị trí
//                    if (check_current_1 && check_history_1){
//                        if (Math.abs(locationCurrent.getmAngleRoad() - locationCurrent.getmAngleVehicle()) < 10){
//                            Log.i(TAG + " CASE", "TH5 - 1");
//                            locationCurrent.setmWayStreet(wayStreet_geo);
//                        }
//                        else{
//                            Log.i(TAG + " CASE", "TH5 - 2");
//                            locationCurrent.setmWayStreet(locationHistory.get(0).getmWayStreet());
//                        }
//                    }
//                }
//            }
//            else {
//                locationCurrent.setmWayStreet(wayStreet_geo);
//            }
//            Log.i(TAG + " WayID_new", locationCurrent.getmWayStreet().getId());
//            int position = CalculateMap.getSegmentContainGPS(locationCurrent.getmWayStreet().getmNodeStreet(), locationCurrent.getmGeoPoint());
//            Log.i(TAG + " POSITION", String.valueOf(position));
//            if (position != -1) {
//                GeoPoint result = CalculateMap.getGeoPointProjection(locationCurrent.getmWayStreet().getmNodeStreet().get(position).getGeoPoint(), locationCurrent.getmWayStreet().getmNodeStreet().get(position + 1).getGeoPoint(), locationCurrent.getmGeoPoint());
//                locationCurrent.setmGeoPointOnRoad(result);
//                Log.i(TAG + " GeoPointOnRoad", result.getLatitude() + "," + result.getLongitude());
//            }
//            else {
//                locationCurrent.setmGeoPointOnRoad(locationCurrent.getmGeoPoint());
//            }
//
//            if (mGeoPointGoal != null) {
//                Road roadToGoal = OSMBounsPack.getRoad(MainActivity.this, locationCurrent.getmGeoPointOnRoad(), mGeoPointGoal);
//                drawPathGoal.updateDrawPath(roadToGoal.mRouteHigh, Color.RED, 7);
//            }
//
//            Log.i(TAG + " AngleVehicle", String.valueOf(locationCurrent.getmAngleVehicle()));
//            Log.i(TAG + " AngleRoad", String.valueOf(locationCurrent.getmAngleRoad()));
//
//            Log.i(TAG + " Time", String.valueOf(System.currentTimeMillis() - time_start));
//            Log.i(TAG, "-----------------------------------------------------");
//            locationHistory.add(0, locationCurrent);
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

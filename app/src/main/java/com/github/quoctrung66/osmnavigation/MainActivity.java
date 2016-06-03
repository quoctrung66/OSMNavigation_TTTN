package com.github.quoctrung66.osmnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.quoctrung66.osmnavigation.Handler.HandleView;
import com.github.quoctrung66.osmnavigation.Helper.Constant;
import com.github.quoctrung66.osmnavigation.View.MapViewCustom;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

public class MainActivity extends AppCompatActivity {

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
    }
}

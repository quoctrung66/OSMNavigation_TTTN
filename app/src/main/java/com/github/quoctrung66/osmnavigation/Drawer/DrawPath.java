package com.github.quoctrung66.osmnavigation.Drawer;

import android.app.Activity;
import android.graphics.Paint;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 5/1/2016.
 */
public class DrawPath {
    private MapView mMapView;
    private PathOverlay myPath;
    private Activity mActivity;
    public DrawPath(Activity activity, MapView mapView){
        mActivity = activity;
        mMapView = mapView;
    }
    public void updateDrawPath(ArrayList<GeoPoint> listRouteHigh, int color, float width){
        mMapView.getOverlays().remove(myPath);
        myPath = new PathOverlay(color, mActivity);
        Paint paint = myPath.getPaint();
        paint.setStrokeWidth(width);
        paint.setAlpha(150);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        myPath.setPaint(paint);
        for (int i = 0; i < listRouteHigh.size(); i++){
            myPath.addPoint(listRouteHigh.get(i));
        }
        mMapView.getOverlays().add(myPath);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMapView.invalidate();
            }
        });
    }
}

package com.github.quoctrung66.osmnavigation.Drawer;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Created by QUOC TRUNG on 4/29/2016.
 */
public class DrawIcon {
    private Activity mActivity;
    private MapView mMapView;
    private ItemizedIconOverlay itemOverlay;
    private CustomOverlayICon itemCustomOverlay;
    private Drawable mMarker;

    public DrawIcon(Activity activity, MapView mapView, Drawable marker){
        mActivity = activity;
        mMapView = mapView;
        mMarker = marker;
    }
    private class UserLocationListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
        @Override
        public boolean onItemSingleTapUp(int index, OverlayItem item) {
            return false;
        }

        @Override
        public boolean onItemLongPress(int index, OverlayItem item) {
            return false;
        }
    }
    public void updateLocation(GeoPoint geoPoint, float radius_center, float radius_accuracy, int[] rgb) {
        try {
            ResourceProxy mResourceProxy = new DefaultResourceProxyImpl(mActivity);
            ArrayList<OverlayItem> items = new ArrayList();
            items.add(new OverlayItem("Here", "My location", geoPoint));
            if (mMarker != null){
                mMapView.getOverlays().remove(itemOverlay);
                mMapView.getOverlays().remove(itemCustomOverlay);
                itemOverlay = new ItemizedIconOverlay(items, mMarker, new UserLocationListener(), mResourceProxy);
                mMapView.getOverlays().add(itemOverlay);
            }
            else {
                mMapView.getOverlays().remove(itemOverlay);
                mMapView.getOverlays().remove(itemCustomOverlay);
                itemCustomOverlay = new CustomOverlayICon(mActivity, geoPoint, radius_center, radius_accuracy, rgb);
                mMapView.getOverlays().add(itemCustomOverlay);
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMapView.invalidate();
                }
            });
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            Log.e("updateUserLocation", e.toString());
        }
    }
}

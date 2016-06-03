package com.github.quoctrung66.osmnavigation.View;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by QUOC TRUNG on 4/28/2016.
 */
public class MapViewCustom extends MapView {

    private Overlay tapOverlay;
    private OnTapListener onTapListener;

    public MapViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected MapViewCustom(Context context, ResourceProxy resourceProxy, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs) {
        super(context, resourceProxy, tileProvider, tileRequestCompleteHandler, attrs);
    }

    public MapViewCustom(Context context, ResourceProxy resourceProxy) {
        super(context, resourceProxy);
    }

    public MapViewCustom(Context context, ResourceProxy resourceProxy, MapTileProviderBase aTileProvider) {
        super(context, resourceProxy, aTileProvider);
    }

    public MapViewCustom(Context context, ResourceProxy resourceProxy, MapTileProviderBase aTileProvider, Handler tileRequestCompleteHandler) {
        super(context, resourceProxy, aTileProvider, tileRequestCompleteHandler);
    }

    private void prepareTagOverlay(){
        this.tapOverlay = new Overlay(this.getContext()) {
            @Override
            protected void draw(Canvas c, MapView osmv, boolean shadow) {

            }

            @Override
            public boolean onLongPress(MotionEvent e, MapView mapView) {
                Projection proj = mapView.getProjection();
                GeoPoint geoPoint = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
                if(MapViewCustom.this.onTapListener != null){
                    MapViewCustom.this.onTapListener.onMapTapped(geoPoint);
                }
                return true;
            }
        };
    }

    public void addTapListener(OnTapListener onTapListener){
        this.prepareTagOverlay();
        this.getOverlays().add(0, this.tapOverlay);
        this.onTapListener = onTapListener;
    }

    public void removeTapListener(){
        if(this.tapOverlay != null && this.getOverlays().size() > 0){
            this.getOverlays().remove(0);
        }
        this.tapOverlay = null;
        this.onTapListener = null;
    }

    public interface OnTapListener{
        void onMapTapped(GeoPoint geoPoint);
    }

}

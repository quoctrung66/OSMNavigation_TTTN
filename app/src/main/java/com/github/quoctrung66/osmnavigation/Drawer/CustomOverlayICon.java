package com.github.quoctrung66.osmnavigation.Drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

public class CustomOverlayICon extends Overlay {

    GeoPoint mGeoPoint;
    private Projection mProject;
    float radius_accuracy;
    float radius_center;
    int[] mRGB;

    public CustomOverlayICon(Context context, GeoPoint geoPoint, float radius_center, float radius_accuracy, int[] rgb) {
        super(context);
        this.mGeoPoint = geoPoint;
        this.radius_accuracy = radius_accuracy;
        this.radius_center = radius_center;
        this.mRGB = rgb;
    }

    protected void draw(Canvas canvas, MapView mapView, boolean arg2) {
        mProject = mapView.getProjection();
        Point point = new Point();
        mProject.toPixels(this.mGeoPoint, point);
        Paint mPaint = new Paint();
        Paint mStroke = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setARGB(100, mRGB[0], mRGB[1], mRGB[2]);
        mPaint.setStyle(Style.FILL);

        mStroke.setAntiAlias(true);
        mStroke.setARGB(50, mRGB[0], mRGB[1], mRGB[2]);
        mStroke.setStyle(Style.FILL);

        canvas.drawCircle((float) point.x, (float) point.y, radius_center, mPaint);
        if (radius_accuracy > radius_center) {
            canvas.drawCircle((float) point.x, (float) point.y, radius_accuracy, mStroke);
        }
        mapView.invalidate();
    }
}
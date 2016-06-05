package com.github.quoctrung66.osmnavigation.Utils;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

/**
 * Created by QUOC TRUNG on 6/5/2016.
 */
public class CalculateMap {
    public static double getAngle(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        double dLon = (geoPoint2.getLongitude() - geoPoint1.getLongitude());

        double y = Math.sin(dLon) * Math.cos(geoPoint2.getLatitude());
        double x = Math.cos(geoPoint1.getLatitude()) * Math.sin(geoPoint2.getLatitude()) - Math.sin(geoPoint1.getLatitude())
                * Math.cos(geoPoint2.getLatitude()) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);

        brng = brng < 0 ? (180 - Math.abs(brng) + 180) : brng;

        return brng;
    }

    public static double getDistance(GeoPoint geoPoint1, GeoPoint geoPoint2){
        float[] results = new float[1];
        Location.distanceBetween(geoPoint1.getLatitude(), geoPoint1.getLongitude(), geoPoint2.getLatitude(), geoPoint2.getLongitude(), results);
        return results[0];
    }
}

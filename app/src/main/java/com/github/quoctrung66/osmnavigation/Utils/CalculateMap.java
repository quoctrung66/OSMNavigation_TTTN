package com.github.quoctrung66.osmnavigation.Utils;

import android.location.Location;
import android.util.Log;

import com.github.quoctrung66.osmnavigation.MainActivity;
import com.github.quoctrung66.osmnavigation.Model.NodeStreet;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

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
    /*
        distance = result[0] meter
     */
    public static double getDistance(GeoPoint geoPoint1, GeoPoint geoPoint2){
        float[] results = new float[1];
        Location.distanceBetween(geoPoint1.getLatitude(), geoPoint1.getLongitude(), geoPoint2.getLatitude(), geoPoint2.getLongitude(), results);
        return results[0];
    }

    public static int getSegmentContainGPS(ArrayList<NodeStreet> list, GeoPoint geoPoint){
        for (int i = 0; i < list.size() - 1; i++){
            double bearing1 = getAngle(list.get(i).getGeoPoint(), geoPoint);
            double bearing2 = getAngle(list.get(i).getGeoPoint(), list.get(i+1).getGeoPoint());

            double bearing3 = getAngle(list.get(i+1).getGeoPoint(), geoPoint);
            double bearing4 = getAngle(list.get(i+1).getGeoPoint(), list.get(i).getGeoPoint());
            Log.i("MainActivity bearing", bearing1 + ", " + bearing2 + ", " + bearing3 + ", " + bearing4);
            double abs1 = Math.abs(bearing1-bearing2);
            double abs2 = Math.abs(bearing3-bearing4);
            abs1 = abs1 <= 180 ? abs1 : 360 - abs1;
            abs2 = abs2 <= 180 ? abs2 : 360 - abs2;
            if (abs1 <= 95 && abs2 <= 95){
                return i;
            }
        }
        return -1;
    }

    public static GeoPoint getGeoPointProjection(GeoPoint start, GeoPoint end, GeoPoint point) {
        GeoPoint vectorChiPhuong = new GeoPoint(end.getLatitude() - start.getLatitude(), end.getLongitude() - start.getLongitude());
        double TU = -(start.getLongitude() - point.getLongitude())*(end.getLongitude() - start.getLongitude()) - (start.getLatitude() - point.getLatitude())*(end.getLatitude() - start.getLatitude());
        double MAU = (end.getLatitude() - start.getLatitude())*(end.getLatitude() - start.getLatitude()) + (end.getLongitude() - start.getLongitude())*(end.getLongitude() - start.getLongitude());
        double t = TU / MAU;
        return new GeoPoint(start.getLatitude() + vectorChiPhuong.getLatitude()*t, start.getLongitude() + vectorChiPhuong.getLongitude()*t);
    }
}

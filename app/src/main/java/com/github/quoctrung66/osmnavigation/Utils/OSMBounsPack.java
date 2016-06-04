package com.github.quoctrung66.osmnavigation.Utils;

import android.content.Context;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 6/5/2016.
 */
public class OSMBounsPack {
    public static Road getRoad(Context mContext, GeoPoint geoPoint1, GeoPoint geoPoint2) {
        RoadManager roadManager = new OSRMRoadManager(mContext);
        ArrayList<GeoPoint> track = new ArrayList<>();
        track.add(geoPoint1);
        track.add(geoPoint2);
        return roadManager.getRoad(track);
    }
}

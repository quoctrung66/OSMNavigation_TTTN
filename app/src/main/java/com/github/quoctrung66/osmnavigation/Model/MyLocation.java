package com.github.quoctrung66.osmnavigation.Model;

import android.location.Location;

import com.github.quoctrung66.osmnavigation.Helper.Constant;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class MyLocation {
    private Location mLocation;
    private GeoPoint mGeoPoint;
    private Road mRoad;
    private GeoPoint mGeoPointOnRoad;

    private WayStreet mWayStreet = null;
    private ArrayList<WayStreet> mWayStreet_stort = null;

    private double mDistanceToPrevious = 0;
    private double mAngleVehicle = Constant.DEFAULT_VALUE;

    private double mDistanceToGoal = Constant.DEFAULT_VALUE_MAX;
    private double mAngleRoad = Constant.DEFAULT_VALUE;

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public GeoPoint getmGeoPoint() {
        return mGeoPoint;
    }

    public void setmGeoPoint(GeoPoint mGeoPoint) {
        this.mGeoPoint = mGeoPoint;
    }

    public Road getmRoad() {
        return mRoad;
    }

    public void setmRoad(Road mRoad) {
        this.mRoad = mRoad;
    }

    public GeoPoint getmGeoPointOnRoad() {
        return mGeoPointOnRoad;
    }

    public void setmGeoPointOnRoad(GeoPoint mGeoPointOnRoad) {
        this.mGeoPointOnRoad = mGeoPointOnRoad;
    }

    public WayStreet getmWayStreet() {
        return mWayStreet;
    }

    public void setmWayStreet(WayStreet mWayStreet) {
        this.mWayStreet = mWayStreet;
    }

    public ArrayList<WayStreet> getmWayStreet_stort() {
        return mWayStreet_stort;
    }

    public void setmWayStreet_stort(ArrayList<WayStreet> mWayStreet_stort) {
        this.mWayStreet_stort = mWayStreet_stort;
    }

    public double getmDistanceToPrevious() {
        return mDistanceToPrevious;
    }

    public void setmDistanceToPrevious(double mDistanceToPrevious) {
        this.mDistanceToPrevious = mDistanceToPrevious;
    }

    public double getmAngleVehicle() {
        return mAngleVehicle;
    }

    public void setmAngleVehicle(double mAngleVehicle) {
        this.mAngleVehicle = mAngleVehicle;
    }

    public double getmDistanceToGoal() {
        return mDistanceToGoal;
    }

    public void setmDistanceToGoal(double mDistanceToGoal) {
        this.mDistanceToGoal = mDistanceToGoal;
    }

    public double getmAngleRoad() {
        return mAngleRoad;
    }

    public void setmAngleRoad(double mAngleRoad) {
        this.mAngleRoad = mAngleRoad;
    }
}

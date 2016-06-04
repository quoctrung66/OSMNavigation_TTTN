package com.github.quoctrung66.osmnavigation.Model;

import org.osmdroid.util.GeoPoint;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class NodeStreet {
    String id;
    String lat;
    String lon;

    public NodeStreet(){
        super();
    }

    public NodeStreet(String id) {
        this.id = id;
    }

    public NodeStreet(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public NodeStreet(String id, String lat, String lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "NodeStreetID: " + id + ", Lat: " + lat + ", Lon: " + lon;
    }

    public boolean hasLatLon(){
        if (this.lat.equals(null) || this.lon.equals(null)){
            return false;
        }
        return true;
    }

    public GeoPoint getGeoPoint(){
        return new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));
    }
}

package com.github.quoctrung66.osmnavigation.Helper;

import org.osmdroid.util.GeoPoint;

/**
 * Created by QUOC TRUNG on 6/3/2016.
 */
public class Constant {
    public static final GeoPoint HCMUT;
    static {
        HCMUT = new GeoPoint(10.770432d, 106.658081d);
    }

    public static final String REVERSE_NOMINATIM = "http://nominatim.openstreetmap.org/reverse?format=json&polygon_geojson=1&extratags=1&namedetails=1&addressdetails=0&lat=";
    public static final String DETAIL_STREET_QUERY = "http://api.openstreetmap.org/api/0.6/way/";  //#id/full/
    public static final String OSM_BBOX = "http://api.openstreetmap.org/api/0.6/map?bbox=";
}

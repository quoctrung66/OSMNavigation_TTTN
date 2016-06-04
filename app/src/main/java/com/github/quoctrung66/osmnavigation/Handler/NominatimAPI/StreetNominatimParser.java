package com.github.quoctrung66.osmnavigation.Handler.NominatimAPI;

import com.github.quoctrung66.osmnavigation.Handler.RequestHTTP;
import com.github.quoctrung66.osmnavigation.Helper.Constant;
import com.github.quoctrung66.osmnavigation.Model.NodeStreet;
import com.github.quoctrung66.osmnavigation.Model.WayStreet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class StreetNominatimParser {
    public WayStreet StreetIDParser(double lat, double lon, int zoom){
        String url = Constant.REVERSE_NOMINATIM + lat + "&lon=" + lon + "&zoom=" + zoom;
        try {
            String result_json = RequestHTTP.readUrl(url);
            JSONObject obj = new JSONObject(result_json);
            String id = obj.getString("osm_id");
            WayStreet wayStreet = new WayStreet(id);

            JSONObject extratags = obj.getJSONObject("extratags");
            if (extratags.has("oneway")){
                String oneway = extratags.getString("oneway");
                if (oneway.equals("yes")) wayStreet.setOneWay(true);
            }

            JSONObject namedetails = obj.getJSONObject("namedetails");
            String name = namedetails.getString("name");
            wayStreet.setNameStreet(name);

            JSONObject geoJson = obj.getJSONObject("geojson");
            JSONArray coordinates = geoJson.getJSONArray("coordinates");

            ArrayList<NodeStreet> nodes = new ArrayList<>();
            for (int i = 0; i < coordinates.length(); i++){
                JSONArray node = coordinates.getJSONArray(i);
                String lon_node = node.getString(0);
                String lat_node = node.getString(1);
                nodes.add(new NodeStreet(lat_node, lon_node));
            }
            wayStreet.setmNodeStreet(nodes);
            return wayStreet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

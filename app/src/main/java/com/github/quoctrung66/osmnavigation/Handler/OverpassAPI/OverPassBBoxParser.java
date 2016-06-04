package com.github.quoctrung66.osmnavigation.Handler.OverpassAPI;

import com.github.quoctrung66.osmnavigation.Handler.RequestHTTP;
import com.github.quoctrung66.osmnavigation.Helper.Constant;
import com.github.quoctrung66.osmnavigation.Model.NodeStreet;
import com.github.quoctrung66.osmnavigation.Model.WayStreet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 5/25/2016.
 */
public class OverPassBBoxParser {

    public ArrayList<WayStreet> getOverpass_BBox(double around, GeoPoint geoPoint){
        String url = Constant.OVERPASS_BBOX + around + "," + geoPoint.getLatitude() + "," + geoPoint.getLongitude() + Constant.OVERPASS_BBOX_TAIL;
        RequestHTTP requestHTTP = new RequestHTTP();
        ArrayList<WayStreet> wayStreetArrayList = new ArrayList<>();
        try {
            String json_result = requestHTTP.readUrl(url);
            JSONObject object = new JSONObject(json_result);
            JSONArray elements = object.getJSONArray("elements");
            for (int i = 0; i < elements.length(); i++){
                JSONObject obj_way = elements.getJSONObject(i);
                String way_id = obj_way.getString("id");
                WayStreet wayStreet = new WayStreet(way_id);
                ArrayList<NodeStreet> list_node = new ArrayList<>();
                JSONArray nodes = obj_way.getJSONArray("nodes");
                for (int j = 0; j < nodes.length(); j++){
                    String node_id = nodes.getString(j);
                    list_node.add(new NodeStreet(node_id));
                }
                wayStreet.setmNodeStreet(list_node);
                JSONObject tags = obj_way.getJSONObject("tags");
                if (tags.has("name")){
                    String name = tags.getString("name");
                    wayStreet.setNameStreet(name);
                }
                if (tags.has("oneway")){
                    if (tags.getString("oneway").equals("yes")){
                        wayStreet.setOneWay(true);
                    }
                }
                wayStreetArrayList.add(wayStreet);
            }
            return wayStreetArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

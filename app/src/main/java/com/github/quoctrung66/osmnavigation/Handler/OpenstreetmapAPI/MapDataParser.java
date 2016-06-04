package com.github.quoctrung66.osmnavigation.Handler.OpenstreetmapAPI;

import com.github.quoctrung66.osmnavigation.Handler.XMLLoader;
import com.github.quoctrung66.osmnavigation.Helper.Constant;
import com.github.quoctrung66.osmnavigation.Model.NodeStreet;
import com.github.quoctrung66.osmnavigation.Model.WayStreet;

import org.osmdroid.util.GeoPoint;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class MapDataParser {

    public ArrayList<WayStreet> ParserNode(GeoPoint geoPoint, double OFFSET) {
        double l = geoPoint.getLongitude() - OFFSET;
        double b = geoPoint.getLatitude() - OFFSET;
        double r = geoPoint.getLongitude() + OFFSET;
        double t = geoPoint.getLatitude() + OFFSET;
        String url = Constant.OSM_BBOX + l + "," + b + "," + r + "," + t;

        try {
            Document document = new XMLLoader().getXmlDoc(url);
            if (document.hasChildNodes()) {
                NodeList nodeList = document.getElementsByTagName("node");
                ArrayList<NodeStreet> node_temps = new ArrayList<>();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    String id = nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue();
                    String lat = nodeList.item(i).getAttributes().getNamedItem("lat").getNodeValue();
                    String lon = nodeList.item(i).getAttributes().getNamedItem("lon").getNodeValue();
                    NodeStreet node_temp = new NodeStreet(id, lat, lon);
                    node_temps.add(node_temp);
                }

                NodeList wayList = document.getElementsByTagName("way");
                ArrayList<WayStreet> way_temps = new ArrayList<>();
                for (int i = 0; i < wayList.getLength(); i++) {
                    boolean checkhighway = false;
                    String id = wayList.item(i).getAttributes().getNamedItem("id").getNodeValue();
                    WayStreet wayStreet = new WayStreet(id);
                    ArrayList<NodeStreet> nd_temp = new ArrayList<>();
                    for (int j = 0; j < wayList.item(i).getChildNodes().getLength(); j++){
                        if (wayList.item(i).getChildNodes().item(j).getNodeName().equals("nd")) {
                            String ref = wayList.item(i).getChildNodes().item(j).getAttributes().getNamedItem("ref").getNodeValue();
                            for (int k = 0; k < node_temps.size(); k++){
                                if (ref.equals(node_temps.get(k).getId())){
                                    nd_temp.add(node_temps.get(k));
                                }
                            }
                        }
                        if (wayList.item(i).getChildNodes().item(j).getNodeName().equals("tag")) {
                            if (wayList.item(i).getChildNodes().item(j).getAttributes().getNamedItem("k").getNodeValue().equals("name")) {
                                String name = wayList.item(i).getChildNodes().item(j).getAttributes().getNamedItem("v").getNodeValue();
                                wayStreet.setNameStreet(name);
                            }
                            if (wayList.item(i).getChildNodes().item(j).getAttributes().getNamedItem("k").getNodeValue().equals("onewway")) {
                                String oneway = wayList.item(i).getChildNodes().item(j).getAttributes().getNamedItem("v").getNodeValue();
                                if (oneway.equals("yes")) wayStreet.setOneWay(true);
                            }
                            if (wayList.item(i).getChildNodes().item(j).getAttributes().getNamedItem("k").getNodeValue().equals("highway")) {
                                checkhighway = true;
                            }
                        }
                    }
                    wayStreet.setmNodeStreet(nd_temp);
                    if (checkhighway)
                        way_temps.add(wayStreet);
                }
                return  way_temps;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

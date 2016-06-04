package com.github.quoctrung66.osmnavigation.Handler.OpenstreetmapAPI;

import com.github.quoctrung66.osmnavigation.Handler.XMLLoader;
import com.github.quoctrung66.osmnavigation.Helper.Constant;
import com.github.quoctrung66.osmnavigation.Model.NodeStreet;
import com.github.quoctrung66.osmnavigation.Model.WayStreet;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class StreetDetailParser {
    public WayStreet StreetDetail(String way_id){
        String url = Constant.DETAIL_STREET_QUERY + way_id + "/full";
        XMLLoader xmlLoader = new XMLLoader();
        WayStreet wayStreet = new WayStreet(way_id);
        ArrayList<NodeStreet> nodes = new ArrayList<>();
        try {
            Document document = xmlLoader.getXmlDoc(url);
            NodeList nodeList = document.getElementsByTagName("node");
            ArrayList<NodeStreet> listNodeStreet = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++){
                String id = nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue();
                String lat = nodeList.item(i).getAttributes().getNamedItem("lat").getNodeValue();
                String lon = nodeList.item(i).getAttributes().getNamedItem("lon").getNodeValue();
                NodeStreet nodeStreet = new NodeStreet(id, lat, lon);
                listNodeStreet.add(nodeStreet);
            }
            NodeList ndList = document.getElementsByTagName("nd");
            for (int i = 0; i < ndList.getLength(); i++){
                String ndref = ndList.item(i).getAttributes().getNamedItem("ref").getNodeValue();
                for (int j = 0; j < listNodeStreet.size(); j++){
                    if (listNodeStreet.get(j).getId().equals(ndref)){
                        nodes.add(listNodeStreet.get(j));
                        break;
                    }
                }
            }
            wayStreet.setmNodeStreet(nodes);
            NodeList tagList = document.getElementsByTagName("tag");
            for (int i = 0; i < tagList.getLength(); i++){
                if (tagList.item(i).getAttributes().getNamedItem("k").getNodeValue().equals("name")){
                    wayStreet.setNameStreet(tagList.item(i).getAttributes().getNamedItem("v").getNodeValue());
                }
                if (tagList.item(i).getAttributes().getNamedItem("k").getNodeValue().equals("oneway")){
                    String oneway = tagList.item(i).getAttributes().getNamedItem("v").getNodeValue();
                    if (oneway.equals("yes")) wayStreet.setOneWay(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wayStreet;
    }
}

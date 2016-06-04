package com.github.quoctrung66.osmnavigation.Model;

import java.util.ArrayList;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class WayStreet {
    String id;
    ArrayList<NodeStreet> mNodeStreet;
    String nameStreet;
    boolean isOneWay = false;

    public WayStreet(){
        super();
    }

    public WayStreet(String id){
        this.id = id;
    }

    public WayStreet(String id, ArrayList<NodeStreet> mNodeStreet) {
        this.id = id;
        this.mNodeStreet = mNodeStreet;
    }

    public WayStreet(String id, ArrayList<NodeStreet> mNodeStreet, String nameStreet, boolean isOneWay) {
        this.id = id;
        this.mNodeStreet = mNodeStreet;
        this.nameStreet = nameStreet;
        this.isOneWay = isOneWay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<NodeStreet> getmNodeStreet() {
        return mNodeStreet;
    }

    public void setmNodeStreet(ArrayList<NodeStreet> mNodeStreet) {
        this.mNodeStreet = mNodeStreet;
    }

    public String getNameStreet() {
        return nameStreet;
    }

    public void setNameStreet(String nameStreet) {
        this.nameStreet = nameStreet;
    }

    public boolean isOneWay() {
        return isOneWay;
    }

    public void setOneWay(boolean oneWay) {
        isOneWay = oneWay;
    }

    @Override
    public String toString() {
        return "WayStreetID: " + this.id + ", NameStreet: " + this.nameStreet + ", OneWay: " + this.isOneWay + ", Node: " + mNodeStreet.size();
    }
}

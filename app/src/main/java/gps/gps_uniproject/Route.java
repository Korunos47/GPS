package gps.gps_uniproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import Help.LocationData;

public class Route{

    private String name;
    private ArrayList<LocationData> data;
    private boolean punkt;
    private int farbe;

    public Route(String name, ArrayList<LocationData> data,boolean punkt, int farbe){
        this.name = name;
        this.data = data;
        this.punkt = punkt;
        this.farbe = farbe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LocationData> getData() {
        return data;
    }

    public void setData(LocationData locdata){
        data.add(locdata);
    }

    public boolean isPunkt() {
        return punkt;
    }

    public void setPunkt(boolean punkt) {
        this.punkt = punkt;
    }

    public int getFarbe() {
        return farbe;
    }

    public void setFarbe(int farbe) {
        this.farbe = farbe;
    }
}

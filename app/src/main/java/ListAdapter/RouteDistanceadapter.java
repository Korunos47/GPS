package ListAdapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import Help.LocationData;
import gps.gps_uniproject.Mainmenu;
import gps.gps_uniproject.R;
import gps.gps_uniproject.Route;

/**
 * Created by ntoma on 07.12.2016.
 */

public class RouteDistanceadapter extends BaseAdapter {

    private ArrayList<LocationData> Routenpunkte;
    private ArrayList<LocationData> Routegeg;
    private Context context;
    LayoutInflater inflater;

    public RouteDistanceadapter(ArrayList<LocationData> Route, ArrayList<LocationData> Routegeg, Context context) {
        this.Routenpunkte = Route;
        this.Routegeg = Routegeg;
        inflater = (LayoutInflater.from(context));

        this.context = context;
    }


    @Override
    public int getCount() {
        return Routenpunkte.size();
    }

    @Override
    public Object getItem(int i) {
        return Routenpunkte.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.routerow,null);
        }

        Location locationgeg = new Location("");
        locationgeg.setLatitude(Routegeg.get(i).latitude);
        locationgeg.setLongitude(Routegeg.get(i).longitude);

        Location locationgem = new Location("");
        locationgem.setLatitude(Routenpunkte.get(i).latitude);
        locationgem.setLongitude(Routenpunkte.get(i).longitude);

        float distance = locationgeg.distanceTo(locationgem);
        if(distance > 1000.0f){
            distance = distance / 1000.0f;
        }

        TextView waypointcounter = (TextView) view.findViewById(R.id.waypointCounter);
        TextView waypointgglong = (TextView) view.findViewById(R.id.waypointgglong);
        TextView waypointgglat = (TextView) view.findViewById(R.id.waypointgglat);
        TextView waypointgemlong = (TextView) view.findViewById(R.id.waypointgemlong);
        TextView waypointgemlat = (TextView) view.findViewById(R.id.waypointgemlat);
        TextView distanceto = (TextView) view.findViewById(R.id.waypointdistanceto);

        waypointcounter.setText(String.valueOf(i+1));
        waypointgemlong.setText(String.valueOf(Routenpunkte.get(i).longitude));
        waypointgemlat.setText(String.valueOf(Routenpunkte.get(i).latitude));

        waypointgglat.setText(String.valueOf(Routegeg.get(i).latitude));
        waypointgglong.setText(String.valueOf(Routegeg.get(i).longitude));

        distanceto.setText(String.valueOf(distance));

        return view;
    }
}


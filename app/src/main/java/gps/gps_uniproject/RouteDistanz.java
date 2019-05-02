package gps.gps_uniproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import ListAdapter.RouteDistanceadapter;

/**
 * Created by ntoma on 07.12.2016.
 */

public class RouteDistanz extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routedistance);

        String chosenRoute = getIntent().getStringExtra("route");
        ListView lw = (ListView) findViewById(R.id.listroutedistance);

        if ("FestgelegteRoute".equals(chosenRoute)) {
            RouteDistanceadapter adapter = new RouteDistanceadapter(Mainmenu.GPSData.RouteAbgelaufenLowGPS, Mainmenu.GPSData.FestgelegteRoute, this);
            lw.setAdapter(adapter);
        }
        //TODO muss auskommentiert und verbessert werden -> Route2 gibt es nicht mehr
        /*else if ("Route2".equals(chosenRoute)) {
            RouteDistanceadapter adapter = new RouteDistanceadapter(Mainmenu.GPSData.Route2Abgelaufen, Mainmenu.GPSData.Route2, this);
            lw.setAdapter(adapter);
        }*/

    }
}

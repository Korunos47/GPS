package gps.gps_uniproject;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * Created by Mali on 09.12.2016.
 */

public class GraphFehler extends AppCompatActivity{

    private XYPlot plot;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int zaehler =0;


        plot = (XYPlot) findViewById(R.id.plot);

        final Number[] domainLabels = new Number[Mainmenu.GPSData.FestgelegteRoute.size()];
        Number[] series1Numbers = new Number[Mainmenu.GPSData.RouteAbgelaufenLowGPS.size()];
        Number[] series2Numbers = new Number[Mainmenu.GPSData.RouteAbgelaufenHighGPS.size()];

        for(int i=0;i<Mainmenu.GPSData.FestgelegteRoute.size();i++){
            domainLabels[i] = zaehler;
            zaehler += 10;
        }


        //FestgelegteRoute 1
        if(Mainmenu.GPSData.RouteAbgelaufenLowGPS.size()>0){

            for(int i = 0; i<Mainmenu.GPSData.FestgelegteRoute.size(); i++){
                double lat1 = Mainmenu.GPSData.FestgelegteRoute.get(i).latitude;
                double lon1 = Mainmenu.GPSData.FestgelegteRoute.get(i).longitude;
                double lat2 = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i).latitude;
                double lon2 = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i).longitude;

                series1Numbers[i] = messung(lat1,lat2,lon1,lon2);

            }

            XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "RouteAbgelaufenLowGPS 1");

            LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.BLUE, Color.RED, null, null);

            series1Format.setInterpolationParams( new CatmullRomInterpolator.Params(Mainmenu.GPSData.FestgelegteRoute.size(), CatmullRomInterpolator.Type.Centripetal));

            plot.addSeries(series1,series1Format);
        }

        //FestgelegteRoute 2
        if(Mainmenu.GPSData.RouteAbgelaufenHighGPS.size()> 0){

            for(int i=0;i<Mainmenu.GPSData.FestgelegteRoute.size();i++){
                double lat1 = Mainmenu.GPSData.FestgelegteRoute.get(i).latitude;
                double lon1 = Mainmenu.GPSData.FestgelegteRoute.get(i).longitude;
                double lat2 = Mainmenu.GPSData.RouteAbgelaufenHighGPS.get(i).latitude;
                double lon2 = Mainmenu.GPSData.RouteAbgelaufenHighGPS.get(i).longitude;

                series2Numbers[i] = messung(lat1,lat2,lon1,lon2);

            }

            XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "RouteAbgelaufenHighGPS 2");

            LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.GREEN, Color.YELLOW, null, null);

            series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[] {


                    PixelUtils.dpToPix(20),
                    PixelUtils.dpToPix(15)}, 0));

            series2Format.setInterpolationParams( new CatmullRomInterpolator.Params(Mainmenu.GPSData.FestgelegteRoute.size(), CatmullRomInterpolator.Type.Centripetal));

            plot.addSeries(series2,series2Format);

        }



        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });


    }


    public double messung(double lat1, double lat2, double lon1, double lon2) {
        double dist = 0;

        Location locationgeg = new Location("");
        locationgeg.setLatitude(lat2);
        locationgeg.setLongitude(lon2);

        Location locationgem = new Location("");
        locationgem.setLatitude(lat1);
        locationgem.setLongitude(lon1);

        float distance = locationgeg.distanceTo(locationgem);
        if(distance > 1000.0f){
            distance = distance / 1000.0f;
        }


        return distance;
    }



}

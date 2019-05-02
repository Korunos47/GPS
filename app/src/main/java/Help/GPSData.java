package Help;

import java.io.Serializable;
import java.util.ArrayList;

public class GPSData implements Serializable {

    public ArrayList<LocationData> FestgelegteRoute;


    //public ArrayList<LocationData> Route2;

    public ArrayList<LocationData> RouteAbgelaufenLowGPS;
    public ArrayList<LocationData> RouteAbgelaufenHighGPS;
    //public ArrayList<LocationData> Route2Abgelaufen;

    public ArrayList<LocationData> locationData;

    public GPSData() {
        locationData = new ArrayList<>();

        RouteAbgelaufenLowGPS = new ArrayList<>();
        RouteAbgelaufenHighGPS = new ArrayList<>();
        //Route2Abgelaufen = new ArrayList<>();

        FestgelegteRoute = new ArrayList<>();
        //Route2 = new ArrayList<>();
        setRoute();
    }

    private void setRoute() {
        double[] LatitudeArrRoute1 = {51.48675,51.48579,51.48485,51.48448,51.48417,51.48383,51.48433,51.48485,51.48489,51.48491,51.48496,51.4852,51.48608,51.48707,51.48772,51.48759,51.48741,51.48736,51.48723};
        double[] LongitudeArrRoute1 = {7.13485,7.1349,7.13475,7.136,7.13685,7.13763,7.1378,7.13794,7.13863,7.13942,7.14002,7.14028,7.14,7.13963,7.13943,7.13806,7.13803,7.13702,7.13549};

        //double[] LatitudeArrRoute2 = {51.4867,51.48627,51.48574,51.48532,51.48485,51.48497,51.48503,51.48509,51.48564,51.48609,51.48657,51.48709,51.48764,51.4883,51.48876,51.48886,51.48904,51.48918,51.48929,51.48934,51.4891,51.48894,51.48871,51.48845,51.48822,51.48795,51.48763,51.48729};
        //double[] LongitudeArrRoute2 = {7.13518,7.135,7.13487,7.1348,7.13471,7.13356,7.1326,7.13162,7.13195,7.13218,7.1323,7.13248,7.13285,7.13338,7.13375,7.13485,7.13566,7.13654,7.13767,7.1381,7.13818,7.13792,7.13765,7.13765,7.13686,7.13645,7.13602,7.13552};

        double[] NewLatitude = {51.48675,51.48488,51.48380,51.48616,51.48738,51.48767 };
        double[] NewLongitude = {7.13485,7.13485,7.13763,7.13803, 7.13803,7.13607};

        LocationData RoutenPunkt1;
        LocationData RoutenPunkt2;

        for(int i = 0;i < NewLatitude.length; i++){
            RoutenPunkt1 = new LocationData();
            RoutenPunkt1.latitude = NewLatitude[i];
            RoutenPunkt1.longitude = NewLongitude[i];

            FestgelegteRoute.add(RoutenPunkt1);
        }
        /*
        for(int i = 0;i<LatitudeArrRoute2.length;i++){
            RoutenPunkt2 = new LocationData();

            RoutenPunkt2.latitude = LatitudeArrRoute2[i];
            RoutenPunkt2.longitude = LongitudeArrRoute2[i];

            Route2.add(RoutenPunkt2);
        }
        */
    }

}
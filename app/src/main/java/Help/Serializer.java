package Help;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import gps.gps_uniproject.Mainmenu;
import gps.gps_uniproject.Route;

public class Serializer {

    public static void write(Object j, Context context) throws Exception {

    }

    public static void SerializeObject(Context context,GPSData object){
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("GPSData", Context.MODE_PRIVATE);
            ObjectOutputStream os = null;
            os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GPSData DeserializeObject(Context context){
        FileInputStream fis = null;
        GPSData gpsData = null;
        try {
            fis = context.openFileInput("GPSData");
            ObjectInputStream is = new ObjectInputStream(fis);
            gpsData = (GPSData) is.readObject();
            is.close();
            fis.close();
            return gpsData;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return gpsData;
    }

    public static void SerializeObjectRoute(Context context,ArrayList<Route> object){
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("FestgelegteRoute", Context.MODE_PRIVATE);
            ObjectOutputStream os = null;
            os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Route> DeserializeObjectRoute(Context context){
        FileInputStream fis = null;
        ArrayList<Route> route = null;

            try {
                fis = context.openFileInput("FestgelegteRoute");
                ObjectInputStream is = new ObjectInputStream(fis);
                route = (ArrayList<Route>) is.readObject();
                is.close();
                fis.close();
                return route;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

        return route;
    }



}

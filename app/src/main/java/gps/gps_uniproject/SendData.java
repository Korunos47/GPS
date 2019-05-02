package gps.gps_uniproject;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;



public class SendData {

    public void sendeDaten(double longitude, double latitude, long locationTime, String timestamp) throws IOException {

        //convert to string
        String longitudeString = String.valueOf(longitude);
        String latitudeString = String.valueOf(latitude);
        String locationTimeString = Long.toString(locationTime);

        String myUrl = "https://mypremades.com/gpsDaten.php";
        URL url = new URL(myUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        //setze request header
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        //parameter die gesendet werden sollen
        String urlParameter = "longitude="+longitudeString+"&latitude="+latitudeString+"&locationTime="+locationTimeString+"&timestamp="+timestamp;

        // Evangelos
        byte[] outputInBytes = urlParameter.getBytes("UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();

        /*
        // Marcel
        // Send post request
        DataOutputStream write = new DataOutputStream(connection.getOutputStream());
        write.writeBytes(urlParameter);
        write.flush();
        write.close();
        */

        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));


        System.out.println("GPS Daten wurden gesendet!");
        input.close();
        connection.disconnect();
    }

}

package gps.gps_uniproject;

/**
 * Created by Denis on 12.01.2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.view.menu.MenuView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HttpsURLConnection;

import Help.LocationData;
import Help.Serializer;
import ListAdapter.CustomGPSResultsAdapter;


public class GPSResultsDistance extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener {
    /*Wenn max Distanz überschritten dann wird GPS gesendet
    *
    * */


    GoogleApiClient apiC;
    ListView listV;
    CustomGPSResultsAdapter customAdapter;
    LocationRequest locationR;

    private Sensor accelerometerSensor;
    private SensorManager sm;
    private TextView speedtxt, tlmt, geschw, distM,vest, accDist;
    private MenuItem iV = null,saveEnergyS=null;
    private int setRequestTime ;
    private boolean saveEnergy = false, stillstand = false;
    private double longitude,latitude, maxdist = 0, totalSpeed = 0,lasDist=0, nCurrentSpeed;
    private long locationtime, waitTime =0, maxSpeed=0, wt = 1000 * 60;
    private String timestamp;
    private Location locSpeed= null;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_resultsdist);

        locationR = new LocationRequest();
        listV = (ListView) findViewById(R.id.gpsresultsListViewDist);
        geschw = (TextView)findViewById(R.id.maxgeschwV);
        distM = (TextView)findViewById(R.id.maxdistV);
        tlmt = (TextView) findViewById(R.id.tlimit);
        vest = (TextView) findViewById(R.id.vest);
        accDist = (TextView)findViewById(R.id.accDist);

        showDistKonfig();
        //Accelerometer
        sensorSetup();

        if (apiC == null) {
            apiC = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        customAdapter = new CustomGPSResultsAdapter(this);
        listV.setAdapter(customAdapter);
    }

    //Position
    public double messungDist(double lat1, double lon1, double lat2, double lon2) {

        Location locationgeg = new Location("");
        locationgeg.setLatitude(lat2);
        locationgeg.setLongitude(lon2);

        Location locationgem = new Location("");
        locationgem.setLatitude(lat1);
        locationgem.setLongitude(lon1);

        double distance = locationgeg.distanceTo(locationgem);
        return distance;
    }

    public void sensorSetup(){
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

    }

    //zeigt GPS Geschwindigkeit an
    public void GPSSpeed(){
        speedtxt = (TextView)this.findViewById(R.id.crtSpeed);

        if(locSpeed == null ){
            speedtxt.setText("-,- m/s");
        }else {
            nCurrentSpeed = locSpeed.getSpeed();
            speedtxt.setText(new DecimalFormat("#.##").format(nCurrentSpeed) + "m/s");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.periodicdistmenu, menu);
        iV = menu.findItem(R.id.maxSpeed);
        saveEnergyS = menu.findItem(R.id.saveEnergyS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.periodicDistTime:
                showAlertDialog();
                break;

            case R.id.distkonfig:
                showDistKonfig();
                break;
            case R.id.saveEnergyRB:
                if(item.isChecked()){
                    item.setChecked(false);
                    saveEnergy = false;
                    iV.setVisible(false);
                    stillstand = false;
                    startLocationUpdates();
                    saveEnergyS.setVisible(false);

                }else{
                    saveEnergy = true;
                    item.setChecked(true);
                    iV.setVisible(true);
                    showMaxSpeedKonfig();
                    saveEnergyS.setVisible(true);
                }
                break;

            case R.id.maxSpeed:
                showMaxSpeedKonfig();
                break;

            case R.id.saveEnergyS:
                if(item.isChecked()){
                    item.setChecked(false);
                    stillstand = false;

                }else{
                    item.setChecked(true);
                    stillstand = true;
                    sm.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

                }
            break;

        }




        return true;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Zeitraum auswählen");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Übernehmen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                locationR.setInterval(Integer.parseInt(input.getText().toString()));
                locationR.setFastestInterval(Integer.parseInt(input.getText().toString()));

                setRequestTime = Integer.parseInt(input.getText().toString());
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void showDistKonfig(){
        // wenn Dist zwischen neuen und alten GPS Punkt GRÖßER als vorgegeben dann  updaten

        AlertDialog.Builder builderDist = new AlertDialog.Builder(this);
        builderDist.setTitle("Distanz wählen (in m)");

        final EditText distinput = new EditText(this);
        distinput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builderDist.setView(distinput);

        builderDist.setPositiveButton("Übernehmen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                maxdist = Double.parseDouble(distinput.getText().toString());
                distM.setText("MaxDist: " + maxdist + "m");

            }
        });
        builderDist.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                maxdist = 0;
                dialogInterface.cancel();
            }
        });

        builderDist.show();



    }

    private void showMaxSpeedKonfig(){
        // max Geschwindigkeit

        AlertDialog.Builder builderSpeed = new AlertDialog.Builder(this);
        builderSpeed.setTitle("Max Geschw. wählen(m/s)");

        final EditText speedinput = new EditText(this);
        speedinput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builderSpeed.setView(speedinput);

        builderSpeed.setPositiveButton("Übernehmen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                maxSpeed = Long.parseLong(speedinput.getText().toString());
                if(maxSpeed > 0) {
                    geschw.setText("maxSpeed: " + maxSpeed + "m/s");
                }
            }
        });
        builderSpeed.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builderSpeed.show();


    }


    //Sagt voraus, wann das GPS starten soll
    public long tlimit(long geschaetzeGesch){
        //tlimit zum wake-up von GPS Sensor

        long t, eModel, dlimit = (long)maxdist,vEst;

        eModel =(long) lasDist;
        vEst = eModel + geschaetzeGesch;
        t = (dlimit - eModel) / vEst;


        vest.setText("vEst: "+ new DecimalFormat("#.####").format(vEst));

        return t;
    }

    public void stopLocationUpdates(){

            LocationServices.FusedLocationApi.removeLocationUpdates(apiC, this);

            Toast.makeText(this, "GPS-Daten werden nicht mehr gesammelt.", Toast.LENGTH_SHORT).show();

    }

    public LocationData pruefeObUeberMaxSchwelle(Location lastGPS){
        //wenn die Distanz von dem näherste GPS Punkt von der Route ist Größer als max Distanz dann GPS Fix einfügen
        double dist=0, dist1=0;
        LocationData distErg = new LocationData();
        int r = Mainmenu.GPSData.FestgelegteRoute.size();

        LocationData d1 = Mainmenu.GPSData.FestgelegteRoute.get(0); // startpunkt
        //letzten GPSUpdate mit der Route vergleichen, den Punkt mit kleinsten distanz merken

        //distanz vonm ersten Punkt der Route
        dist1 = messungDist(d1.latitude,d1.longitude,lastGPS.getLatitude(),lastGPS.getLongitude());

        for(int i=0;i<r;i++){
            LocationData rP = Mainmenu.GPSData.FestgelegteRoute.get(i);
            dist = messungDist(rP.latitude,rP.longitude,lastGPS.getLatitude(),lastGPS.getLongitude());

            if(dist<=dist1 ){
                lasDist = dist;
                distErg.latitude = lastGPS.getLatitude();
                distErg.longitude = lastGPS.getLongitude();
            }

        }
        if(lasDist>0){
            Toast.makeText(this, "Distanz: " + new DecimalFormat("#.##").format(lasDist) + "m", Toast.LENGTH_SHORT).show();
        }

        return distErg;
    }

    public void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(apiC,locationR,this);
        Toast.makeText(this, "GPS-Daten werden gesammelt.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        apiC.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Toast.makeText(this, "GPS gestoppt...", Toast.LENGTH_LONG).show();
        apiC.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Wenn die Activity nicht mehr im Fokus steht, stope GPS updates
        Toast.makeText(this, "Warten...", Toast.LENGTH_LONG).show();
        stopLocationUpdates();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // Lege den Intervall fest in der die GPS daten abgerufen werden
        if(saveEnergy == false) {
            locationR.setInterval(10000);
            locationR.setFastestInterval(10000);
        }else if(saveEnergy == true){
            locationR.setInterval((waitTime * 1000));
            locationR.setFastestInterval((waitTime * 1000));
        }



        locationR.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationR);

        // Wenn keine Berechtigungen für Standort gegeben wurden, frage sie ab
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    200);
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(apiC, locationR, this);


    }

    @Override
    public void onLocationChanged(Location location) {
        TextView lastDist = (TextView) findViewById(R.id.LastDist);

        locSpeed = location;

        //get gpsspeed
        GPSSpeed();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        LocationData loc = new LocationData();
        loc.latitude = location.getLatitude();
        loc.longitude = location.getLongitude();
        loc.timestamp = strDate;
        loc.locationTime = location.getTime();

        Mainmenu.GPSData.locationData.add(loc);

        Serializer.SerializeObject(this, Mainmenu.GPSData);
        customAdapter.notifyDataSetChanged();

        if(maxdist != 0 ){
            LocationData prf = pruefeObUeberMaxSchwelle(location);
            lastDist.setText("LastDistance : "+ new DecimalFormat("#.##").format(lasDist) + "m");

            if (prf != null) {
                if (maxdist < lasDist) {
                        //Toast.makeText(this, "Sende GPS-Daten an Server.", Toast.LENGTH_SHORT).show();

                    SendGPSPosition senddata = new SendGPSPosition();
                    senddata.execute();
                } else {
                    Toast.makeText(this, "Distanz zu gering", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this, "Keine max Schwelle gewählt", Toast.LENGTH_SHORT).show();

        }

        if(saveEnergy == true){

            if(lasDist > 0) {
                waitTime = tlimit(maxSpeed);
            }
            tlmt.setText("next update in: " +waitTime+ " sec.");

            if(maxSpeed != 0 && lasDist != 0 && waitTime> 0){

                locationR.setInterval(waitTime*1000);
                locationR.setInterval(waitTime*1000);

                Toast.makeText(this, "Warten: " +(waitTime)+ " sek" , Toast.LENGTH_SHORT).show();
            }
        }else if(saveEnergy == false){
            locationR.setInterval(10000);
            locationR.setFastestInterval(10000);
            maxSpeed = 0;
            waitTime = 0;
            geschw.setText("OFF");
            tlmt.setText("OFF");
            vest.setText("OFF");
        }



    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[0];
        double y = sensorEvent.values[1];
        double z = sensorEvent.values[2];




        if(stillstand == true){
            totalSpeed = Math.sqrt((Math.pow(x,2)+ Math.pow(y,2)  + Math.pow(z,2))) ;

            accDist.setText(new DecimalFormat("#.##").format(totalSpeed)+" m/s²");
            if(totalSpeed <= 0.5 && totalSpeed > 0.1){
                stopLocationUpdates();

            }else if(totalSpeed >= 0.6){
                sm.unregisterListener(this);
                startLocationUpdates();
                stillstand = false;
                saveEnergyS.setChecked(false);
                Toast.makeText(this, "Stillstandmodus deaktiviert", Toast.LENGTH_SHORT).show();
                accDist.setText("0 m/s²");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class SendGPSPosition extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            /*
            SendData sendData = new SendData();
            try {
                sendData.sendeDaten(longitude,latitude,locationtime,timestamp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;*/



            String myUrl = "https://mypremades.com/gpsDaten.php";
            URL url = null;
            try {
                url = new URL(myUrl);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

                //setze request header
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                //parameter die gesendet werden sollen
                String urlParameter = "longitude="+longitude+"&latitude="+latitude+"&locationTime="+locationtime+"&timestamp="+timestamp;

                // Evangelos
                byte[] outputInBytes = urlParameter.getBytes("UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(outputInBytes);
                os.close();


                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                System.out.println(input);
                System.out.println("GPS Daten wurden gesendet!");
                input.close();
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(GPSResultsDistance.this, "GPS-Daten wurden an den Server übermittelt", Toast.LENGTH_SHORT).show();
        }
    }
}



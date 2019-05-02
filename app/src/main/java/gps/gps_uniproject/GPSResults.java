package gps.gps_uniproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import Help.LocationData;
import Help.Serializer;
import ListAdapter.CustomGPSResultsAdapter;

public class GPSResults extends Activity implements LocationListener {

    LocationManager locationManager;


    GoogleApiClient apiClient;
    ListView listview;
    CustomGPSResultsAdapter adapter;
    LocationRequest locationRequest;

    private int setRequestTime;

    private double longitude,latitude;
    private long locationtime;
    private String timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_results);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    200);
            return;
        }



        //locationRequest = new LocationRequest();
        showAlertDialog();
        listview = (ListView) findViewById(R.id.gpsresultsListView);

/*        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }*/

        adapter = new CustomGPSResultsAdapter(this);
        listview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.periodicmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.periodicsetting:
                showAlertDialog();
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

                /*locationRequest.setInterval(Integer.parseInt(input.getText().toString()));
                locationRequest.setFastestInterval(Integer.parseInt(input.getText().toString()));*/
                setRequestTime = Integer.parseInt(input.getText().toString()) * 1000;
                requestLocationUpdates();
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

    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, setRequestTime, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String strDate = sdf.format(c.getTime());

                LocationData newLocation = new LocationData();
                newLocation.timestamp = strDate;
                newLocation.latitude = location.getLatitude();
                newLocation.longitude = location.getLongitude();
                newLocation.locationTime = location.getTime();

                Mainmenu.GPSData.locationData.add(newLocation);
                Serializer.SerializeObject(getApplicationContext(), Mainmenu.GPSData);
                adapter.notifyDataSetChanged();

                longitude = location.getLongitude();
                latitude = location.getLatitude();
                locationtime = location.getTime();
                timestamp = strDate;

                SendGPSPosition senddata = new SendGPSPosition();
                senddata.execute();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }


/*    @Override
    protected void onStart() {
        apiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        apiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Wenn die Activity nicht mehr im Fokus steht, stope GPS updates
        LocationServices.FusedLocationApi.removeLocationUpdates(
                apiClient, this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // Lege den Intervall fest in der die GPS daten abgerufen werden
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // Wenn keine Berechtigungen für Standort gegeben wurden, frage sie ab
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    200);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String strDate = sdf.format(c.getTime());



        LocationData newLocation = new LocationData();
        newLocation.timestamp = strDate;
        newLocation.latitude = location.getLatitude();
        newLocation.longitude = location.getLongitude();
        newLocation.locationTime = location.getTime();

        Mainmenu.GPSData.locationData.add(newLocation);
        Serializer.SerializeObject(this, Mainmenu.GPSData);
        adapter.notifyDataSetChanged();

        longitude = location.getLongitude();
        latitude = location.getLatitude();
        locationtime = location.getTime();
        timestamp = strDate;

        Toast.makeText(this, "Sende GPS-Daten an Server", Toast.LENGTH_SHORT).show();
        SendGPSPosition senddata = new SendGPSPosition();
        senddata.execute();
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    */

    private class SendGPSPosition extends AsyncTask<Void,Void,Void>{

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
            Toast.makeText(GPSResults.this, "GPS-Daten wurden an den Server übermittelt", Toast.LENGTH_SHORT).show();
        }
    }
}


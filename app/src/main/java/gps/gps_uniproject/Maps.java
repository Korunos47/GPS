package gps.gps_uniproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import Help.LocationData;
import Help.Serializer;

public class Maps extends AppCompatActivity implements OnMapReadyCallback,LocationListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private ArrayAdapter<String> adapter;
    private String activeMode;

    GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final ListView listView = new ListView(this);

        checkGPSPermission();

        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // GPS -Permission Abfrage
    private void checkGPSPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    200);
            return;
        }
    }

    //Abspeichern der abgelaufenen Punkte
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.GPSAufnehmen:
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
                String timestamp = sdf.format(c.getTime());

                Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                if (location != null) {
                    LocationData currentLocation = new LocationData();
                    currentLocation.timestamp = timestamp;

                    if(activeMode=="high"){
                        // Füge die Position der FestgelegteRoute hinzu
                        Mainmenu.GPSData.RouteAbgelaufenHighGPS.add(currentLocation);
                        Serializer.SerializeObject(getApplicationContext(), Mainmenu.GPSData);
                        Toast.makeText(this, "Timestamp + >Hohe Genauigkeit<-Koordinate erfolgreich aufgenommen und gespeichert", Toast.LENGTH_SHORT).show();
                    }
                    else if(activeMode=="low"){
                        Mainmenu.GPSData.RouteAbgelaufenLowGPS.add(currentLocation);
                        Serializer.SerializeObject(getApplicationContext(),Mainmenu.GPSData);
                        Toast.makeText(this, "Timestamp + >Nur GPS<-Koordinate wurde erfolgreich aufgenommen und gespeichert", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Maps.this, "Location war null. Versuch es noch einmal!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.GPSHighStart:
                if(apiClient!=null) {
                    activeMode = "high";
                    apiClient.connect();
                    Toast.makeText(this, "GPS-Aufnahme High wurde gestartet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.GPSLowStart:
                if(apiClient != null) {
                    activeMode = "low";
                    apiClient.connect();
                    Toast.makeText(this, "GPS-Aufnahme Low wurde gestartet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.GPSStop:
                apiClient.disconnect();
                Toast.makeText(this, "GPS-Aufnahme gestoppt", Toast.LENGTH_SHORT).show();
                break;
            case R.id.route1anzeigen:
                createPolyLinesRoute1(Mainmenu.GPSData.FestgelegteRoute);
                createMarkerRoute1(Mainmenu.GPSData.FestgelegteRoute);
                break;
            case R.id.route1gemessen:
                createPolyLinesRoute1gem();
                createMarkerRoute1gem();
                break;
            case R.id.fehlerGraph:
                startActivity(new Intent(this, GraphFehler.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void interpoliereZweiPunkte(LocationData Punkt1, LocationData Punkt2,String typ){

        MarkerOptions markerOptions = new MarkerOptions();

        //Get time der punkte
        long timestampPunkt1 = Punkt1.locationTime; // in millisekunden;
        long timestampPunkt2 = Punkt2.locationTime; // in millisekunden;

        //Deltas zwischen den punkten
        double deltaLat = Punkt1.latitude - Punkt2.latitude;
        double deltaLon =  Punkt1.longitude- Punkt2.longitude;


        long step = 10 * 1000; // 10 sekunden in millisekunden

        for (long t = timestampPunkt1; timestampPunkt1 < timestampPunkt2; t+= step) {

            //Faktor berechnen
            double timestampFaktor = (t - timestampPunkt1) / (timestampPunkt2 - timestampPunkt1);
            //Punkte Interpolieren
            double latInterpoliert = Punkt1.latitude + (deltaLat  * timestampFaktor);
            double lonInterpoliert = Punkt1.longitude + (deltaLon  * timestampFaktor);

            LatLng interPolLatLng = new LatLng(latInterpoliert, lonInterpoliert);

            markerOptions.title("Interpol. Marker: " + typ ).alpha(.5f).position(interPolLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
        mMap.addMarker(markerOptions);
    }


    //Marker für gegebene FestgelegteRoute 1 setzen
    private void createMarkerRoute1(ArrayList<LocationData> route1) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng newLatLng = null;
        LocationData punkt1;
        LocationData punkt2;

        for (int i = 0; i < route1.size(); i++) {
            newLatLng = new LatLng(route1.get(i).latitude, route1.get(i).longitude);

            markerOptions.title("FestgelegteRoute 1: Marker " + (i + 1)).position(newLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(markerOptions);
        }

        int i = 0;

        while( i < route1.size()){
            punkt1 = route1.get(i);
            i+=1;
            punkt2 = route1.get(i);

            interpoliereZweiPunkte(punkt1,punkt2,"Gegebene Route");
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    //Marker für gemessene FestgelegteRoute 1 setzen
    private void createMarkerRoute1gem() {
        MarkerOptions markerOptions = new MarkerOptions();

        LatLng newLatLng = null;
        LocationData punkt1;
        LocationData punkt2;

        for (int i = 0; i < Mainmenu.GPSData.RouteAbgelaufenLowGPS.size(); i++) {

            double latitude = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i).latitude;
            double longtitude = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i).longitude;
            newLatLng = new LatLng(latitude, longtitude);

            markerOptions.title("FestgelegteRoute 1 gem.: Marker " + (i + 1)).position(newLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
        }

        int i = 0;

        while( i < Mainmenu.GPSData.RouteAbgelaufenLowGPS.size()){
            punkt1 = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i);
            i+=1;
            punkt2 = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i);

            interpoliereZweiPunkte(punkt1,punkt2,"Gemessene Route");
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    //Zeichnen der Routen mit Polylines

    private void createPolyLinesRoute1(ArrayList<LocationData> locationData) {

        PolylineOptions polylineOptions = new PolylineOptions().color(Color.BLUE).width(10);

        for (int i = 0; i < locationData.size(); i++) {
            LatLng newLatLng = new LatLng(locationData.get(i).latitude, locationData.get(i).longitude);

            if (i != 18) {
                polylineOptions.add(newLatLng);
            }
        }

        mMap.addPolyline(polylineOptions);
    }

    private void createPolyLinesRoute1gem() {

        PolylineOptions polylineOptions = new PolylineOptions().color(Color.GREEN).width(10);
        LatLng newLatLng = null;

        for (int i = 0; i < Mainmenu.GPSData.RouteAbgelaufenLowGPS.size(); i++) {
            double latitude = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i).latitude;
            double longtitude = Mainmenu.GPSData.RouteAbgelaufenLowGPS.get(i).longitude;
            newLatLng = new LatLng(latitude, longtitude);

            polylineOptions.add(newLatLng);

        }

        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onClick(View view) {
    }

    public void onCreateView(ListView listView, GoogleMap googleMap) {

        String[] datasource = new String[Mainmenu.arrRoute.size()];

        //create datasource
        for (int i = 0; i < Mainmenu.arrRoute.size(); i++) {
            datasource[i] = Mainmenu.arrRoute.get(i).getName();
        }

        //create adapter
        adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.txtitem, datasource);


        //Bind adapter to the listfragment
        listView.setAdapter(adapter);

        this.registerForContextMenu(listView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_context_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.maps_context_menu, menu);

        menu.add("Delete FestgelegteRoute");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle() == "Delete FestgelegteRoute") {
            System.out.println("Deleting pos:...");
            Toast.makeText(this, "HI", Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    //Implementierung von Maps (und Marker setzen auf Startbildschirm)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
    //}
    protected void onStop() {
        apiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        if(activeMode=="high") {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        else if (activeMode=="low"){
            locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        }

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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        LocationData newLocation = new LocationData();

        newLocation.latitude = location.getLatitude();
        newLocation.longitude = location.getLongitude();
        newLocation.locationTime = location.getTime();

        if(activeMode=="high"){
            Mainmenu.GPSData.RouteAbgelaufenHighGPS.add(newLocation);
            Serializer.SerializeObject(this,Mainmenu.GPSData);
            Toast.makeText(this, "Eine Position mit >Hoher Genauigkeit< wurde aufgenommen", Toast.LENGTH_SHORT).show();
        }
        else if(activeMode=="low"){
            Mainmenu.GPSData.RouteAbgelaufenLowGPS.add(newLocation);
            Serializer.SerializeObject(this,Mainmenu.GPSData);
            Toast.makeText(this, "Eine Position mit >Nur GPS< wurde aufgenommen", Toast.LENGTH_SHORT).show();
        }

    }
}


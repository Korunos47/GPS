package gps.gps_uniproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import Help.GPSData;
import Help.Serializer;

public class Mainmenu extends Activity {
    public static Help.GPSData GPSData;
    public static ArrayList<Route> arrRoute;

    private Button Route1Show,Route2Show,PeriodicButton,PeriodicDistButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Initialize();

        Button btnOne = (Button) findViewById(R.id.start);

        //button onClick
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBoxClicked();
            }
        });

        Route1Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RouteDistanz.class);
                intent.putExtra("route","FestgelegteRoute");
                startActivity(intent);
            }
        });


        Route2Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RouteDistanz.class);
                intent.putExtra("route","Route2");
                startActivity(intent);
            }
        });

        PeriodicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GPSResults.class);
                startActivity(intent);
            }
        });

        PeriodicDistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GPSResultsDistance.class);
                startActivity(intent);
            }
        });

    }

    private void Initialize() {

        Route1Show = (Button) findViewById(R.id.ShowRoute1);
        Route2Show = (Button) findViewById(R.id.ShowRoute2);
        PeriodicButton = (Button) findViewById(R.id.periodicbutton);
        PeriodicDistButton = (Button) findViewById(R.id.periodicbtndist);



        String path = getFilesDir().getAbsolutePath() + "/GPSData";
        File file = new File(path);

        String pathRoute = getFilesDir().getAbsolutePath() + "/FestgelegteRoute";
        File fileRoute = new File(pathRoute);

        if(fileRoute.exists()) {
            arrRoute = Serializer.DeserializeObjectRoute(this);
        }else{
            CreateSerializableObjectRoute();
        }

        if (file.exists()) {
            GPSData = Serializer.DeserializeObject(this);
        } else {
            CreateSerializableObject();
        }
    }

    //erkennung checkboxen
    public void onCheckBoxClicked() {
        CheckBox GPSCheckbox = (CheckBox) findViewById(R.id.GPSCheckbox);
        CheckBox AccelerometerCheckbox = (CheckBox) findViewById(R.id.AccelerometerCheckbox);
        CheckBox AllSensorCheckbox = (CheckBox) findViewById(R.id.AllSensorCheckbox);
        CheckBox GoogleMapsCheckbox = (CheckBox) findViewById(R.id.GoogleMapsCheckbox);

        if (AllSensorCheckbox.isChecked()) {
            startActivity(new Intent(this, AllSensors.class));
            finish();
        } else if (AccelerometerCheckbox.isChecked()) {
            startActivity(new Intent(this, Accelerometer.class));
            finish();
        } else if (GPSCheckbox.isChecked()) {
            startActivity(new Intent(this, GPSResults.class));
        } else if (GoogleMapsCheckbox.isChecked()) {
            startActivity(new Intent(this, Maps.class));

        } else {
            Toast.makeText(this, "Keine Option ausgewählt", Toast.LENGTH_SHORT).show();
        }



    }

    private void CreateSerializableObject() {
        GPSData = new GPSData();

        Serializer.SerializeObject(this, GPSData);

    }

    private void CreateSerializableObjectRoute() {

        arrRoute = new ArrayList<Route>();

        Serializer.SerializeObjectRoute(this,arrRoute);
    }
}

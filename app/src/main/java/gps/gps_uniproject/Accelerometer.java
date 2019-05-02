package gps.gps_uniproject;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class Accelerometer extends Activity implements SensorEventListener{
    private Sensor accelerometerSensor;
    private SensorManager sm;
    private TextView accelemeterText, speedacc;
    private double totalSpeed=0;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer);


        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sm.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        speedacc = (TextView) findViewById(R.id.speedacc);
        accelemeterText = (TextView) findViewById(R.id.accText);


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[0];
        double y = sensorEvent.values[1];
        double z = sensorEvent.values[2];

        totalSpeed = Math.sqrt((Math.pow(x,2)+ Math.pow(y,2)  + Math.pow(z,2))) ;

        speedacc.setText("Speed: " + totalSpeed);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

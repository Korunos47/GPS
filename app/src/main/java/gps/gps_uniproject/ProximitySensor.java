package gps.gps_uniproject;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Laki on 26.11.2016.
 */

public class ProximitySensor extends Activity implements SensorEventListener {
    TextView proxText;
    Sensor proxSensor;
    SensorManager smP;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        smP = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = smP.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        proxText = (TextView) findViewById(R.id.proxSensAllSensors);

        smP.registerListener(this,proxSensor,SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        proxText.setText("Proximity Sensor: " + String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

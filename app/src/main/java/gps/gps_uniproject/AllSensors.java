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

public class AllSensors extends Activity implements SensorEventListener{
    TextView proxText,accelemeterText, gyrotext, vectorTxt;
    SensorManager smA,smP, smG ,smV;
    Sensor proxSensor,accelerometerSensor, gyroSensor, vectorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allsensors);

        Button btnTwo = (Button) findViewById(R.id.btnBackAct2);

        //Sensor methoden aufrufen
        proxSensor();
        accelerometer();
        gyroscope();
        rotationVector();

        //menü button
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllSensors.this, Mainmenu.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            accelemeterText.setText("Accelerometer: \nX:" + sensorEvent.values[0] +
                    "\nY: " + sensorEvent.values[1] +
                    "\nZ: " + sensorEvent.values[2]);

        }else if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){
            proxText.setText("Proximity Sensor: \n" + String.valueOf(sensorEvent.values[0]));

        }else if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyrotext.setText("Gyroscope Sensor: \nX: "+ sensorEvent.values[0] +
                    "\nY: " + sensorEvent.values[1] +
                    "\nZ: " + sensorEvent.values[2]);
        }else if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            vectorTxt.setText("VectorRotation Sensor \nX: " + sensorEvent.values[0] +
                    "\nY: " + sensorEvent.values[1] +
                    "\nZ: " + sensorEvent.values[2] +
                    "\n \nVectorRotation Sensor: \n"  + sensorEvent.values[3]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Sensoren methoden
    public void proxSensor(){
        smP = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = smP.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        proxText = (TextView) findViewById(R.id.proxSensAllSensors);

        smP.registerListener(this,proxSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void accelerometer(){
        smA = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = smA.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelemeterText = (TextView) findViewById(R.id.accSensorAllSensors);

        smA.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void gyroscope(){
        smG = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = smG.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyrotext = (TextView) findViewById(R.id.gyroSensorAllSensors);

        smG.registerListener(this, gyroSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void rotationVector(){
        smV = (SensorManager) getSystemService(SENSOR_SERVICE);
        vectorSensor = smV.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        vectorTxt = (TextView) findViewById(R.id.rotVectSensor);

        smV.registerListener(this, vectorSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
}

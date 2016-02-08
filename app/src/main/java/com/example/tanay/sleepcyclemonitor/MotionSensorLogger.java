package com.example.tanay.sleepcyclemonitor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MotionSensorLogger extends Service implements SensorEventListener {
    private static final String DEBUG_TAG = "MotionSensorLogger";
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    SQLiteDatabase mydatabase;
    public MotionSensorLogger() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MotionLoggerService", "Started");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        mydatabase = openOrCreateDatabase("sleep_data",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS sensor_data (timestamp INTEGER,accelx REAL,accely REAL,accelz REAL);");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d("MotionLogger","Stopped");
        sensorManager.unregisterListener(this,sensor);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        new SensorEventLoggerTask().execute(event);
        /*sensorManager.unregisterListener(this);
        stopSelf();*/

    }

    private class SensorEventLoggerTask extends AsyncTask <SensorEvent,Void, Void> {

        @Override
        protected Void doInBackground(SensorEvent... events) {
            SensorEvent sensorEvent = events[0];
            long timestamp = System.currentTimeMillis();
            final float sensorThreshold = 0.05f;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            Log.d("currtime",sdf.format(new Date(System.currentTimeMillis())));
            Log.d("timestamp", Long.toString(timestamp));
            boolean writeToDb = false;

            float x = sensorEvent.values[0];
            if(x > sensorThreshold){
                Log.d("accel x", Float.toString(x));
                writeToDb = true;
            }
            else{
                Log.d("accel x","not significant");
            }

            float y = sensorEvent.values[1];
            if( y > sensorThreshold){
                Log.d("accel y", Float.toString(y));
                writeToDb = true;
            }
            else {
                Log.d("accel y","not significant");
            }

            float z = sensorEvent.values[2];
            if(z > sensorThreshold){
                Log.d("accel z", Float.toString(z));
                writeToDb = true;
            }
            else  {
                Log.d("accel z","not significant");
            }

            if(writeToDb){
                mydatabase.execSQL("INSERT INTO sensor_data VALUES(?,?,?,?);",new Object[]{timestamp,x,y,z});
            }
            return null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nothing
    }
}

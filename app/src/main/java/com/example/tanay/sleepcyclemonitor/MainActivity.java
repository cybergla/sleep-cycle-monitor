package com.example.tanay.sleepcyclemonitor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends Activity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static MainActivity inst;
    private TextView alarmStatus;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Intent motionIntent;

    ToggleButton alarmToggle;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("com.example.tanay.sleepcyclemonnitor",this.MODE_PRIVATE);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmToggle.setChecked(sharedPreferences.getBoolean("alarmToggle",false));
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmStatus = (TextView) findViewById(R.id.alarmStatus);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        motionIntent = new Intent(this,MotionSensorLogger.class);
    }

    public void onToggleClicked(View view) {
        if(alarmToggle.isChecked()){
            editor = sharedPreferences.edit();
            editor.putBoolean("alarmToggle",true);
            editor.commit();
        }
        else {
            editor = sharedPreferences.edit();
            editor.putBoolean("alarmToggle",false);
            editor.commit();
        }

        if (sharedPreferences.getBoolean("alarmToggle",true)) {
            Log.d("MyActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            alarmStatus.setText("Alarm Set For: " + sdf.format(calendar.getTime()));
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
            alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            Intent stopIntent = new Intent(this, AlarmService.class);
            this.stopService(stopIntent);
            alarmStatus.setText("Alarm Set For: ");
            Log.d("MyActivity", "Alarm Off");
        }
    }

    public void turnOffAlarm(View view){
        Intent stopIntent = new Intent(this, AlarmService.class);
        this.stopService(stopIntent);
    }

    public void viewGraph(View view){
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public void startService(View view){
        /*AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MotionSensorLogger.class );
        PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        scheduler.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, scheduledIntent);*/
        Intent intent = new Intent(this, MotionSensorLogger.class );
        startService(intent);
    }

    public void stopService(View view){
        /*AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,MotionSensorLogger.class );
        PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        scheduler.cancel(scheduledIntent);*/
        Intent intent = new Intent(this, MotionSensorLogger.class );
        this.stopService(intent);
    }
}

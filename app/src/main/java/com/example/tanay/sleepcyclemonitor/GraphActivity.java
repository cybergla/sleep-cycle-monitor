package com.example.tanay.sleepcyclemonitor;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class GraphActivity extends Activity {
    LineChart chart;

    public GraphActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Resources r = getResources();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        chart = (LineChart) findViewById(R.id.chart);
        SQLiteDatabase mydatabase = SQLiteDatabase.openDatabase("/data/data/com.example.tanay.sleepcyclemonitor/databases/sleep_data", null, MODE_PRIVATE);
        Cursor resultSet = mydatabase.rawQuery("Select * from sensor_data", null);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        resultSet.moveToFirst();
        ArrayList<Entry> accelX =  new ArrayList<Entry>();
        ArrayList<Entry> accelY =  new ArrayList<Entry>();
        ArrayList<Entry> accelZ =  new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        int j=0;
        while(!resultSet.isAfterLast()){
            Log.d("Time", sdf.format(new Date(resultSet.getLong(0))));

            for(int i=1; i<4; i++){
                Log.d("Accel",Float.toString(resultSet.getFloat(i)));
            }

            accelX.add(new Entry(resultSet.getFloat(1),j));
            accelY.add(new Entry(resultSet.getFloat(2),j));
            accelZ.add(new Entry(resultSet.getFloat(3),j));
            xVals.add(Integer.toString(j));
            j++;
            resultSet.moveToNext();
        }
        LineDataSet Xset = new LineDataSet(accelX,"X Axis");
        Xset.setAxisDependency(YAxis.AxisDependency.LEFT);
        Xset.setColor(r.getColor(R.color.Red));
        Xset.setLineWidth(3.0f);
        LineDataSet Yset = new LineDataSet(accelY,"Y Axis");
        Yset.setAxisDependency(YAxis.AxisDependency.LEFT);
        Yset.setColor(r.getColor(R.color.Purple));
        Yset.setLineWidth(3.0f);
        LineDataSet Zset = new LineDataSet(accelZ,"Z Axis");
        Zset.setAxisDependency(YAxis.AxisDependency.LEFT);
        Zset.setColor(r.getColor(R.color.Green));
        Zset.setLineWidth(3.0f);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(Xset);
        dataSets.add(Yset);
        dataSets.add(Zset);
        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate();
     }


}

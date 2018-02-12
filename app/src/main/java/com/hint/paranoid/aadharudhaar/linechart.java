package com.hint.paranoid.aadharudhaar;


import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class linechart extends AppCompatActivity {
    private GestureDetectorCompat gestureDetectorCompat;
    SQLiteDatabase mydatabase;
    Cursor resultset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);
        mydatabase = openOrCreateDatabase("TransactDB", MODE_PRIVATE, null);
        final Calendar cal = Calendar.getInstance();
        int curr_year = cal.get(Calendar.YEAR);
   /*   try {

            resultset= mydatabase.rawQuery("SELECT * FROM lend ;",null);

     Log.d("check", resultset.getCount()+" ");
            if(resultset.moveToFirst())
            {
                do{
                    int mon=resultset.getInt(8);
                   // int cnt=resultset.getInt(1);
                    Log.d("check0","mon="+mon);
                   // hash[mn]=cnt;
                    // entries.add(new Entry(cnt, mn));
                }while(resultset.moveToNext());
            }

            Toast.makeText(this, "Success!"+resultset.getCount(), Toast.LENGTH_SHORT).show();
        }catch (SQLException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "database query failed", Toast.LENGTH_SHORT).show();
        }*/
       try {

           resultset= mydatabase.rawQuery("SELECT month,COUNT(*) FROM lend WHERE year="+curr_year+" GROUP BY month ;",null);
           Log.d("check1", resultset.getCount()+"");

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }catch (SQLException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "database query failed", Toast.LENGTH_SHORT).show();
        }
        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        gestureDetectorCompat = new GestureDetectorCompat(this, new My3rdGestureListener());

        ArrayList<Entry> entries = new ArrayList<>();
        int hash[]=new int[12];
        if(resultset.moveToFirst())
        {
            do{
                int mn=resultset.getInt(0);
                int cnt=resultset.getInt(1);
                Log.d("check","mon="+mn+"count="+cnt);
                hash[mn]=cnt;
               // entries.add(new Entry(cnt, mn));
            }while(resultset.moveToNext());
        }
        for(int i=0;i<12;i++)
        {
            entries.add(new Entry(hash[i], i));

        }


        LineDataSet dataset = new LineDataSet(entries, "Frequency of borrowers");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        labels.add("August");
        labels.add("September");
        labels.add("October");
        labels.add("November");
        labels.add("December");

        LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        dataset.setDrawStepped(true);
        //  dataset.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.animateY(5000);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    class My3rdGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe right' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

            if(event2.getX() > event1.getX()){
                Toast.makeText(getBaseContext(),
                        "Swipe right - finish()",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(
                        linechart.this, barchart.class);

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }

            return true;
        }

    }
}

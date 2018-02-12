package com.hint.paranoid.aadharudhaar;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.hint.paranoid.aadharudhaar.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class barchart extends ActionBarActivity {
    //  Button b;
    private GestureDetectorCompat gestureDetectorCompat1;
    SQLiteDatabase mydatabase;
    Cursor resultset,resultset2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barchart);
        //Database
        mydatabase = openOrCreateDatabase("TransactDB", MODE_PRIVATE, null);
        final Calendar cal = Calendar.getInstance();
        int curr_year = cal.get(Calendar.YEAR);

        gestureDetectorCompat1 = new GestureDetectorCompat(this, new MyGestureListener());
        // gestureDetectorCompat2 = new GestureDetectorCompat(this, new MyGestureListener());
        //  b=(Button)findViewById(R.id.pie);
        BarChart chart = (BarChart) findViewById(R.id.chart);

        try {

            resultset= mydatabase.rawQuery("SELECT month,SUM(amount) FROM lend WHERE year="+curr_year+" GROUP BY month ;",null);
            Log.d("check1", resultset.getCount() + "");

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }catch (SQLException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "database query failed", Toast.LENGTH_SHORT).show();
        }
      /*  try {

            resultset2= mydatabase.rawQuery("SELECT * FROM borrower ;",null);

            Log.d("check", resultset2.getCount()+" ");
            if(resultset2.moveToFirst())
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

            resultset2= mydatabase.rawQuery("SELECT month,SUM(amount) FROM borrow WHERE year="+curr_year+" GROUP BY month ;",null);
            Log.d("check2", resultset.getCount() + "");

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }catch (SQLException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "database query failed", Toast.LENGTH_SHORT).show();
        }

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("My Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat1.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

            if (event2.getX() < event1.getX()) {
                Toast.makeText(getBaseContext(),
                        "Swipe left - startActivity()",
                        Toast.LENGTH_SHORT).show();

                //switch another activity
                Intent intent = new Intent(
                        barchart.this, linechart.class);
                startActivity(intent);

                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
            else if(event2.getX() > event1.getX()) {
                Toast.makeText(getBaseContext(),
                        "Swipe right - startActivity()",
                        Toast.LENGTH_SHORT).show();

                //switch another activity
                Intent intent = new Intent(
                        barchart.this, piechart.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }

            return true;
        }
    }


    private ArrayList<IBarDataSet> getDataSet() {
        ArrayList<IBarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        int hash[]=new int[12];
        if(resultset.moveToFirst())
        {
            do{
                int mn=resultset.getInt(0);
                int sum=resultset.getInt(1);
                Log.d("check","mon="+mn+"sum="+sum);
                hash[mn]=sum;
                // entries.add(new Entry(cnt, mn));
            }while(resultset.moveToNext());
        }
        for(int i=0;i<12;i++)
        {
            BarEntry v1e1 = new BarEntry(hash[i], i); // Jan
            valueSet1.add(v1e1);

        }

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();

        if(resultset2.moveToFirst())
        {
            do{
                int mn=resultset2.getInt(0);
                int sum=resultset2.getInt(1);
                Log.d("check","mon="+mn+"sum="+sum);
                hash[mn]=sum;
                // entries.add(new Entry(cnt, mn));
            }while(resultset2.moveToNext());
        }
        for(int i=0;i<12;i++)
        {
            BarEntry v1e1 = new BarEntry(hash[i], i); // Jan
            valueSet2.add(v1e1);

        }


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Money Lent");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Money Borrowed");
        barDataSet2.setColor(Color.rgb(155,0,0));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        xAxis.add("JUL");
        xAxis.add("AUG");
        xAxis.add("SEP");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");

        return xAxis;
    }

}
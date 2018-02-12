package com.hint.paranoid.aadharudhaar;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddLendActivity extends AppCompatActivity {
    SQLiteDatabase mydatabase;
    private EditText name,phone,amt,interest,comment,uid,addr,state,pin;
    private String nameString,phoneString,commentString,uidString,addrString,stateString,dateString,amtString,interestString,pinString;
    private int amtInt, interestInt, pinInt;
    private Button save;
    int year_x,month_x,day_x,week_x,curr_year,curr_month,curr_day;
    private TextView result_tv;

    private int PERMISSION_FOR_CAMERA=123;

    private String name_scan,uid_scan,address_scan,state_scan,pincode_scan;

    //private String duedate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        //for calendar
        final Calendar cal = Calendar.getInstance();
        curr_year=year_x=cal.get(Calendar.YEAR);
        curr_month=month_x=cal.get(Calendar.MONTH) ;
        curr_day=day_x=cal.get(Calendar.DAY_OF_MONTH);
        week_x=cal.get(Calendar.WEEK_OF_YEAR);
        result_tv=(TextView)findViewById(R.id.date);
        showDialogOnButtonClick();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(AddLendActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan a Aadharcard QR Code");
                integrator.setResultDisplayDuration(500);
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.initiateScan();
            }
        });

        grantPermissionForCamera();

        loadTextViews();
        createDB();
        //Buttons --------------------------------
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
                saveInput();
            }
        });

    }

    private void grantPermissionForCamera() {

        if (ContextCompat.checkSelfPermission(AddLendActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(AddLendActivity.this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(AddLendActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_FOR_CAMERA);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if(requestCode==PERMISSION_FOR_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getBaseContext(),"PERMISSION GRANTED",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(),"PERMISSION NOT GRANTED",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String data = scanResult.getContents();
            if(data != null && !data.isEmpty()){
                displayInfo(data);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Scan Cancelled", Toast.LENGTH_SHORT);
                toast.show();
            }

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void displayInfo(String data) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(data));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && "PrintLetterBarcodeData".equals(parser.getName())) {

                    uid_scan = parser.getAttributeValue(null,"uid");
                    name_scan = parser.getAttributeValue(null,"name");
                    address_scan = parser.getAttributeValue(null,"dist");
                    state_scan = parser.getAttributeValue(null,"state");
                    pincode_scan = parser.getAttributeValue(null,"pc");
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        name.setText(name_scan);
        uid.setText(uid_scan);
        addr.setText(address_scan);
        state.setText(state_scan);
        pin.setText(pincode_scan);

    }


    Button btn;
    static final int DIALOG_ID=0;
    public void showDialogOnButtonClick(){
        btn = (Button)findViewById(R.id.cal);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }

        );
    }
    @Override
    protected Dialog onCreateDialog(int id){
        if(id==DIALOG_ID){
            return new DatePickerDialog(this,dpickerListner,year_x,month_x,day_x);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListner
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x=year;
            month_x=monthOfYear ;
            day_x=dayOfMonth;
            Calendar c =new GregorianCalendar();
            c.set(Calendar.YEAR,year_x);
            c.set(Calendar.MONTH,monthOfYear);
            c.set(Calendar.DAY_OF_MONTH,day_x);
            week_x = c.get(Calendar.WEEK_OF_YEAR);
            Toast.makeText(AddLendActivity.this, day_x + "/" + month_x + "/" + year_x + "/" + week_x, Toast.LENGTH_LONG).show();
            result_tv.setText(day_x + "/" + month_x + "/" + year_x);
            //duedate=(String)result_tv.getText();
        }
    };
    private void createDB()
    {
        mydatabase = openOrCreateDatabase("TransactDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "lend(id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar NOT NULL,phone varchar NOT NULL,amount integer NOT NULL,interest integer,date varchar NOT NULL,day int NOT NULL,month int NOT NULL,year int NOT NULL,comments varchar,uid varchar,address varchar,state varchar,pin integer);");
    }
    private void loadTextViews()
    {
        name = (EditText) findViewById(R.id.borrow_name);
        phone = (EditText) findViewById(R.id.phone);
        amt = (EditText) findViewById(R.id.amt);
        interest = (EditText) findViewById(R.id.interest);
        comment = (EditText) findViewById(R.id.comments);
        uid = (EditText) findViewById(R.id.uid);
        addr = (EditText) findViewById(R.id.address);
        state = (EditText) findViewById(R.id.state);
        pin = (EditText) findViewById(R.id.postal);
    }
    private void getInput()
    {
        nameString = name.getText().toString();
        phoneString = phone.getText().toString();
        amtString = amt.getText().toString();
        if(!amtString.equals(""))
        {
            amtInt = Integer.parseInt(amtString);
        }
        interestString = interest.getText().toString();
        if(!interestString.equals(""))
            interestInt = Integer.parseInt(interestString);
        commentString = comment.getText().toString();
        uidString = uid.getText().toString();
        addrString = addr.getText().toString();
        stateString = state.getText().toString();
        pinString = pin.getText().toString();
        if(!pinString.equals(""))
        pinInt = Integer.parseInt(pinString);
        dateString = result_tv.getText().toString();
        //Toast.makeText(this, nameString, Toast.LENGTH_SHORT).show();
       // Toast.makeText(this, Integer.toString(amtInt), Toast.LENGTH_SHORT).show();
       // Toast.makeText(this, dateString, Toast.LENGTH_SHORT).show();
    }
    private void saveInput()
    {
        if(validate())

        {
            try {
                mydatabase.execSQL("INSERT INTO lend(name,phone,amount,interest,date,day,month,year,comments,uid,address,state,pin) VALUES('"+nameString+"'," +
                        "'"+phoneString+"',"+amtInt+","+interestInt+","+"'"+dateString+"',"+curr_day+","+curr_month+","+curr_year+",'"+commentString+"','"+uidString+"','"+addrString+"','"+stateString+"',"+pinInt+");");
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            }catch (SQLException e)
            {
                e.printStackTrace();
                Toast.makeText(this, "database query failed", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            name.setText("");
            phone.setText("");
            amt.setText("");
            interest.setText("");
            comment.setText("");
            uid.setText("");
            addr.setText("");
            state.setText("");
            pin.setText("");
            result_tv.setText("");
            Toast.makeText(this, "Invalid entry", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validate() {

        if(nameString.equals(""))
        {
            //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
            return false;

        }
        if(phoneString.length()!=10)
        {
            //Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(amtString.equals(""))
        {
            //Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!isDateValid(dateString))
        {
            //Toast.makeText(this, "4", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!uidString.equals("") && uidString.length()!=12)
        {
            //Toast.makeText(this, "5", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!pinString.equals("") && pinString.length()!=6)
        {
            //Toast.makeText(this, "6", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }
    public boolean isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


}

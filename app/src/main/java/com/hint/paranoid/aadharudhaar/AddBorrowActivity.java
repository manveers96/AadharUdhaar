package com.hint.paranoid.aadharudhaar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddBorrowActivity extends AppCompatActivity {
    private EditText name,phone,amt,interest,comment;
    private TextView datex;
    private String nameString,phoneString,commentString,dateString,amtString,interestString;
    private int amtInt, interestInt;
    SQLiteDatabase mydatabase;
    int year_x,month_x,day_x,week_x;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_borrow);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        loadTextViews();
        createDB();
        final Calendar cal = Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH) ;
        day_x=cal.get(Calendar.DAY_OF_MONTH);

        datex=(TextView)findViewById(R.id.date);
        showDialogOnButtonClick();
        save = (Button) findViewById(R.id.save_borrow);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
                saveInput();
            }
        });
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
            Toast.makeText(AddBorrowActivity.this, day_x + "/" + month_x + "/" + year_x + "/" + week_x, Toast.LENGTH_LONG).show();
            datex.setText(day_x + "/" + month_x + "/" + year_x);
            //duedate=(String)result_tv.getText();
        }
    };
    private void createDB() {
        mydatabase = openOrCreateDatabase("TransactDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "borrow(id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar NOT NULL,phone varchar NOT NULL,amount integer NOT NULL,interest integer,date varchar NOT NULL,day integer,month integer,year integer,comments varchar);");
    }

    private void loadTextViews() {
        name = (EditText) findViewById(R.id.borrow_name);
        phone = (EditText) findViewById(R.id.borrow_phone);
        amt = (EditText) findViewById(R.id.amt_borrow);
        interest = (EditText) findViewById(R.id.interest_borrow);
        comment = (EditText) findViewById(R.id.comments_borrow);

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
        dateString = datex.getText().toString();
    }
    private void saveInput()
    {
        if(validate())
        {
            try {
                final Calendar cal = Calendar.getInstance();
                int year_x=cal.get(Calendar.YEAR);
                int month_x=cal.get(Calendar.MONTH);
                int day_x=cal.get(Calendar.DAY_OF_MONTH);
                int week_x=cal.get(Calendar.WEEK_OF_YEAR);
                mydatabase.execSQL("INSERT INTO borrow(name,phone,amount,interest,date,day,month,year,comments) VALUES('"+nameString+"'," +
                        "'"+phoneString+"',"+amtInt+","+interestInt+","+"'"+dateString+"',"+day_x+","+month_x+","+year_x+",'"+commentString+"');");
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
            datex.setText("");
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

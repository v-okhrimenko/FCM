package com.example.fcm.jobreview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fcm.AlarmResiver;
import com.example.fcm.NumberPicker;
import com.example.fcm.R;
import com.example.fcm.helper.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class FixedJobReview extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    {
        user = auth.getCurrentUser();
    }
    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");

    private TextView tv_name, tv_date, tv_description, tv_alarm;
    private EditText et_price;
    private Spinner sp_valuta;
    private Switch sw_status;
    private FloatingActionButton fabsetAlarm;
    private Button cancel;
    private Button ok;
    private String alarm;
    private String uid;
    private String t_piblish;
    private String docName;

    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_fixed_job_rewiev );

        tv_name = (TextView) findViewById( R.id.tv_name_jrFixed );
        et_price = (EditText) findViewById( R.id.et_price_jrFixed );
        tv_date = (TextView) findViewById( R.id.tv_date_jrFixed );
        tv_description = (TextView) findViewById( R.id.et_description_jrFixed );
        sp_valuta = (Spinner) findViewById( R.id.spinner_valuta );
        sw_status = (Switch) findViewById( R.id.sw_paid2 );
        tv_alarm = (TextView) findViewById( R.id.tv_alarm_jrFixed );
        fabsetAlarm = (FloatingActionButton) findViewById( R.id.fab_alarmSet_jrFixed );
        cancel = (Button) findViewById( R.id.btn_cancel );
        ok = (Button) findViewById( R.id.button_ok_edit );

        context = FixedJobReview.this;

        Intent intent = getIntent();
        docName = intent.getStringExtra("documentName");

        uid = intent.getStringExtra("uid");
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String date = intent.getStringExtra("date");
        String description = intent.getStringExtra("description");
        String valuta = intent.getStringExtra("valuta");
        String status = intent.getStringExtra("status");
        alarm = intent.getStringExtra("alarm");

        alarmCheck( alarm );

        tv_name.setText( name );
        et_price.setText( price);
        tv_date.setText( date );
        tv_description.setText( description );
        sp_valuta.setSelection( Helper.getValutaIndex( valuta ) );
        sw_status.setChecked( Boolean.valueOf( status ) );
        et_price.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_price.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        fabsetAlarm.setOnClickListener( v -> setAlarm() );
        cancel.setOnClickListener( v1-> cancel() );
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_price.getText().toString().trim().isEmpty()){
                    System.out.println("ADD PRICE PLEASE");
                    et_price.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );

                } else {

                    noteRef_addWork_Full.document( docName ).update( "price_fixed", Integer.valueOf( et_price.getText().toString()) );
                    noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", Float.valueOf( et_price.getText().toString()) );
                    noteRef_addWork_Full.document( docName ).update( "valuta", sp_valuta.getSelectedItem().toString() );
                    noteRef_addWork_Full.document( docName ).update( "status", sw_status.isChecked() );
                    noteRef_addWork_Full.document( docName ).update( "discription", tv_description.getText().toString());
                    finish();
                }


            }
        } );
    }

    private void alarmCheck(String d) {

        if (d==null){
            tv_alarm.setText( getString( R.string.AlarmNoSet ) );
            fabsetAlarm.setImageDrawable( getDrawable( R.drawable.alarm_off ) );

        } else {
            String t;
            Date date_test = new Date();
            Date x = new Date(d);
            if (x.before(date_test)){
                tv_alarm.setText( getString( R.string.AlarmNoSet ) );
//                        alarmSet.setImageDrawable( getDrawable( R.drawable.alarm_off ) );
                fabsetAlarm.setImageDrawable( getDrawable( R.drawable.alarm_off ) );

            } else {
//                        alarmSet.setImageDrawable( getDrawable( R.drawable.alarmsetico ) );
                fabsetAlarm.setImageDrawable( getDrawable( R.drawable.alarmsetico ) );
                Calendar cal = Calendar.getInstance();
                cal.setTime(x);
                String month_edit;
                int day = cal.get( Calendar.DAY_OF_MONTH );
                int month = (cal.get( Calendar.MONTH ));
                int year = cal.get( Calendar.YEAR );
                int h_= cal.get(Calendar.HOUR_OF_DAY);
                int m_= cal.get(Calendar.MINUTE);

                if(month<10) {
                    month_edit = "0"+(month+1);
                } else {
                    month_edit = String.valueOf( (month+1) );
                }
                if (m_<10){t = String.valueOf( h_+":"+0+m_ );
                    t_piblish=day+"-"+(month_edit)+"-"+year+"  "+t;
                }
                else {
                    t = h_+":"+m_ ;
                    t_piblish=day+"-"+(month_edit)+"-"+year+"  "+t;
                }
                tv_alarm.setText( t_piblish );
            }
            }


    }
    private void setAlarm() {

        AlertDialog.Builder builder = new AlertDialog.Builder( FixedJobReview.this);
        LayoutInflater inflater = LayoutInflater.from( FixedJobReview.this);
        final View regiserWindow = inflater.inflate(R.layout.my_year_month_date_infater, null);
        builder.setView(regiserWindow);

        final NumberPicker numbDay = regiserWindow.findViewById( R.id.np_DateSelect);
        final NumberPicker numbMonth = regiserWindow.findViewById( R.id.np_MonthSelect);
        final NumberPicker numbYear = regiserWindow.findViewById( R.id.np_YearSelect );

        final NumberPicker numbHour = regiserWindow.findViewById( R.id.np_HourSelect );
        final NumberPicker numbMinute = regiserWindow.findViewById( R.id.np_MinutSelect );

        final Button setReminder = regiserWindow.findViewById( R.id.btn_set_reminder );
        //final Button exit = regiserWindow.findViewById( R.id.btnCancel );
        final Button cancel = regiserWindow.findViewById( R.id.btn_cencel_reminder );

        numbDay.setMinValue( 1 );
        numbDay.setMaxValue( 31 );
        numbDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numbDay.setValue( Calendar.getInstance().get( Calendar.DAY_OF_MONTH ) );
        numbDay.setWrapSelectorWheel(true);

        String mValues[] = { getString( R.string.January ),getString( R.string.February ),getString( R.string.March ),
                getString( R.string.April ),getString( R.string.May ),getString( R.string.June ),getString( R.string.July ),
                getString( R.string.August ),getString( R.string.September ),getString( R.string.October ),getString( R.string.November ),
                getString( R.string.December )};
        setNubmerPicker(numbMonth,mValues);
        numbMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numbMonth.setValue(Calendar.getInstance().get( Calendar.MONTH ) );

        numbYear.setMinValue( 2010 );
        numbYear.setMaxValue( 2030 );
        numbYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numbYear.setValue( Calendar.getInstance().get( Calendar.YEAR ) );

        numbHour.setMinValue( 0 );
        numbHour.setMaxValue( 23 );
        numbHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numbHour.setValue( Calendar.getInstance().get( Calendar.HOUR_OF_DAY ) );

        String mValues_minute[] = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21",
                "22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43",
                "44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
        setNubmerPicker(numbMinute,mValues_minute);
        numbMinute.setMinValue( 0 );
        numbMinute.setMaxValue( 59 );
        numbMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numbMinute.setValue( Calendar.getInstance().get( Calendar.MINUTE ) );

        setDividerColor(numbMonth, Color.BLACK);
        setDividerColor(numbYear, Color.BLACK);
        setDividerColor(numbDay, Color.BLACK);
        setDividerColor(numbHour, Color.BLACK);
        setDividerColor(numbMinute, Color.BLACK);

        if(alarm==null) {

            Calendar c = Calendar.getInstance();
            numbDay.setValue( c.get( Calendar.DAY_OF_MONTH ) );
            numbMonth.setValue(c.get( Calendar.MONTH ) );
            numbYear.setValue(c.get( Calendar.YEAR ) );
            numbHour.setValue( c.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( c.get( Calendar.MINUTE ) );
        }

        else {

            Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
            Date x = new Date(alarm);
            calendar.setTime(x);   // assigns calendar to given date
            numbDay.setValue( calendar.get( Calendar.DAY_OF_MONTH ) );
            numbMonth.setValue(calendar.get( Calendar.MONTH ) );
            numbYear.setValue(calendar.get( Calendar.YEAR ) );
            numbHour.setValue( calendar.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( calendar.get(Calendar.MINUTE) );
        }

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        dialog.show();

            numbYear.setOnValueChangedListener( new android.widget.NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
            int year = Integer.valueOf(numbYear.getValue());
            int month = Integer.valueOf(numbMonth.getValue());
            Calendar calendarD = new GregorianCalendar(year, month, 1);
            int noOfDaysOfMonth = calendarD.getActualMaximum(Calendar.DAY_OF_MONTH);
            numbDay.setMaxValue( noOfDaysOfMonth );
        }
    } );

    numbMonth.setOnValueChangedListener( new android.widget.NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {

            int year = Integer.valueOf(numbYear.getValue());
            int month = Integer.valueOf(numbMonth.getValue());
            Calendar calendarD = new GregorianCalendar(year, month, 1);
            int noOfDaysOfMonth = calendarD.getActualMaximum(Calendar.DAY_OF_MONTH);
            numbDay.setMaxValue( noOfDaysOfMonth );

        }
    } );


        setReminder.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c1 = Calendar.getInstance();
                c1.set( Calendar.YEAR, numbYear.getValue() );
                c1.set( Calendar.MONTH, numbMonth.getValue() );
                c1.set( Calendar.DAY_OF_MONTH, numbDay.getValue() );
                c1.set( Calendar.HOUR_OF_DAY, numbHour.getValue() );
                c1.set( Calendar.MINUTE, numbMinute.getValue() );
                c1.set( Calendar.SECOND, 1 );
                Date alarm1DataToFB = c1.getTime();

                noteRef_addWork_Full.document( docName ).update( "alarm1", alarm1DataToFB );

                alarmManager = (AlarmManager) getBaseContext().getSystemService( ALARM_SERVICE );
                Intent my_intent = new Intent( FixedJobReview.this, AlarmResiver.class );
                my_intent.putExtra( "jobId", docName );
                pendingIntent = pendingIntent.getBroadcast( FixedJobReview.this, Integer.valueOf( uid ), my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
                alarmManager.set( AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent );
                alarm = String.valueOf(alarm1DataToFB);

                alarmCheck(alarm);
                dialog.dismiss();
            }
        } );

        //exit.setOnClickListener( v -> dialog.dismiss() );

        Locale locale = Resources.getSystem().getConfiguration().locale;

        cancel.setOnClickListener( v1 -> {
            noteRef_addWork_Full.document( docName ).update("alarm1", null );

            alarm = null;
            alarmManager = (AlarmManager) getBaseContext().getSystemService( ALARM_SERVICE );
            Intent my_intent = new Intent( FixedJobReview.this, AlarmResiver.class );
            my_intent.putExtra( "jobId", docName );

            pendingIntent = pendingIntent.getBroadcast( FixedJobReview.this, Integer.valueOf( uid ), my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
            alarmManager.cancel( pendingIntent );
            Toast.makeText( FixedJobReview.this, getString( R.string.AlarmCancel ), Toast.LENGTH_SHORT ).show();

            alarmCheck(alarm);
            dialog.dismiss();
    });
    }
    private void cancel(){
//        startActivity(new Intent( FixedJobReview.this, CalendarMainActivity.class));
//        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
//        overridePendingTransition(0, 0);
        finish();
    }
    private void setNubmerPicker(NumberPicker nubmerPicker,String [] numbers ){
        nubmerPicker.setMaxValue(numbers.length-1);
        nubmerPicker.setMinValue(0);
        nubmerPicker.setWrapSelectorWheel(true);
        nubmerPicker.setDisplayedValues(numbers);
    }
    private void setDividerColor(android.widget.NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = android.widget.NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}

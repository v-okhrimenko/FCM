package com.example.fcm.jobRewiev;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fcm.AlarmResiver;
import com.example.fcm.NumberPicker;
import com.example.fcm.R;
import com.example.fcm.helper.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ForHourJobRewiev extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    {
        user = auth.getCurrentUser();
    }
    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");

    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    private String uid, name, priceHour, startTime, endTime, finalCost, date, description, valuta, status, alarm, rounded_minutes, normal_result_price;
    private String t_piblish, docName;
    private Date startTimeToFB, finishTimeToFB;
    private TextView tv_name, tv_date, tv_description, tv_alarm;
    private EditText et_price, et_okruglenie;
    private Spinner sp_valuta;
    private Switch sw_status;
    private FloatingActionButton fabsetAlarm;
    private Button ok, cancel, start, stop, btn_info;

    private Context context;


    // COUNTER
    private Handler handler = new Handler();
    private Runnable runnable;

    private TextView tv_sec, tv_min, tv_hour, tv_result;

    long i = 0; // переменная счетчика каунтера
    long i2 = 0; // переменная счетчика каунтера
    long i3 = 0;
    float result = 0;
    double priceMinuta = 0;

    private final static Date EMPTYDATE = Helper.emptyDate();
    private Calendar today;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_for_hour_job_rewiev );



        tv_sec = findViewById(R.id.tv_counter_seconds);
        tv_min = findViewById(R.id.tv_counter_minutes);
        tv_hour = findViewById(R.id.tv_counter_hour);
        tv_result = findViewById(R.id.tv_counter_result);

        tv_name = (TextView) findViewById( R.id.tv_name_jrFixed );
        tv_date = (TextView) findViewById( R.id.tv_date_jrFixed );
        et_price = (EditText) findViewById( R.id.et_price_jrFixed );
        tv_description = (TextView) findViewById( R.id.et_description_jrFixed );
        sp_valuta = (Spinner) findViewById( R.id.spinner_valuta );
        sw_status = (Switch) findViewById( R.id.sw_paid2 );
        tv_alarm = (TextView) findViewById( R.id.tv_alarm_jrFixed );
        fabsetAlarm = (FloatingActionButton) findViewById( R.id.fab_alarmSet_jrFixed );
        cancel = (Button) findViewById( R.id.btn_cancel );
        ok = (Button) findViewById( R.id.button_ok_edit );
        start = (Button) findViewById( R.id.btn_start );
        stop = (Button) findViewById( R.id.btn_end );
        btn_info = (Button) findViewById( R.id.btn_info2 );
        et_okruglenie = (EditText) findViewById( R.id.et_okruglenie_minut );

        context = ForHourJobRewiev.this;

        Intent intent = getIntent();
        docName = intent.getStringExtra("documentName");
        uid = intent.getStringExtra("uid");
        name = intent.getStringExtra("name");
        priceHour = intent.getStringExtra("priceHour");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        finalCost = intent.getStringExtra("finalCost");
        date = intent.getStringExtra("date");
        description = intent.getStringExtra("description");
        valuta = intent.getStringExtra("valuta");
        status = intent.getStringExtra("status");
        alarm = intent.getStringExtra("alarm");
        rounded_minutes = intent.getStringExtra("rounded_nimutes");

        alarmCheck( alarm );

        et_okruglenie.setText( rounded_minutes );
        tv_name.setText( name );
        tv_date.setText( date );
        et_price.setText( priceHour );
        tv_description.setText( description );
        priceMinuta = Double.valueOf( priceHour )/60;

        today = Calendar.getInstance(); // cейчас

        sp_valuta.setSelection(Helper.getValutaIndex( valuta ));
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

        if(startTime.equals( "null" )){
            start.setText( getString( R.string.Start ) );
            stop.setEnabled( false );
        } else {

            if(!endTime.equals( "null" )){

                    setFinishTimerTimeCorrect();
                    getSum();

            }
            else {
                Calendar today = Calendar.getInstance();
                Date date_start_check_date = new Date(startTime);
                Calendar date_start_check = Calendar.getInstance();
                date_start_check.setTime(date_start_check_date);

                if(date_start_check.after( today )){

                    String hr = String.valueOf(date_start_check.get(Calendar.HOUR_OF_DAY));
                    if (date_start_check.get( Calendar.HOUR_OF_DAY )<10 || date_start_check.get( Calendar.HOUR_OF_DAY )==0 ){
                        hr = "0"+date_start_check.get( Calendar.HOUR_OF_DAY);
                    }
                    String mnt = String.valueOf(date_start_check.get(Calendar.MINUTE));
                    if (date_start_check.get( Calendar.MINUTE )<10 || date_start_check.get( Calendar.MINUTE )==0 ){
                        mnt = "0"+date_start_check.get( Calendar.MINUTE);
                    }
                    start.setText( hr+ ":" +  mnt);

                    stop.setEnabled( true );


                } else {
                    setStartTimerTimeCorrect();
                }


            }


        }

        btn_info.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.showInfoRounded( context );
            }
        } );
        stop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);

                AlertDialog.Builder builder = new AlertDialog.Builder( ForHourJobRewiev.this);
                LayoutInflater inflater = LayoutInflater.from( ForHourJobRewiev.this);
                final View regiserWindow = inflater.inflate(R.layout.set_stop_time_inflater, null);
                builder.setView(regiserWindow);

                final NumberPicker numbHour = regiserWindow.findViewById( R.id.np_HourSelect );
                final NumberPicker numbMinute = regiserWindow.findViewById( R.id.np_MinutSelect );
                final DatePicker datePicker = regiserWindow.findViewById(R.id.dateSelectToReminder2);

                final Button cancel = regiserWindow.findViewById( R.id.btn_cencel_timer );
                final Button set = regiserWindow.findViewById( R.id.btn_set_timer );
                final FloatingActionButton del = regiserWindow.findViewById( R.id.fab_clear_start_timer2 );
                //final TextView tv_finisDate = regiserWindow.findViewById(R.id.tv_finishDate);

                Calendar calendar_start = Calendar.getInstance(); // creates a new calendar instance
                Date x = new Date(startTime);
                calendar_start.setTime(x);

                numbHour.setMinValue( 0 );
                numbHour.setMaxValue( 23 );
                numbHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


                String mValues_minute[] = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21",
                        "22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43",
                        "44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
                setNubmerPicker(numbMinute,mValues_minute);
                numbMinute.setMinValue( 0 );
                numbMinute.setMaxValue( 59 );

                numbMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                if(!endTime.equals( "null" )){
                    Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
                    Date x_ = new Date(endTime);
                    calendar.setTime(x_);   // assigns calendar to given date
                    numbHour.setValue( calendar.get( Calendar.HOUR_OF_DAY ) );
                    numbMinute.setValue( calendar.get(Calendar.MINUTE) );
                    datePicker.updateDate( calendar.get( Calendar.YEAR ),calendar.get( Calendar.MONTH ) , calendar.get( Calendar.DAY_OF_MONTH ));
                }
                else {

                    Calendar calendar_end = Calendar.getInstance(); // creates a new calendar instance
                    Date dateS = new Date(startTime);
                    calendar_end.setTime( dateS );
                    datePicker.updateDate( calendar_end.get( Calendar.YEAR ),calendar_end.get( Calendar.MONTH ),calendar_end.get( Calendar.DAY_OF_MONTH ));
                    numbHour.setValue( calendar_end.get( Calendar.HOUR_OF_DAY ) );
                    numbMinute.setValue( calendar_end.get( Calendar.MINUTE ) );
                }
//
                setDividerColor(numbHour, Color.BLACK);
                setDividerColor(numbMinute, Color.BLACK);



                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( false );
                dialog.show();
                del.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //setNoFinish();
                        stop.setText( "00:00" );
                        endTime = "null";
                        tv_sec.setText( "0" );
                        tv_min.setText( "0" );
                        tv_hour.setText(  "0" );
                        tv_result.setText(String.valueOf("0"));
                        noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", null );
                        dialog.dismiss();
                    }
                } );
                set.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();
                        int hour = numbHour.getValue();
                        int minute = numbMinute.getValue();

                        Calendar calendar_end = Calendar.getInstance();
                        calendar_end.set( Calendar.DAY_OF_MONTH, day );
                        calendar_end.set( Calendar.MONTH, month );
                        calendar_end.set( Calendar.YEAR, year );
                        calendar_end.set(Calendar.HOUR_OF_DAY, hour);
                        calendar_end.set(Calendar.MINUTE, minute);


                        //System.out.println(calendar_end.getTime());
                        if(calendar_end.before( calendar_start )){
                            System.out.println("ТАКОГО БЫТЬ НЕ МОЖЕТ КАЛЕНДАРЬ НАОБОРОТ");

                            AlertDialog.Builder builder = new AlertDialog.Builder( ForHourJobRewiev.this);
                            LayoutInflater inflater = LayoutInflater.from( ForHourJobRewiev.this);
                            final View regiserWindow = inflater.inflate(R.layout.show_alert_info_one_button, null);
                            builder.setView(regiserWindow);


                            final TextView shapka = regiserWindow.findViewById( R.id.tv_info_shapka );
                            shapka.setText( getResources().getString( R.string.Incorrect_time ) );
                            final TextView txt = regiserWindow.findViewById( R.id.tv_info_txt);
                            txt.setText( getResources().getString( R.string.no_correct_end_time ) );


                            final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                            ok.setOnClickListener( v1 -> dialog.dismiss() );

                            final AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                            dialog.setCancelable( false );
                            dialog.show();

                            ok.setOnClickListener( v1 -> dialog.dismiss() );



                        } else {


                            finishTimeToFB = calendar_end.getTime();
                            endTime = String.valueOf(finishTimeToFB);


                            setFinishTimerTimeCorrect();

                            dialog.dismiss();
                            handler.removeCallbacks(runnable);
                            getSum();


                        }


                    }
                } );
                cancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //millisecontToMinutes();
                        dialog.dismiss();
                    }
                } );

            }
        } );
        start.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);

                AlertDialog.Builder builder = new AlertDialog.Builder( ForHourJobRewiev.this);
                LayoutInflater inflater = LayoutInflater.from( ForHourJobRewiev.this);
                final View regiserWindow = inflater.inflate(R.layout.set_start_time_inflater, null);
                builder.setView(regiserWindow);

                final NumberPicker numbHour = regiserWindow.findViewById( R.id.np_HourSelect );
                final NumberPicker numbMinute = regiserWindow.findViewById( R.id.np_MinutSelect );
                final Button cancel = regiserWindow.findViewById( R.id.btn_cencel_timer );
                final Button set = regiserWindow.findViewById( R.id.btn_set_timer );
                final FloatingActionButton delete = regiserWindow.findViewById( R.id.fab_clear_start_timer );

                numbHour.setMinValue( 0 );
                numbHour.setMaxValue( 23 );
                numbHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


                String mValues_minute[] = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21",
                        "22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43",
                        "44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
                setNubmerPicker(numbMinute,mValues_minute);
                numbMinute.setMinValue( 0 );
                numbMinute.setMaxValue( 59 );
                numbMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                if(!startTime.equals( "null" )){
                    Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
                    Date x = new Date(startTime);
                    calendar.setTime(x);   // assigns calendar to given date
                    numbHour.setValue( calendar.get( Calendar.HOUR_OF_DAY ) );
                    numbMinute.setValue( calendar.get(Calendar.MINUTE) );
                }
                else {
                    numbHour.setValue( Calendar.getInstance().get( Calendar.HOUR_OF_DAY ) );
                    numbMinute.setValue( Calendar.getInstance().get( Calendar.MINUTE ) );
                }
//
                setDividerColor(numbHour, Color.BLACK);
                setDividerColor(numbMinute, Color.BLACK);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( false );
                dialog.show();

                cancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //millisecontToMinutes();
                        dialog.dismiss();
                    }
                } );

                delete.setOnClickListener( new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder( ForHourJobRewiev.this);
                        LayoutInflater inflater = LayoutInflater.from( ForHourJobRewiev.this);
                        final View regiserWindow = inflater.inflate(R.layout.template_is_present_info_inflater, null);

                        final TextView shapka = regiserWindow.findViewById(R.id.tv_shapka_name);
                        final TextView txt = regiserWindow.findViewById(R.id.textView15);
                        final Button delete = regiserWindow.findViewById(R.id.btn_update);
                        final Button cancel = regiserWindow.findViewById(R.id.btn_change_name_1);


                        shapka.setText( getResources().getString( R.string.Delete_start_time ) );
                        txt.setText(  getResources().getString( R.string.Delete_start_time_txt ) );
                        delete.setText( getResources().getString( R.string.delete ) );
                        delete.setBackgroundTintList(  ContextCompat.getColorStateList( ForHourJobRewiev.this, R.color.red) );
                        cancel.setText( getResources().getString( R.string.cancel ) );

                        builder.setView(regiserWindow);

                        final AlertDialog dialog2 = builder.create();
                        dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                        dialog2.setCancelable( false );
                        dialog2.show();
                        cancel.setOnClickListener( v1 -> dialog2.dismiss() );
                        delete.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setNoStartFinish();
//                                startTime= null;
//                                endTime = null;

                                dialog2.dismiss();
                                dialog.dismiss();
                            }
                        } );


                    }
                } );

                set.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        handler.removeCallbacks(runnable);

                        //System.out.println(ConvertStringToDate(date) + " ±!");
                        Calendar c1 = Calendar.getInstance();
                        Date x = ConvertStringToDate(date);
                        c1.setTime(x);
                        c1.set( Calendar.HOUR_OF_DAY, numbHour.getValue() );
                        c1.set( Calendar.MINUTE, numbMinute.getValue() );
                        c1.set( Calendar.SECOND, 1 );

//                        System.out.println("C1" + c1.getTime());
//                        System.out.println("START " + startTime+ " End "+endTime);

                        if(!endTime.equals( "null" )){

                            Date e = new Date(endTime);
                            Calendar calendar_end = Calendar.getInstance();
                            calendar_end.setTime(e);

                            if(!startTime.equals( "null" )){
                                if(c1.getTime().after(today.getTime())){

                                    if(calendar_end.before( c1 )){
                                        //System.out.println("ТАКОГО БЫТЬ НЕ МОЖЕТ КАЛЕНДАРЬ НАОБОРОТ");
                                        AlertDialog.Builder builder = new AlertDialog.Builder( ForHourJobRewiev.this);
                                        LayoutInflater inflater = LayoutInflater.from( ForHourJobRewiev.this);
                                        final View regiserWindow = inflater.inflate(R.layout.show_alert_info_one_button, null);
                                        builder.setView(regiserWindow);
                                        final TextView shapka = regiserWindow.findViewById( R.id.tv_info_shapka );
                                        shapka.setText( getResources().getString( R.string.Incorrect_time ) );
                                        final TextView txt = regiserWindow.findViewById( R.id.tv_info_txt);
                                        txt.setText( getResources().getString( R.string.no_correct_start_time ) );
                                        final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                        ok.setOnClickListener( v1 -> dialog.dismiss() );
                                        final AlertDialog dialog = builder.create();
                                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                        dialog.setCancelable( false );
                                        dialog.show();

                                        ok.setOnClickListener( v1 -> dialog.dismiss() ); } else {
                                    //System.out.println("AFFER TODAY");
                                    startTimeToFB = c1.getTime();
                                    startTime = String.valueOf(startTimeToFB);

                                    String hr = String.valueOf(c1.get(Calendar.HOUR_OF_DAY));
                                    if (c1.get( Calendar.HOUR_OF_DAY )<10 || c1.get( Calendar.HOUR_OF_DAY )==0 ){
                                        hr = "0"+c1.get( Calendar.HOUR_OF_DAY);
                                    }
                                    String mnt = String.valueOf(c1.get(Calendar.MINUTE));
                                    if (c1.get( Calendar.MINUTE )<10 || c1.get( Calendar.MINUTE )==0 ){
                                        mnt = "0"+c1.get( Calendar.MINUTE);
                                    }
                                    start.setText( hr+ ":" +  mnt);
                                        getSum();
                                        //setStartTimerTimeCorrect();
                                        //setFinishTimerTimeCorrect();
                                    dialog.dismiss();

                                }
                                } else {
                                    if(calendar_end.before( c1 )){
                                        //System.out.println("ТАКОГО БЫТЬ НЕ МОЖЕТ КАЛЕНДАРЬ НАОБОРОТ");
                                        AlertDialog.Builder builder = new AlertDialog.Builder( ForHourJobRewiev.this);
                                        LayoutInflater inflater = LayoutInflater.from( ForHourJobRewiev.this);
                                        final View regiserWindow = inflater.inflate(R.layout.show_alert_info_one_button, null);
                                        builder.setView(regiserWindow);
                                        final TextView shapka = regiserWindow.findViewById( R.id.tv_info_shapka );
                                        shapka.setText( getResources().getString( R.string.Incorrect_time ) );
                                        final TextView txt = regiserWindow.findViewById( R.id.tv_info_txt);
                                        txt.setText( getResources().getString( R.string.no_correct_start_time ) );
                                        final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                        ok.setOnClickListener( v1 -> dialog.dismiss() );
                                        final AlertDialog dialog = builder.create();
                                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                        dialog.setCancelable( false );
                                        dialog.show();

                                        ok.setOnClickListener( v1 -> dialog.dismiss() ); }
                                    else {
                                        startTimeToFB = c1.getTime();
                                        startTime = String.valueOf(startTimeToFB);

                                        String hr = String.valueOf(c1.get(Calendar.HOUR_OF_DAY));
                                        if (c1.get( Calendar.HOUR_OF_DAY )<10 || c1.get( Calendar.HOUR_OF_DAY )==0 ){
                                            hr = "0"+c1.get( Calendar.HOUR_OF_DAY);
                                        }
                                        String mnt = String.valueOf(c1.get(Calendar.MINUTE));
                                        if (c1.get( Calendar.MINUTE )<10 || c1.get( Calendar.MINUTE )==0 ){
                                            mnt = "0"+c1.get( Calendar.MINUTE);
                                        }
                                        start.setText( hr+ ":" +  mnt);
                                        getSum();

                                        dialog.dismiss();
                                    }
                                }

                            }

                        }
                            else{
                                //еслм END NULL

                            if(startTime.equals( "null" )){



                                //System.out.println("_______NUUULLLLL");
                                int hour = numbHour.getValue();
                                int minute = numbMinute.getValue();
                                Date d = ConvertStringToDate( date );
                                Calendar c = Calendar.getInstance();
                                c.setTime(d);
                                c.set( Calendar.HOUR_OF_DAY, hour );
                                c.set( Calendar.MINUTE, minute );


                                String hr = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
                                if (c.get( Calendar.HOUR_OF_DAY )<10 || c.get( Calendar.HOUR_OF_DAY )==0 ){
                                    hr = "0"+c.get( Calendar.HOUR_OF_DAY);
                                }
                                String mnt = String.valueOf(c.get(Calendar.MINUTE));
                                if (c.get( Calendar.MINUTE )<10 || c.get( Calendar.MINUTE )==0 ){
                                    mnt = "0"+c.get( Calendar.MINUTE);
                                }
                                start.setText( hr+ ":" +  mnt);
                                stop.setEnabled( true );
                                startTimeToFB = c.getTime();
                                startTime = String.valueOf( startTimeToFB );


                                Calendar today = Calendar.getInstance();
                                today.set( Calendar.HOUR_OF_DAY, 0 );
                                today.set( Calendar.MINUTE, 0 );
                                today.set( Calendar.SECOND, 0 );
                                today.set( Calendar.MILLISECOND, 0 );

//
                                Calendar todayStartCheck = Calendar.getInstance();
                                 // set date start job from intent
                                todayStartCheck.setTime( c.getTime() );
                                todayStartCheck.set( Calendar.HOUR_OF_DAY, 0 );
                                todayStartCheck.set( Calendar.MINUTE, 0 );
                                todayStartCheck.set( Calendar.SECOND, 0 );
                                todayStartCheck.set( Calendar.MILLISECOND, 0 );

                                if(today.equals( todayStartCheck )){
                                    setStartTimerTimeCorrect();
                                    dialog.dismiss();
                                } else {

                                    dialog.dismiss();
                                }


                            } else {

                                int hour = numbHour.getValue();
                                int minute = numbMinute.getValue();


                                Date setDayToStart = new Date( startTime );

                                Calendar calendar_start = Calendar.getInstance();
                                calendar_start.setTime( setDayToStart );

                                calendar_start.set( Calendar.HOUR_OF_DAY, hour );
                                calendar_start.set( Calendar.MINUTE, minute );
//                                System.out.println( "________________________" );
//                                System.out.println( "TODAY " + today.getTime() );
//                                System.out.println( "START " + calendar_start.getTime() );


                                if (today.getTime().before( calendar_start.getTime() )) {
                                    //System.out.println( "!!!! РАНЬШЕ" );
                                    String hr = String.valueOf( calendar_start.get( Calendar.HOUR_OF_DAY ) );
                                    if (calendar_start.get( Calendar.HOUR_OF_DAY ) < 10 || calendar_start.get( Calendar.HOUR_OF_DAY ) == 0) {
                                        hr = "0" + calendar_start.get( Calendar.HOUR_OF_DAY );
                                    }
                                    String mnt = String.valueOf( calendar_start.get( Calendar.MINUTE ) );
                                    if (calendar_start.get( Calendar.MINUTE ) < 10 || calendar_start.get( Calendar.MINUTE ) == 0) {
                                        mnt = "0" + calendar_start.get( Calendar.MINUTE );
                                    }
                                    start.setText( hr + ":" + mnt );
                                    stop.setEnabled( true );

                                    tv_sec.setText( "0" );
                                    tv_min.setText( "0" );
                                    tv_hour.setText( "0" );
                                    //setStartTimerTimeCorrect();
                                    startTimeToFB = calendar_start.getTime();
                                    startTime = String.valueOf( startTimeToFB );
                                    dialog.dismiss();


                                    //System.out.println("TODAY "+today.getTime());
                                } else {

                                    startTimeToFB = c1.getTime();
                                    startTime = String.valueOf( startTimeToFB );
                                    stop.setEnabled( true );
                                    //startCountTimer( "__" );
                                    setStartTimerTimeCorrect();
                                    millisecontToMinutes();
                                    dialog.dismiss();
                                }

                            }}

                    }
                } );
            }
        } );
        fabsetAlarm.setOnClickListener( v -> setAlarm() );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handler.removeCallbacks(runnable);
                cancel();
            }
        } );
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_price.getText().toString().trim().isEmpty()){
                    System.out.println("ADD PRICE PLEASE");
                    et_price.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );

                } else {
                    if(!startTime.equals( "null" )){
                    Date s = new Date(startTime);
                    noteRef_addWork_Full.document( docName ).update( "start", s );


                        if(!endTime.equals( "null" )){
                            Date f = new Date(endTime);
                            noteRef_addWork_Full.document( docName ).update( "end", f );
                            getSum();
                            //float fl = Float.valueOf(normal_result_price);
                            noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", result  );//
                            // TODO: 14.04.2020


                        } else {
                            noteRef_addWork_Full.document( docName ).update( "end", null );
                        }


                    noteRef_addWork_Full.document( docName ).update( "price_hour", Integer.valueOf( et_price.getText().toString()) );
                    noteRef_addWork_Full.document( docName ).update( "valuta", sp_valuta.getSelectedItem().toString() );
                    noteRef_addWork_Full.document( docName ).update( "status", sw_status.isChecked() );
                    noteRef_addWork_Full.document( docName ).update( "discription", tv_description.getText().toString());
                    if(et_okruglenie.getText().toString().isEmpty() || Integer.valueOf(et_okruglenie.getText().toString().trim())>59){
                        noteRef_addWork_Full.document( docName ).update( "rounded_minut", 15 );
                    } else {

                        noteRef_addWork_Full.document( docName ).update( "rounded_minut", Integer.valueOf( et_okruglenie.getText().toString().trim()) );
                    }
                    handler.removeCallbacks(runnable);
                    finish();
                }
                    else{

                        noteRef_addWork_Full.document( docName ).update( "start", null );
                        noteRef_addWork_Full.document( docName ).update( "end", null );

                        noteRef_addWork_Full.document( docName ).update( "price_hour", Integer.valueOf( et_price.getText().toString()) );
                        noteRef_addWork_Full.document( docName ).update( "valuta", sp_valuta.getSelectedItem().toString() );
                        noteRef_addWork_Full.document( docName ).update( "status", sw_status.isChecked() );
                        noteRef_addWork_Full.document( docName ).update( "discription", tv_description.getText().toString());
                        if(et_okruglenie.getText().toString().isEmpty() || Integer.valueOf(et_okruglenie.getText().toString().trim())>59){
                            noteRef_addWork_Full.document( docName ).update( "rounded_minut", 15 );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "rounded_minut", Integer.valueOf( et_okruglenie.getText().toString().trim()) );
                        }
                        handler.removeCallbacks(runnable);
                        finish();
                    }
                }
            }
        } );
    }


    private void setNoStartFinish() {
        start.setText( getString( R.string.Start ) );
        stop.setText( "00:00" );
        stop.setEnabled( false );
        startTime = "null";
        endTime = "null";
        tv_sec.setText( "0" );
        tv_min.setText( "0" );
        tv_hour.setText(  "0" );
        tv_result.setText(String.valueOf("0"));
        noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", 0.0 );

    }
    private void getSum() {

        Date dd = new Date(startTime);
        Calendar strt_calend = Calendar.getInstance();
        strt_calend.setTime(dd);

        Date dn = new Date(endTime);
        Calendar fin_calend = Calendar.getInstance();
        fin_calend.setTime(dn);


        long start = strt_calend.getTimeInMillis();
        long finish = fin_calend.getTimeInMillis();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(finish-start);
        long minut = seconds/60;
        long x = minut*60;
        long s = seconds-x;
        long ostatok;
        float ostatok_minut_price=0;


        if (minut>=60){
            i3 = minut/60;
            i2 = minut-(i3*60);
            i = seconds - ((i2*60)+((i3*60)*60));


        } else {

            i3 = 0;
            i2 = minut;
            i = s;

        }
        tv_sec.setText( String.valueOf( i ) );
        tv_min.setText( String.valueOf( i2 ) );
        tv_hour.setText(  String.valueOf( i3 ) );
        ostatok_minut_price = ( Helper.fcm_rounded_minutes(i2, Integer.parseInt( et_price.getText().toString().trim()), Integer.parseInt(rounded_minutes)));
        result = (Integer.parseInt( et_price.getText().toString().trim())*i3)+ostatok_minut_price;
        normal_result_price = String.format("%.2f", result);
        tv_result.setText(String.valueOf( normal_result_price ));
    }
    private void setStartTimerTimeCorrect() {
        Date d = new Date(startTime);
        System.out.println(d);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        String hr = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        if (c.get( Calendar.HOUR_OF_DAY )<10 || c.get( Calendar.HOUR_OF_DAY )==0 ){
            hr = "0"+c.get( Calendar.HOUR_OF_DAY);
        }
        String mnt = String.valueOf(c.get(Calendar.MINUTE));
        if (c.get( Calendar.MINUTE )<10 || c.get( Calendar.MINUTE )==0 ){
            mnt = "0"+c.get( Calendar.MINUTE);
        }
        start.setText( hr+ ":" +  mnt);
        stop.setEnabled( true );
        millisecontToMinutes();
    }
    private void setFinishTimerTimeCorrect() {

        Date d = new Date(startTime);
        Calendar c_ = Calendar.getInstance();
        c_.setTime(d);
        String hr_s = String.valueOf(c_.get(Calendar.HOUR_OF_DAY));
        if (c_.get( Calendar.HOUR_OF_DAY )<10 || c_.get( Calendar.HOUR_OF_DAY )==0 ){
            hr_s = "0"+c_.get( Calendar.HOUR_OF_DAY);
        }
        String mnt_s = String.valueOf(c_.get(Calendar.MINUTE));
        if (c_.get( Calendar.MINUTE )<10 || c_.get( Calendar.MINUTE )==0 ){
            mnt_s = "0"+c_.get( Calendar.MINUTE);
        }
        start.setText( hr_s+ ":" +  mnt_s);


        Date d_ = new Date(endTime);
        Calendar c = Calendar.getInstance();
        c.setTime(d_);
        String hr = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        if (c.get( Calendar.HOUR_OF_DAY )<10 || c.get( Calendar.HOUR_OF_DAY )==0 ){
            hr = "0"+c.get( Calendar.HOUR_OF_DAY);
        }
        String mnt = String.valueOf(c.get(Calendar.MINUTE));
        if (c.get( Calendar.MINUTE )<10 || c.get( Calendar.MINUTE )==0 ){
            mnt = "0"+c.get( Calendar.MINUTE);
        }
        int test = (int)c.get( Calendar.MONTH )+1;
        String correctMonth = String.valueOf( test );
        if(test<10){
            correctMonth = "0"+test;
        }
        String correctDayOfMonth = String.valueOf(c.get( Calendar.DAY_OF_MONTH ));
        if(c.get( Calendar.DAY_OF_MONTH )<10){
            correctDayOfMonth = "0"+String.valueOf(c.get( Calendar.DAY_OF_MONTH ));

        }

        String date_to_button = correctDayOfMonth+ "-"+correctMonth+ "-"+ c.get( Calendar.YEAR );
        String time_to_button = hr+ ":" +  mnt;
        //start.setText( hr+ ":" +  mnt);
        stop.setText(date_to_button+"\n"+time_to_button );
    }

    private void startCountTimer (String startTime) {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    handler.postDelayed(this, 1000);
                    if(i==60){
                        i=0;
                        tv_sec.setText( String.valueOf( i ) );
                        i2=i2+1;

                        if(i2==60){
                            i2=0;
                            tv_min.setText( String.valueOf( i2 ) );
                            i3 = i3+1;
                            tv_hour.setText( String.valueOf(i3 ));

                            result = Integer.valueOf(priceHour)*i3;

                        }}

                    tv_sec.setText( String.valueOf( i ) );
                    i++;

                    tv_min.setText( String.valueOf( i2 ) );
                    tv_hour.setText(  String.valueOf( i3 ) );
                    result = Integer.valueOf(priceHour)*i3;
                    tv_result.setText(String.valueOf( result ));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
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

        AlertDialog.Builder builder = new AlertDialog.Builder( ForHourJobRewiev.this);
        LayoutInflater inflater = LayoutInflater.from( ForHourJobRewiev.this);
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
                Intent my_intent = new Intent( ForHourJobRewiev.this, AlarmResiver.class );
                my_intent.putExtra( "jobId", docName );
                pendingIntent = pendingIntent.getBroadcast( ForHourJobRewiev.this, Integer.valueOf( uid ), my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
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
            Intent my_intent = new Intent( ForHourJobRewiev.this, AlarmResiver.class );
            my_intent.putExtra( "jobId", docName );

            pendingIntent = pendingIntent.getBroadcast( ForHourJobRewiev.this, Integer.valueOf( uid ), my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
            alarmManager.cancel( pendingIntent );
            Toast.makeText( ForHourJobRewiev.this, getString( R.string.AlarmCancel ), Toast.LENGTH_SHORT ).show();

            alarmCheck(alarm);
            dialog.dismiss();
        });
    }
    private void cancel(){
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
    private Date ConvertStringToDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("ddd-MM-yyy", Locale.getDefault() );
        Date date  = null;
        try {
            date = format.parse( eventDate );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    private void millisecontToMinutes(){

            Date dd = new Date(startTime);
            Calendar cc = Calendar.getInstance();
            cc.setTime(dd);
////
            long st = cc.getTimeInMillis();
            long f = Calendar.getInstance().getTimeInMillis();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(f-st);
            //long seconds = 7158;
            long minut = seconds/60;
            long x = minut*60;
            long s = seconds-x;


            if (minut>=60){
                i3 = minut/60;
                i2 = minut-(i3*60);
                i = seconds - ((i2*60)+((i3*60)*60));
                startCountTimer( "1");



            } else {
                System.out.println(minut + " "+ s);

                i3 = 0;
                i2 = minut;
                i = s;
                startCountTimer( "1");
        }
    }
}

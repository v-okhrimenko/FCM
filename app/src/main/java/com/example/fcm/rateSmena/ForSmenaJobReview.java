package com.example.fcm.rateSmena;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.fcm.other.AlarmResiver;
import com.example.fcm.other.NumberPicker;
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

public class ForSmenaJobReview extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private float overTimeSummaResult;
    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document( "My DB" ).collection( "MyWorksFull" );


    private String uid, name, priceShift, startTime, endTime, finalCost, date, description, valuta, status, alarm, rounded_minutes, duration_smena, overtime_procent, half_shift, half_shift_hours;

    private String t_piblish, docName;
    private Date startTimeToFB, finishTimeToFB;
    private TextView tv_name, tv_date, tv_alarm;
    private EditText tv_description, et_price, et_okruglenie, et_duration_smena, et_overtime_procent, et_half_shiht;

    private Spinner sp_valuta;
    private Switch sw_status, sw_half_hour;
    private FloatingActionButton fabsetAlarm;
    private Button ok, cancel;
    private Button start, stop, btn_info, btn_info_overtime;

    private Calendar now;
    private Calendar startTimeCalendar, endTimeCalendar;
    // COUNTER
    private Handler handler = new Handler();
    private Runnable runnable;

    CountDownTimer t;

    long i = 0; // переменная счетчика каунтера
    long i2 = 0; // переменная счетчика каунтера
    long i3 = 0;
    float result = 0;
    double priceMinuta = 0;

    long hour_final = 0;
    long minut_final = 0;
    long howManyovertimeHourMinutes = 0;

    long durationInMsecond;
    long halfInMsecond;

    private ConstraintLayout cl_overTime_layout;
    private TextView tv_overTime_hours, tv_overTime_minutes, tv_overTime_summa;

    private TextView tv_sec, tv_min, tv_hour, tv_result, tv_otrabotano_start_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_for_smena_job_rewiew_new );

        View decorView = getWindow().getDecorView();
        Helper.hideSystemUI( decorView );

        tv_name = (TextView) findViewById( R.id.tv_name_jrShift );
        tv_date = (TextView) findViewById( R.id.tv_date_jrShift );
        et_price = (EditText) findViewById( R.id.et_price_for_shift );
        tv_description = (EditText) findViewById( R.id.et_description_jrShift );
        sp_valuta = (Spinner) findViewById( R.id.spinner_valuta );
        sw_status = (Switch) findViewById( R.id.sw_paid2 );
        tv_alarm = (TextView) findViewById( R.id.tv_alarm_jrShift );
        fabsetAlarm = (FloatingActionButton) findViewById( R.id.fab_alarmSet_jrShift );
        cancel = (Button) findViewById( R.id.btn_cancel );
        ok = (Button) findViewById( R.id.button_ok_edit );
        start = (Button) findViewById( R.id.btn_start );
        stop = (Button) findViewById( R.id.btn_end );
        btn_info = (Button) findViewById( R.id.btn_info );
        btn_info_overtime = (Button) findViewById( R.id.btn_info_overtime );

        et_okruglenie = (EditText) findViewById( R.id.et_okruglenie_minut );
        et_duration_smena = (EditText) findViewById( R.id.et_dlitelnost_smeny );
        et_overtime_procent = (EditText) findViewById( R.id.et_procent_overtime );
        sw_half_hour = (Switch) findViewById( R.id.switch_half_shift );
        et_half_shiht = (EditText) findViewById( R.id.et_half_shift );

        tv_sec = findViewById( R.id.tv_counter_seconds );
        tv_min = findViewById( R.id.tv_counter_minutes );
        tv_hour = findViewById( R.id.tv_counter_hour );
        tv_result = findViewById( R.id.tv_counter_result );
        tv_otrabotano_start_txt = findViewById( R.id.tv_otrabotano_start_txt );

        cl_overTime_layout = findViewById( R.id.cl_overtime );
        tv_overTime_hours = findViewById( R.id.tv_overTime_hours );
        tv_overTime_minutes = findViewById( R.id.tv_overTime_minutes );
        tv_overTime_summa = findViewById( R.id.tv_overTime_summa );

        cl_overTime_layout.setVisibility( View.INVISIBLE );


        Intent intent = getIntent();
        docName = intent.getStringExtra( "documentName" );
        uid = intent.getStringExtra( "uid" );
        name = intent.getStringExtra( "name" );
        priceShift = intent.getStringExtra( "priceShift" );
        startTime = intent.getStringExtra( "startTime" );
        endTime = intent.getStringExtra( "endTime" );
        finalCost = intent.getStringExtra( "finalCost" );
        date = intent.getStringExtra( "date" );
        description = intent.getStringExtra( "description" );
        valuta = intent.getStringExtra( "valuta" );
        status = intent.getStringExtra( "status" );
        alarm = intent.getStringExtra( "alarm" );
        rounded_minutes = intent.getStringExtra( "rounded_nimutes" );
        duration_smena = intent.getStringExtra( "durationSm" );
        overtime_procent = intent.getStringExtra( "overTimeProcent" );
        half_shift = intent.getStringExtra( "half_shiht" );
        half_shift_hours = intent.getStringExtra( "half_shiht_hours" );


        nowUpdate();

        initStartEnd(); // update calendar data
        initData(); // заполняем поля из интента
        alarmCheck( alarm );
        chekStartEnd();
        checkTimer();


        sw_half_hour.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    et_half_shiht.setEnabled( true );
                } else {
                    et_half_shiht.setEnabled( false );
                }
            }
        } );
        tv_name.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tv_name.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
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

        durationInMsecond = (Long.parseLong( et_duration_smena.getText().toString().trim() ) * 60 * 60 * 1000);

        try {
            halfInMsecond = (Long.parseLong( et_half_shiht.getText().toString().trim() ) * 60 * 60 * 1000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        fabsetAlarm.setOnClickListener( v -> setAlarm() );

        btn_info.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.showInfoRounded( ForSmenaJobReview.this );
            }
        } );

        btn_info_overtime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.showInfoOvertime( ForSmenaJobReview.this );
            }
        } );

        cancel.setOnClickListener( v1 -> finish() );

        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
            }
        } );

        start.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressStartButton();
            }
        } );

        stop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressStopButton();
            }
        } );


    }

    private void pressStopButton() {
        nowUpdate();
        AlertDialog.Builder builder = new AlertDialog.Builder( ForSmenaJobReview.this);
        LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this);
        final View regiserWindow = inflater.inflate(R.layout.set_stop_time_inflater, null);
        builder.setView(regiserWindow);

        final NumberPicker numbHour = regiserWindow.findViewById( R.id.np_HourSelect );
        final NumberPicker numbMinute = regiserWindow.findViewById( R.id.np_MinutSelect );
        final DatePicker datePicker = regiserWindow.findViewById(R.id.dateSelectToReminder2);

        final Button cancel = regiserWindow.findViewById( R.id.btn_cencel_timer );
        final Button set = regiserWindow.findViewById( R.id.btn_set_timer );
        final FloatingActionButton del = regiserWindow.findViewById( R.id.fab_clear_start_timer2 );
        //final TextView tv_finisDate = regiserWindow.findViewById(R.id.tv_finishDate);

        Calendar calendar_start = startTimeCalendar.getInstance(); // creates a new calendar instance

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

        if(endTimeCalendar!=null){
            numbHour.setValue( endTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( endTimeCalendar.get(Calendar.MINUTE) );
            datePicker.updateDate( endTimeCalendar.get( Calendar.YEAR ),endTimeCalendar.get( Calendar.MONTH ) , endTimeCalendar.get( Calendar.DAY_OF_MONTH ));
        }
        else {
            nowUpdate();
            datePicker.updateDate( startTimeCalendar.get( Calendar.YEAR ),startTimeCalendar.get( Calendar.MONTH ),startTimeCalendar.get( Calendar.DAY_OF_MONTH ));
            numbHour.setValue( now.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( now.get( Calendar.MINUTE ) );
        }
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
        set.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                int hour = numbHour.getValue();
                int minute = numbMinute.getValue();

                endTimeCalendar = Calendar.getInstance();
                endTimeCalendar.set( Calendar.DAY_OF_MONTH,day );
                endTimeCalendar.set( Calendar.MONTH,month );
                endTimeCalendar.set( Calendar.YEAR,year );
                endTimeCalendar.set( Calendar.HOUR_OF_DAY,hour );
                endTimeCalendar.set( Calendar.MINUTE,minute );


                //tv_otrabotano_start_txt.setText( getResources().getString( R.string.otrabotano ) );


                if(startTimeCalendar.after( endTimeCalendar )){
                    AlertDialog.Builder builder = new AlertDialog.Builder( ForSmenaJobReview.this);
                    LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this);
                    final View regiserWindow = inflater.inflate(R.layout.alert_no_correct_date_set, null);
                    builder.setView(regiserWindow);

                    final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka );
                    shapka.setText( getResources().getString( R.string.Incorrect_time ) );
                    final TextView txt = regiserWindow.findViewById( R.id.tv_txt);
                    txt.setText( getResources().getString( R.string.no_correct_end_time ) );

                    final Button ok = regiserWindow.findViewById( R.id.btn_ok );
                    ok.setOnClickListener( v1 -> dialog.dismiss() );

                    final AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                    dialog.setCancelable( false );
                    dialog.show();

                    ok.setOnClickListener( v1 -> dialog.dismiss() );
                } else {
                    try {
                        t.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        handler.removeCallbacks(runnable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    finishTimeToFB = endTimeCalendar.getTime();
                    endTime = String.valueOf(finishTimeToFB);
                    initStartEnd();
                    chekStartEnd();
                    getSumma();
                    dialog.dismiss();

                }
            }
        } );
        del.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_overTime_layout.setVisibility( View.INVISIBLE );

                endTime = null;
                endTimeCalendar = null;

                tv_sec.setText( "0" );
                tv_min.setText( "0" );
                tv_hour.setText( "0" );
                tv_result.setText( "0" );
                result=0;
                overTimeSummaResult=0;




                try {
                    handler.removeCallbacks(runnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    t.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                chekStartEnd();
                initStartEnd();
                checkTimer();

                dialog.dismiss();
            }
        } );

    }

    private void pressStartButton() {
        nowUpdate();

        AlertDialog.Builder builder = new AlertDialog.Builder( ForSmenaJobReview.this );
        LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this );
        final View regiserWindow = inflater.inflate( R.layout.set_start_time_inflater, null );
        builder.setView( regiserWindow );

        final NumberPicker numbHour = regiserWindow.findViewById( R.id.np_HourSelect );
        final NumberPicker numbMinute = regiserWindow.findViewById( R.id.np_MinutSelect );
        final Button cancel = regiserWindow.findViewById( R.id.btn_cencel_timer );
        final Button set = regiserWindow.findViewById( R.id.btn_set_timer );
        final FloatingActionButton delete = regiserWindow.findViewById( R.id.fab_clear_start_timer );

        numbHour.setMinValue( 0 );
        numbHour.setMaxValue( 23 );
        numbHour.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );


        String mValues_minute[] = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43",
                "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        setNubmerPicker( numbMinute, mValues_minute );
        numbMinute.setMinValue( 0 );
        numbMinute.setMaxValue( 59 );
        numbMinute.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );

        if (startTimeCalendar != null) {
            numbHour.setValue( startTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( startTimeCalendar.get( Calendar.MINUTE ) );
        } else {
            // if startTimeCalendar null
            numbHour.setValue( now.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( now.get( Calendar.MINUTE ) );
        }
//
        setDividerColor( numbHour, Color.BLACK );
        setDividerColor( numbMinute, Color.BLACK );

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        dialog.show();

        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
        delete.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder( ForSmenaJobReview.this );
                LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this );
                final View regiserWindow = inflater.inflate( R.layout.template_is_present_info_inflater, null );

                final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka_name );
                final TextView txt = regiserWindow.findViewById( R.id.textView15 );
                final Button delete = regiserWindow.findViewById( R.id.btn_update );
                final Button cancel = regiserWindow.findViewById( R.id.btn_change_name_1 );

                shapka.setText( getResources().getString( R.string.Delete_start_time ) );
                txt.setText( getResources().getString( R.string.Delete_start_time_txt ) );
                delete.setText( getResources().getString( R.string.delete ) );
                delete.setBackgroundTintList( ContextCompat.getColorStateList( ForSmenaJobReview.this, R.color.red ) );
                cancel.setText( getResources().getString( R.string.cancel ) );

                builder.setView( regiserWindow );

                final AlertDialog dialog2 = builder.create();
                dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog2.setCancelable( false );
                dialog2.show();
                cancel.setOnClickListener( v1 -> dialog2.dismiss() );
                delete.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                                result=0;
//                                overTimeSummaResult = 0;
                        cl_overTime_layout.setVisibility( View.INVISIBLE );
                        startTime = null;
                        endTime = null;
                        result=0;
                        overTimeSummaResult=0;


                        initStartEnd();
                        chekStartEnd();
                        handler.removeCallbacks(runnable);

                        try {
                            t.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog2.dismiss();
                        dialog.dismiss();
                    }
                } );

            }
        } );
        set.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nowUpdate();
                try {
                    t.cancel();
                    handler.removeCallbacks(runnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tv_otrabotano_start_txt.setText( getResources().getString( R.string.left ) );
                Calendar dataNachalSobitiya = Calendar.getInstance();
                Date x = ConvertStringToDate( date ); // set date start job from intent
                dataNachalSobitiya.setTime( x );
                dataNachalSobitiya.set( Calendar.HOUR_OF_DAY, numbHour.getValue() );
                dataNachalSobitiya.set( Calendar.MINUTE, numbMinute.getValue() );
                dataNachalSobitiya.set( Calendar.SECOND, 1 );

                if (endTimeCalendar != null) {
                    tv_otrabotano_start_txt.setText( getResources().getString( R.string.otrabotano ) );
                    if (endTimeCalendar.before( dataNachalSobitiya )) {
                        // //System.out.println("ТАКОГО БЫТЬ НЕ МОЖЕТ КАЛЕНДАРЬ НАОБОРОТ");
                        AlertDialog.Builder builder = new AlertDialog.Builder( ForSmenaJobReview.this );
                        LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this );
                        final View regiserWindow = inflater.inflate( R.layout.alert_no_correct_date_set, null );
                        builder.setView( regiserWindow );
                        final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka );
                        shapka.setText( getResources().getString( R.string.Incorrect_time ) );
                        final TextView txt = regiserWindow.findViewById( R.id.tv_txt );
                        txt.setText( getResources().getString( R.string.no_correct_start_time ) );
                        final Button ok = regiserWindow.findViewById( R.id.btn_ok );
                        ok.setOnClickListener( v1 -> dialog.dismiss() );
                        final AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                        dialog.setCancelable( false );
                        dialog.show();
                        ok.setOnClickListener( v1 -> dialog.dismiss() );
                    } else {



                            startTimeToFB = dataNachalSobitiya.getTime();
                            startTime = String.valueOf( startTimeToFB );
                            initStartEnd();
                            chekStartEnd();
                            //getSumma();
                            dialog.dismiss();
                        }




                } else {
                        nowUpdate();

                    Calendar today = Calendar.getInstance();
                    today.set( Calendar.HOUR_OF_DAY, 0 );
                    today.set( Calendar.MINUTE, 0 );
                    today.set( Calendar.SECOND, 0 );
                    today.set( Calendar.MILLISECOND, 0 );

//
                    Calendar todayStartCheck = Calendar.getInstance();
                    Date x_ = ConvertStringToDate( date ); // set date start job from intent
                    todayStartCheck.setTime( x_ );
                    todayStartCheck.set( Calendar.HOUR_OF_DAY, 0 );
                    todayStartCheck.set( Calendar.MINUTE, 0 );
                    todayStartCheck.set( Calendar.SECOND, 0 );
                    todayStartCheck.set( Calendar.MILLISECOND, 0 );

                    if (todayStartCheck.equals( today )) {

                        if(dataNachalSobitiya.after( now )){
                             //System.out.println("FUTURE");
                            startTimeToFB = dataNachalSobitiya.getTime();
                            startTime = String.valueOf( startTimeToFB );
                            cl_overTime_layout.setVisibility( View.INVISIBLE );
                            tv_sec.setText( "0" );
                            tv_min.setText( "0" );
                            tv_hour.setText( "0" );
                            tv_result.setText( "0" );
                            //initStartEnd();
                            initStartEnd();
                            chekStartEnd();
                            dialog.dismiss();

                        } else {
                             //System.out.println("HERE");
                            startTimeToFB = dataNachalSobitiya.getTime();
                            startTime = String.valueOf( startTimeToFB );
                            initStartEnd();
                            chekStartEnd();
                            checkTimer();
                            getSumma();
                            dialog.dismiss();
                        }
                    }   else {


                        Calendar oldJob = Calendar.getInstance();
                        oldJob.setTime(x_);
                        oldJob.set( Calendar.HOUR_OF_DAY,0 );
                        oldJob.set( Calendar.MINUTE,0 );
                        oldJob.set( Calendar.SECOND,0 );
                        oldJob.set( Calendar.MILLISECOND,0 );

                        Calendar now_notime = Calendar.getInstance();
                        oldJob.set( Calendar.HOUR_OF_DAY,0 );
                        oldJob.set( Calendar.MINUTE,0 );
                        oldJob.set( Calendar.SECOND,0 );
                        oldJob.set( Calendar.MILLISECOND,0 );

                        if(oldJob.before( now_notime )){
                            startTimeToFB = dataNachalSobitiya.getTime();
                            startTime = String.valueOf( startTimeToFB );
                            initStartEnd();
                            String hr = String.valueOf( startTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
                            if (startTimeCalendar.get( Calendar.HOUR_OF_DAY ) < 10 || startTimeCalendar.get( Calendar.HOUR_OF_DAY ) == 0) {
                                hr = "0" + startTimeCalendar.get( Calendar.HOUR_OF_DAY );
                            }
                            String mnt = String.valueOf( startTimeCalendar.get( Calendar.MINUTE ) );
                            if (startTimeCalendar.get( Calendar.MINUTE ) < 10 || startTimeCalendar.get( Calendar.MINUTE ) == 0) {
                                mnt = "0" + startTimeCalendar.get( Calendar.MINUTE );
                            }
                            start.setText( hr + ":" + mnt );
                            stop.setText( "00:00" );
                            stop.setEnabled( true );

                            dialog.dismiss();
                        } else {


                        startTimeToFB = dataNachalSobitiya.getTime();
                        startTime = String.valueOf( startTimeToFB );
                        initStartEnd();
                        chekStartEnd();
                        dialog.dismiss();
                    }
                    }




                    }



                }




        } );


    }

    private void checkTimer() {
        nowUpdate();

        initStartEnd();

        if(startTimeCalendar!=null){
            Calendar check_now = Calendar.getInstance();
            check_now.set( Calendar.HOUR_OF_DAY,0 );
            check_now.set( Calendar.MINUTE,0 );
            check_now.set( Calendar.SECOND,0 );
            check_now.set( Calendar.MILLISECOND,0 );

            Calendar check_start = Calendar.getInstance();
            Date x = new Date(startTime);
            check_start.setTime(x);
            check_start.set( Calendar.HOUR_OF_DAY,0 );
            check_start.set( Calendar.MINUTE,0 );
            check_start.set( Calendar.SECOND,0 );
            check_start.set( Calendar.MILLISECOND,0 );

             //System.out.println(check_now.getTime());
             //System.out.println(check_start.getTime());

            if(endTimeCalendar!=null) {

//                long hours_sum = endTimeCalendar.getTimeInMillis()-startTimeCalendar.getTimeInMillis();
//                String h = String.format( "%02d", TimeUnit.MILLISECONDS.toHours( hours_sum ) );
//                String m = String.format( "%02d", TimeUnit.MILLISECONDS.toMinutes( hours_sum ) - TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( hours_sum ) ) );
//                String s = String.format( "%02d", TimeUnit.MILLISECONDS.toSeconds(hours_sum) -
//                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(hours_sum)) );
//
//

                getSumma();

                 //System.out.println("+++++++++++++++++++++++++++++++++");
            } else {


            if(check_now.after( check_start ) || check_now.before( check_start )){
                 //System.out.println("NO NEED TIMER");
                try {
                    t.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.removeCallbacks(runnable);
                tv_sec.setText( "0" );
                tv_min.setText( "0" );
                tv_hour.setText( "0" );
                tv_result.setText( "0" );


            } else {

                if(endTimeCalendar==null){
                     //System.out.println("NEED TIMER !!!");
                    nowUpdate();
                    try {
                        t.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Calendar todayStartCheck_ = Calendar.getInstance();
                    Date x_ = new Date( startTime ); // set date start job from intent
                    todayStartCheck_.setTime( x_ );

                    Calendar now = Calendar.getInstance();
                     //System.out.println("!!!!!" +todayStartCheck_.getTime() + "  " + now.getTime());

                    Calendar oldJob = Calendar.getInstance();
                    oldJob.setTime(x_);
                    oldJob.set( Calendar.HOUR_OF_DAY,0 );
                    oldJob.set( Calendar.MINUTE,0 );
                    oldJob.set( Calendar.SECOND,0 );
                    oldJob.set( Calendar.MILLISECOND,0 );

                    Calendar now_notime = Calendar.getInstance();
                    oldJob.set( Calendar.HOUR_OF_DAY,0 );
                    oldJob.set( Calendar.MINUTE,0 );
                    oldJob.set( Calendar.SECOND,0 );
                    oldJob.set( Calendar.MILLISECOND,0 );


                    if(todayStartCheck_.after(now) ){
                         //System.out.println("BEFORE !");

                        tv_sec.setText( "0" );
                        tv_min.setText( "0" );
                        tv_hour.setText( "0" );
                        tv_result.setText( "0" );
                        try {
                            t.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.removeCallbacks(runnable);

                    } else {
                         //System.out.println("ТУТ");

                        long dataStartMinusNow =
                                (startTimeCalendar.getTimeInMillis() + (Long.parseLong(et_duration_smena.getText().toString().trim())*60*60*1000))-now.getTimeInMillis();
                        startTimer( dataStartMinusNow );
                        getSumma();
                    }

//



                } else {
                     //System.out.println("NO NEED TIMER NEED SUMMA");
                    getSumma();
                }
        }}


        } else {
            nowUpdate();

        }

    }

    private void startTimer(long time) {

        long hours = time;
        handler.removeCallbacks(runnable);

        t = new CountDownTimer(hours,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                String h = String.format( "%02d", TimeUnit.MILLISECONDS.toHours( millisUntilFinished ) );
                String m = String.format( "%02d", TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished ) - TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( millisUntilFinished ) ) );
                String s = String.format( "%02d", TimeUnit.MILLISECONDS.toSeconds( millisUntilFinished ) -
                        TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished ) ) );

                tv_hour.setText( h );
                tv_min.setText( m );
                tv_sec.setText( s );
            }


            @Override
            public void onFinish() {
                 //System.out.println("Finish");
                nowUpdate();

                long smena_end = startTimeCalendar.getTimeInMillis()+((Long.parseLong( et_duration_smena.getText().toString().trim() ))*60*60*1000);
                long overTimeCountStartAt = now.getTimeInMillis() - smena_end;
                String h = String.format( "%02d", TimeUnit.MILLISECONDS.toHours( overTimeCountStartAt ) );
                String m = String.format( "%02d", TimeUnit.MILLISECONDS.toMinutes( overTimeCountStartAt ) - TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( overTimeCountStartAt ) ) );
                String s = String.format( "%02d", TimeUnit.MILLISECONDS.toSeconds(overTimeCountStartAt) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(overTimeCountStartAt)) );

                 //System.out.println(h + "HOUR  "+ m + "  MIN");
                i3 = Integer.parseInt(h);
                i2 = Integer.parseInt( m );
                i= Integer.parseInt( s );
                tv_otrabotano_start_txt.setText( getResources().getString( R.string.overtime ) );
                result = Float.parseFloat(et_price.getText().toString().trim());
                tv_result.setText(et_price.getText().toString().trim()) ;
                startCounterOvertime();

            }
        }.start();
    }

    private void startCounterOvertime() {

        runnable = new Runnable() {

            @Override
            public void run() {
                try {

                    handler.postDelayed(this, 1000);
                    if(i==60){
                        i=0;
                        tv_sec.setText( String.valueOf( i ) );
                        i2=i2+1;
                        //result = Integer.valueOf(priceHour)*i3+priceMinuta;

                        if(i2==60){
                            i2=0;
                            tv_min.setText( String.valueOf( i2 ) );

                            i3 = i3+1;
                            tv_hour.setText( String.valueOf(i3 ));

                        }}

                    tv_sec.setText( String.valueOf( i ) );
                    i++;

                    tv_min.setText( String.valueOf( i2 ) );
                    tv_hour.setText(  String.valueOf( i3 ) );

                    getOvertimeSumma( String.valueOf( i3 ),String.valueOf( i2 ) );
                    result = overTimeSummaResult+Float.parseFloat( et_price.getText().toString().trim() );
                    String formattedDouble = String.format("%.2f", result);
                    tv_result.setText( String.valueOf( formattedDouble ) );


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private void getSumma() {
        cl_overTime_layout.setVisibility( View.INVISIBLE );

        if (startTimeCalendar != null && endTimeCalendar == null) {

            nowUpdate();
            Calendar now = Calendar.getInstance();
            long finishTime = now.getTimeInMillis() - startTimeCalendar.getTimeInMillis();
            String h = String.format( "%02d", TimeUnit.MILLISECONDS.toHours( finishTime ) );

            if( Long.parseLong( h )<=0){
                 //System.out.println("Это будущее????");
            }else{
                if(sw_half_hour.isChecked()){


                    hour_final = Long.parseLong( h );
                     //System.out.println("CHECKED + "+ hour_final+ "   "+ half_shift_hours);

                    if (hour_final>=Long.parseLong( et_half_shiht.getText().toString().trim())){
                        float half_shift = Float.valueOf( et_price.getText().toString().trim() )/2;
                        tv_result.setText(String.valueOf( half_shift ) );
                    }
                }
            }



        }

        else if (startTimeCalendar != null && endTimeCalendar != null) {

            long finishTime = endTimeCalendar.getTimeInMillis() - startTimeCalendar.getTimeInMillis();
            String h = String.format( "%02d", TimeUnit.MILLISECONDS.toHours( finishTime ) );
            String m = String.format( "%02d", TimeUnit.MILLISECONDS.toMinutes( finishTime ) - TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( finishTime ) ) );
             //System.out.println( h + "HOUR  " + m + "  MIN" );

            long roundedMinutes = roundetOnlyMiutes( Long.parseLong( m ) );
             //System.out.println( roundedMinutes + " ROUNDED TO " );



            if (roundedMinutes == 60) {
                hour_final = Long.parseLong( h )+1;
                minut_final = 0;
                 //System.out.println( hour_final + " HOUR FINAL+ 1 HOUR,  BUT WAS " + h );

            } else {
                hour_final = Long.parseLong( h );
                minut_final = roundedMinutes;
                 //System.out.println( "ROUNDED TO  " + roundedMinutes );

            }

            long durationInMsecond_ = Long.parseLong( et_duration_smena.getText().toString().trim() ) * 60 * 60 * 1000;
            //long allTimeFinal = (hour_final * 60 * 60 * 1000) + (minut_final * 60 * 1000);
            long allTimeFinal = (hour_final * 60 * 60 * 1000) + (minut_final * 60 * 1000);

             //System.out.println(hour_final+ "   "+ Long.parseLong( et_duration_smena.getText().toString().trim()  ));


            if (hour_final >= Long.parseLong( et_duration_smena.getText().toString().trim() )) {
                 //System.out.println( "БОЛЬШЕ, БУДЕТ ОВЕРТАЙМ" );

                howManyovertimeHourMinutes = allTimeFinal - durationInMsecond_;

                String h_over = String.format( "%02d", TimeUnit.MILLISECONDS.toHours( howManyovertimeHourMinutes ) );
                String m_over = String.format( "%02d", TimeUnit.MILLISECONDS.toMinutes( howManyovertimeHourMinutes ) - TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( howManyovertimeHourMinutes ) ) );

                 //System.out.println( h_over + "  " + m_over + "  OVERTIME" );
                tv_hour.setText( h );
                tv_min.setText( m );
                //
                 //System.out.println(h_over + "  " + m_over + " OVERTTYYYYYY");
                getOvertimeSumma( h_over, m_over );

                cl_overTime_layout.setVisibility( View.VISIBLE );
                tv_overTime_summa.setText( String.valueOf( overTimeSummaResult ) );
                tv_overTime_hours.setText( h_over );
                tv_overTime_minutes.setText( m_over );
                result = overTimeSummaResult + Float.parseFloat( et_price.getText().toString().trim() );

                String formattedDouble = String.format("%.2f", result);
                tv_result.setText( String.valueOf( formattedDouble ) );


            } else {
                tv_hour.setText( h );
                tv_min.setText( m );
                 //System.out.println( "МЕНЬШЕ, НЕТ ОВЕРТАЙМА" );
                if (sw_half_hour.isChecked()) {

                    if (hour_final < Long.parseLong( et_half_shiht.getText().toString().trim() )) {
                         //System.out.println( "ПОЛ СМЕНЫ" );
                        result = Float.parseFloat( et_price.getText().toString().trim() ) / 2;
                        tv_result.setText( String.valueOf( result ) );

                    } else {
                        result = Float.parseFloat( et_price.getText().toString().trim() );
                        tv_result.setText( String.valueOf( result ) );
                    }
                } else {

                    result = Float.parseFloat( et_price.getText().toString().trim() );
                    tv_result.setText( String.valueOf( result ) );

                }
                // ТУТ БУДЕТ СУММА
            }
        }
    }

    private void nowUpdate() {
        now = Calendar.getInstance();
    }

    private void initStartEnd() {

        try {
            Date start = new Date( startTime );
            startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.setTime( start );
        } catch (Exception e) {
            e.printStackTrace();
            startTimeCalendar = null;
        }

        try {
            Date end = new Date( endTime );
            endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.setTime( end );
        } catch (Exception e) {
            e.printStackTrace();
            endTimeCalendar = null;
        }

    }

    private void initData() {

        tv_name.setText( name );
        tv_date.setText( date );
        et_price.setText( priceShift );
        et_okruglenie.setText( rounded_minutes );
        tv_description.setText( description );


        int valutaIndex = 0;
        switch (valuta) {
            case "грн":
                valutaIndex = 0;
                break;
            case "usd":
                valutaIndex = 1;
                break;
            case "eur":
                valutaIndex = 2;
                break;
            case "руб":
                valutaIndex = 3;
                break;
        }
        sp_valuta.setSelection( valutaIndex );
        sw_status.setChecked( Boolean.valueOf( status ) );
        et_duration_smena.setText( duration_smena );
        et_overtime_procent.setText( overtime_procent );

        Boolean bool_half_shift = Boolean.valueOf( half_shift );
        if (bool_half_shift == true) {
            sw_half_hour.setChecked( true );
            et_half_shiht.setText( half_shift_hours );
        } else {
            sw_half_hour.setChecked( false );
            et_half_shiht.setEnabled( false );
            et_half_shiht.clearComposingText();
        }


    }

    private void alarmCheck(String d) {

        if (d == null) {
            tv_alarm.setText( getString( R.string.AlarmNoSet ) );
            fabsetAlarm.setImageDrawable( getDrawable( R.drawable.alarm_off ) );

        } else {
            String t;
            Date date_test = new Date();
            Date x = new Date( d );
            if (x.before( date_test )) {
                tv_alarm.setText( getString( R.string.AlarmNoSet ) );
//                        alarmSet.setImageDrawable( getDrawable( R.drawable.alarm_off ) );
                fabsetAlarm.setImageDrawable( getDrawable( R.drawable.alarm_off ) );

            } else {
//                        alarmSet.setImageDrawable( getDrawable( R.drawable.alarmsetico ) );
                fabsetAlarm.setImageDrawable( getDrawable( R.drawable.alarmsetico ) );
                Calendar cal = Calendar.getInstance();
                cal.setTime( x );
                String month_edit;
                int day = cal.get( Calendar.DAY_OF_MONTH );
                int month = (cal.get( Calendar.MONTH ));
                int year = cal.get( Calendar.YEAR );
                int h_ = cal.get( Calendar.HOUR_OF_DAY );
                int m_ = cal.get( Calendar.MINUTE );

                if (month < 10) {
                    month_edit = "0" + (month + 1);
                } else {
                    month_edit = String.valueOf( (month + 1) );
                }
                if (m_ < 10) {
                    t = String.valueOf( h_ + ":" + 0 + m_ );
                    t_piblish = day + "-" + (month_edit) + "-" + year + "  " + t;
                } else {
                    t = h_ + ":" + m_;
                    t_piblish = day + "-" + (month_edit) + "-" + year + "  " + t;
                }
                tv_alarm.setText( t_piblish );
            }
        }


    }

    private void setAlarm() {

        AlertDialog.Builder builder = new AlertDialog.Builder( ForSmenaJobReview.this );
        LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this );
        final View regiserWindow = inflater.inflate( R.layout.my_year_month_date_infater, null );
        builder.setView( regiserWindow );

        final NumberPicker numbDay = regiserWindow.findViewById( R.id.np_DateSelect );
        final NumberPicker numbMonth = regiserWindow.findViewById( R.id.np_MonthSelect );
        final NumberPicker numbYear = regiserWindow.findViewById( R.id.np_YearSelect );

        final NumberPicker numbHour = regiserWindow.findViewById( R.id.np_HourSelect );
        final NumberPicker numbMinute = regiserWindow.findViewById( R.id.np_MinutSelect );

        final Button setReminder = regiserWindow.findViewById( R.id.btn_set_reminder );
        //final Button exit = regiserWindow.findViewById( R.id.btnCancel );
        final Button cancel = regiserWindow.findViewById( R.id.btn_cencel_reminder );

        numbDay.setMinValue( 1 );
        numbDay.setMaxValue( 31 );
        numbDay.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
        numbDay.setValue( Calendar.getInstance().get( Calendar.DAY_OF_MONTH ) );
        numbDay.setWrapSelectorWheel( true );

        String mValues[] = {getString( R.string.January ), getString( R.string.February ), getString( R.string.March ),
                getString( R.string.April ), getString( R.string.May ), getString( R.string.June ), getString( R.string.July ),
                getString( R.string.August ), getString( R.string.September ), getString( R.string.October ), getString( R.string.November ),
                getString( R.string.December )};
        setNubmerPicker( numbMonth, mValues );
        numbMonth.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
        numbMonth.setValue( Calendar.getInstance().get( Calendar.MONTH ) );

        numbYear.setMinValue( 2010 );
        numbYear.setMaxValue( 2030 );
        numbYear.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
        numbYear.setValue( Calendar.getInstance().get( Calendar.YEAR ) );

        numbHour.setMinValue( 0 );
        numbHour.setMaxValue( 23 );
        numbHour.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
        numbHour.setValue( Calendar.getInstance().get( Calendar.HOUR_OF_DAY ) );

        String mValues_minute[] = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43",
                "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        setNubmerPicker( numbMinute, mValues_minute );
        numbMinute.setMinValue( 0 );
        numbMinute.setMaxValue( 59 );
        numbMinute.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
        numbMinute.setValue( Calendar.getInstance().get( Calendar.MINUTE ) );

        setDividerColor( numbMonth, Color.BLACK );
        setDividerColor( numbYear, Color.BLACK );
        setDividerColor( numbDay, Color.BLACK );
        setDividerColor( numbHour, Color.BLACK );
        setDividerColor( numbMinute, Color.BLACK );

        if (alarm == null) {

            Calendar c = Calendar.getInstance();
            numbDay.setValue( c.get( Calendar.DAY_OF_MONTH ) );
            numbMonth.setValue( c.get( Calendar.MONTH ) );
            numbYear.setValue( c.get( Calendar.YEAR ) );
            numbHour.setValue( c.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( c.get( Calendar.MINUTE ) );
        } else {

            Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
            Date x = new Date( alarm );
            calendar.setTime( x );   // assigns calendar to given date
            numbDay.setValue( calendar.get( Calendar.DAY_OF_MONTH ) );
            numbMonth.setValue( calendar.get( Calendar.MONTH ) );
            numbYear.setValue( calendar.get( Calendar.YEAR ) );
            numbHour.setValue( calendar.get( Calendar.HOUR_OF_DAY ) );
            numbMinute.setValue( calendar.get( Calendar.MINUTE ) );
        }

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        dialog.show();

        numbYear.setOnValueChangedListener( new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                int year = Integer.valueOf( numbYear.getValue() );
                int month = Integer.valueOf( numbMonth.getValue() );
                Calendar calendarD = new GregorianCalendar( year, month, 1 );
                int noOfDaysOfMonth = calendarD.getActualMaximum( Calendar.DAY_OF_MONTH );
                numbDay.setMaxValue( noOfDaysOfMonth );
            }
        } );

        numbMonth.setOnValueChangedListener( new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {

                int year = Integer.valueOf( numbYear.getValue() );
                int month = Integer.valueOf( numbMonth.getValue() );
                Calendar calendarD = new GregorianCalendar( year, month, 1 );
                int noOfDaysOfMonth = calendarD.getActualMaximum( Calendar.DAY_OF_MONTH );
                numbDay.setMaxValue( noOfDaysOfMonth );

            }
        } );


        setReminder.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendarNow = Calendar.getInstance();

                Calendar c1 = Calendar.getInstance();
                c1.set( Calendar.YEAR, numbYear.getValue() );
                c1.set( Calendar.MONTH, numbMonth.getValue() );
                c1.set( Calendar.DAY_OF_MONTH, numbDay.getValue() );
                c1.set( Calendar.HOUR_OF_DAY, numbHour.getValue() );
                c1.set( Calendar.MINUTE, numbMinute.getValue() );
                c1.set( Calendar.SECOND, 1 );
                Date alarm1DataToFB = c1.getTime();

                if(c1.before( calendarNow )){
                    Toast.makeText( getApplicationContext(), getResources().getString( R.string.wrong_time_alarm ), Toast.LENGTH_SHORT ).show();
                }
                else {
                    noteRef_addWork_Full.document( docName ).update( "alarm1", alarm1DataToFB );

                    alarmManager = (AlarmManager) getBaseContext().getSystemService( ALARM_SERVICE );
                    Intent my_intent = new Intent( ForSmenaJobReview.this, AlarmResiver.class );
                    my_intent.putExtra( "jobId", docName );
                    pendingIntent = pendingIntent.getBroadcast( ForSmenaJobReview.this, Integer.valueOf( uid ), my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
                    alarmManager.set( AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent );
                    alarm = String.valueOf( alarm1DataToFB );

                    alarmCheck( alarm );
                    dialog.dismiss();
                }


            }
        } );

        //exit.setOnClickListener( v -> dialog.dismiss() );

        Locale locale = Resources.getSystem().getConfiguration().locale;

        cancel.setOnClickListener( v1 -> {
            noteRef_addWork_Full.document( docName ).update( "alarm1", null );

            alarm = null;
            alarmManager = (AlarmManager) getBaseContext().getSystemService( ALARM_SERVICE );
            Intent my_intent = new Intent( ForSmenaJobReview.this, AlarmResiver.class );
            my_intent.putExtra( "jobId", docName );

            pendingIntent = pendingIntent.getBroadcast( ForSmenaJobReview.this, Integer.valueOf( uid ), my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
            alarmManager.cancel( pendingIntent );
            Toast.makeText( ForSmenaJobReview.this, getString( R.string.AlarmCancel ), Toast.LENGTH_SHORT ).show();

            alarmCheck( alarm );
            dialog.dismiss();
        } );
    }

    private void setNubmerPicker(NumberPicker nubmerPicker, String[] numbers) {
        nubmerPicker.setMaxValue( numbers.length - 1 );
        nubmerPicker.setMinValue( 0 );
        nubmerPicker.setWrapSelectorWheel( true );
        nubmerPicker.setDisplayedValues( numbers );
    }

    private void setDividerColor(android.widget.NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = android.widget.NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals( "mSelectionDivider" )) {
                pf.setAccessible( true );
                try {
                    ColorDrawable colorDrawable = new ColorDrawable( color );
                    pf.set( picker, colorDrawable );
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private Date ConvertStringToDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat( "ddd-MM-yyy", Locale.getDefault() );
        Date date = null;
        try {
            date = format.parse( eventDate );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void saveInfo() {
        try {
            t.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (et_price.getText().toString().trim().isEmpty()  ) {
            et_price.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );

        } else {

            if (sw_half_hour.isChecked()){
                 //System.out.println(et_duration_smena.getText().toString().trim() + "  ДЛИТЕЛЬНОСТЬ СМЕНЫ");
                if (et_half_shiht.getText().toString().trim().isEmpty() || et_half_shiht.getText().toString().trim().equals( "" )){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( ForSmenaJobReview.this );
                    LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this );
                    final View regiserWindow = inflater.inflate( R.layout.alert_no_correct_date_set, null );
                    builder.setView( regiserWindow );
                    final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka );
                    shapka.setText( getResources().getString( R.string.Duration_of_half_shift_is_incorrect ) );
                    final TextView txt = regiserWindow.findViewById( R.id.tv_txt );
                    txt.setText( getResources().getString( R.string.Half_shift_is_null ) );
                    final Button ok = regiserWindow.findViewById( R.id.btn_ok );

                    final androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                    dialog.setCancelable( false );
                    dialog.show();
                    ok.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    } );
                } else {

                int half = Integer.parseInt( et_half_shiht.getText().toString().trim());
                int durationSmena = Integer.parseInt( et_duration_smena.getText().toString().trim() );
                if (half>durationSmena) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( ForSmenaJobReview.this );
                    LayoutInflater inflater = LayoutInflater.from( ForSmenaJobReview.this );
                    final View regiserWindow = inflater.inflate( R.layout.alert_no_correct_date_set, null );
                    builder.setView( regiserWindow );
                    final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka );
                    shapka.setText( getResources().getString( R.string.Duration_of_half_shift_is_incorrect ) );
                    final TextView txt = regiserWindow.findViewById( R.id.tv_txt );
                    txt.setText( getResources().getString( R.string.Duration_half_shift_txt ) );
                    final Button ok = regiserWindow.findViewById( R.id.btn_ok );

                    final androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                    dialog.setCancelable( false );
                    dialog.show();
                    ok.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    } );
                    } else {
                    if (startTimeCalendar != null) {
                        Date s = new Date( startTime );
                        noteRef_addWork_Full.document( docName ).update( "start", s );


                        if (endTimeCalendar != null) {
                            Date f = new Date( endTime );
                            noteRef_addWork_Full.document( docName ).update( "end", f );


                            getSumma();
                            noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", result  );//
                            // TODO: 14.04.2020

                        } else {
                            noteRef_addWork_Full.document( docName ).update( "end", null );
                        }
                        noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", result );
                        noteRef_addWork_Full.document( docName ).update( "zarabotanoOvertimeFinal", overTimeSummaResult );
                        noteRef_addWork_Full.document( docName ).update( "price_smena", Integer.valueOf( et_price.getText().toString() ) );
                        noteRef_addWork_Full.document( docName ).update( "valuta", sp_valuta.getSelectedItem().toString() );
                        noteRef_addWork_Full.document( docName ).update( "status", sw_status.isChecked() );
                        noteRef_addWork_Full.document( docName ).update( "discription", tv_description.getText().toString() );
                        if (et_okruglenie.getText().toString().isEmpty() || Integer.valueOf( et_okruglenie.getText().toString().trim() ) > 59) {
                            noteRef_addWork_Full.document( docName ).update( "rounded_minut", 15 );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "rounded_minut", Integer.valueOf( et_okruglenie.getText().toString().trim() ) );
                        }
                        if (et_duration_smena.getText().toString().isEmpty()) {
                            noteRef_addWork_Full.document( docName ).update( "smena_duration", 0 );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "smena_duration", Integer.valueOf( et_duration_smena.getText().toString() ) );
                        }
                        if (et_overtime_procent.getText().toString().isEmpty()) {
                            noteRef_addWork_Full.document( docName ).update( "overtime_pocent", 0 );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "overtime_pocent", Integer.valueOf( et_overtime_procent.getText().toString() ) );
                        }
                        if (sw_half_hour.isChecked()) {
                            noteRef_addWork_Full.document( docName ).update( "half_shift", true );
                            noteRef_addWork_Full.document( docName ).update( "half_shift_hours", Integer.valueOf( et_half_shiht.getText().toString().trim() ) );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "half_shift", false );
                        }
                        finish();
                    } else {
                        // если старт тайм == null
                        noteRef_addWork_Full.document( docName ).update( "start", null );
                        noteRef_addWork_Full.document( docName ).update( "end", null );
                        noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", result );
                        noteRef_addWork_Full.document( docName ).update( "zarabotanoOvertimeFinal", overTimeSummaResult );
                        noteRef_addWork_Full.document( docName ).update( "price_smena", Integer.valueOf( et_price.getText().toString() ) );
                        noteRef_addWork_Full.document( docName ).update( "valuta", sp_valuta.getSelectedItem().toString() );
                        noteRef_addWork_Full.document( docName ).update( "status", sw_status.isChecked() );
                        noteRef_addWork_Full.document( docName ).update( "discription", tv_description.getText().toString() );
                        if (et_okruglenie.getText().toString().isEmpty() || Integer.valueOf( et_okruglenie.getText().toString().trim() ) > 59) {
                            noteRef_addWork_Full.document( docName ).update( "rounded_minut", 15 );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "rounded_minut", Integer.valueOf( et_okruglenie.getText().toString().trim() ) );
                        }
                        if (et_duration_smena.getText().toString().isEmpty()) {
                            noteRef_addWork_Full.document( docName ).update( "smena_duration", 0 );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "smena_duration", Integer.parseInt( et_duration_smena.getText().toString() ) );
                        }
                        if (et_overtime_procent.getText().toString().isEmpty()) {
                            noteRef_addWork_Full.document( docName ).update( "overtime_pocent", 0 );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "overtime_pocent", Integer.parseInt( et_overtime_procent.getText().toString().trim() ) );
                        }
                        if (sw_half_hour.isChecked()) {
                            noteRef_addWork_Full.document( docName ).update( "half_shift", true );
                            noteRef_addWork_Full.document( docName ).update( "half_shift_hours", Integer.parseInt( et_half_shiht.getText().toString().trim() ) );
                        } else {
                            noteRef_addWork_Full.document( docName ).update( "half_shift", false );
                        }
                        finish();
                }
                }
                }
            }

            else {
                if (startTimeCalendar != null) {
                    Date s = new Date( startTime );
                    noteRef_addWork_Full.document( docName ).update( "start", s );


                    if (endTimeCalendar != null) {
                        Date f = new Date( endTime );
                        noteRef_addWork_Full.document( docName ).update( "end", f );


                        getSumma();
                        noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", result  );//
                        // TODO: 14.04.2020

                    } else {
                        noteRef_addWork_Full.document( docName ).update( "end", null );
                    }
                    noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", result );
                    noteRef_addWork_Full.document( docName ).update( "zarabotanoOvertimeFinal", overTimeSummaResult );
                    noteRef_addWork_Full.document( docName ).update( "price_smena", Integer.valueOf( et_price.getText().toString() ) );
                    noteRef_addWork_Full.document( docName ).update( "valuta", sp_valuta.getSelectedItem().toString() );
                    noteRef_addWork_Full.document( docName ).update( "status", sw_status.isChecked() );
                    noteRef_addWork_Full.document( docName ).update( "discription", tv_description.getText().toString() );
                    if (et_okruglenie.getText().toString().isEmpty() || Integer.valueOf( et_okruglenie.getText().toString().trim() ) > 59) {
                        noteRef_addWork_Full.document( docName ).update( "rounded_minut", 15 );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "rounded_minut", Integer.valueOf( et_okruglenie.getText().toString().trim() ) );
                    }
                    if (et_duration_smena.getText().toString().isEmpty()) {
                        noteRef_addWork_Full.document( docName ).update( "smena_duration", 0 );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "smena_duration", Integer.valueOf( et_duration_smena.getText().toString() ) );
                    }
                    if (et_overtime_procent.getText().toString().isEmpty()) {
                        noteRef_addWork_Full.document( docName ).update( "overtime_pocent", 0 );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "overtime_pocent", Integer.valueOf( et_overtime_procent.getText().toString() ) );
                    }
                    if (sw_half_hour.isChecked()) {
                        noteRef_addWork_Full.document( docName ).update( "half_shift", true );
                        noteRef_addWork_Full.document( docName ).update( "half_shift_hours", Integer.valueOf( et_half_shiht.getText().toString().trim() ) );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "half_shift", false );
                    }
                    finish();
                } else {
                    // если старт тайм == null
                    noteRef_addWork_Full.document( docName ).update( "start", null );
                    noteRef_addWork_Full.document( docName ).update( "end", null );
                    noteRef_addWork_Full.document( docName ).update( "zarabotanoFinal", result );
                    noteRef_addWork_Full.document( docName ).update( "zarabotanoOvertimeFinal", overTimeSummaResult );
                    noteRef_addWork_Full.document( docName ).update( "price_smena", Integer.valueOf( et_price.getText().toString() ) );
                    noteRef_addWork_Full.document( docName ).update( "valuta", sp_valuta.getSelectedItem().toString() );
                    noteRef_addWork_Full.document( docName ).update( "status", sw_status.isChecked() );
                    noteRef_addWork_Full.document( docName ).update( "discription", tv_description.getText().toString() );
                    if (et_okruglenie.getText().toString().isEmpty() || Integer.valueOf( et_okruglenie.getText().toString().trim() ) > 59) {
                        noteRef_addWork_Full.document( docName ).update( "rounded_minut", 15 );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "rounded_minut", Integer.valueOf( et_okruglenie.getText().toString().trim() ) );
                    }
                    if (et_duration_smena.getText().toString().isEmpty()) {
                        noteRef_addWork_Full.document( docName ).update( "smena_duration", 0 );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "smena_duration", Integer.parseInt( et_duration_smena.getText().toString() ) );
                    }
                    if (et_overtime_procent.getText().toString().isEmpty()) {
                        noteRef_addWork_Full.document( docName ).update( "overtime_pocent", 0 );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "overtime_pocent", Integer.parseInt( et_overtime_procent.getText().toString().trim() ) );
                    }
                    if (sw_half_hour.isChecked()) {
                        noteRef_addWork_Full.document( docName ).update( "half_shift", true );
                        noteRef_addWork_Full.document( docName ).update( "half_shift_hours", Integer.parseInt( et_half_shiht.getText().toString().trim() ) );
                    } else {
                        noteRef_addWork_Full.document( docName ).update( "half_shift", false );
                    }
                    finish();
                }

            }

            }


    }

    private void chekStartEnd() {

        if (startTimeCalendar == null) {

             //System.out.println( "ВСЕ НУЛ" );
            int set = 1;
            setButtons( set );
        }

        if (startTimeCalendar != null) {

            if (endTimeCalendar == null) {
                 //System.out.println( "ЕНД НУЛ" );
                int set = 2;
                setButtons( set );

            }
            if (endTimeCalendar != null) {
                 //System.out.println( "ВСЕ НЕ НУЛ" );
                int set = 3;
                setButtons( set );

            }

        }

    }

    private void setButtons(int set) {
        nowUpdate();
        switch (set) {
            case 1:
                start.setText( getString( R.string.Start ) );
                stop.setText( "00:00" );
                stop.setEnabled( false );
                cl_overTime_layout.setVisibility( View.INVISIBLE );
                tv_sec.setText( "0" );
                tv_min.setText( "0" );
                tv_hour.setText( "0" );
                tv_result.setText( "0" );

                break;
            case 2:

                //initStartEnd();
                Calendar today = Calendar.getInstance();
                today.set( Calendar.HOUR_OF_DAY, 0 );
                today.set( Calendar.MINUTE, 0 );
                today.set( Calendar.SECOND, 0 );
                today.set( Calendar.MILLISECOND, 0 );

//
                Calendar todayStartCheck = startTimeCalendar.getInstance();
                todayStartCheck.set( Calendar.HOUR_OF_DAY, 0 );
                todayStartCheck.set( Calendar.MINUTE, 0 );
                todayStartCheck.set( Calendar.SECOND, 0 );
                todayStartCheck.set( Calendar.MILLISECOND, 0 );


                 //System.out.println(todayStartCheck.getTime() + "  "+ today.getTime());

                if (todayStartCheck.before( today ) || todayStartCheck.after( today )) {
                    String hr = String.valueOf( startTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
                    if (startTimeCalendar.get( Calendar.HOUR_OF_DAY ) < 10 || startTimeCalendar.get( Calendar.HOUR_OF_DAY ) == 0) {
                        hr = "0" + startTimeCalendar.get( Calendar.HOUR_OF_DAY );
                    }
                    String mnt = String.valueOf( startTimeCalendar.get( Calendar.MINUTE ) );
                    if (startTimeCalendar.get( Calendar.MINUTE ) < 10 || startTimeCalendar.get( Calendar.MINUTE ) == 0) {
                        mnt = "0" + startTimeCalendar.get( Calendar.MINUTE );
                    }
                    start.setText( hr + ":" + mnt );
                    stop.setText( "00:00" );
                    stop.setEnabled( true );




                }
                if (todayStartCheck.equals( today )) {
                     //System.out.println("TODAY");
                    nowUpdate();
                    Calendar todayStartCheck_ = Calendar.getInstance();
                    Date x_ = new Date( startTime ); // set date start job from intent
                    todayStartCheck_.setTime( x_ );

                    Calendar now = Calendar.getInstance();

                     //System.out.println("!!!!!" +todayStartCheck_.getTime() + "  " + now.getTime());
                    if(todayStartCheck_.before(now)){
                        String hr = String.valueOf( startTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
                        if (startTimeCalendar.get( Calendar.HOUR_OF_DAY ) < 10 || startTimeCalendar.get( Calendar.HOUR_OF_DAY ) == 0) {
                            hr = "0" + startTimeCalendar.get( Calendar.HOUR_OF_DAY );
                        }
                        String mnt = String.valueOf( startTimeCalendar.get( Calendar.MINUTE ) );
                        if (startTimeCalendar.get( Calendar.MINUTE ) < 10 || startTimeCalendar.get( Calendar.MINUTE ) == 0) {
                            mnt = "0" + startTimeCalendar.get( Calendar.MINUTE );
                        }
                        start.setText( hr + ":" + mnt );
                        stop.setEnabled( true );
                        stop.setText( "00:00" );
                        tv_result.setText( "0" );
                        long dataStartMinusNow =
                                (startTimeCalendar.getTimeInMillis() + (Long.parseLong( et_duration_smena.getText().toString().trim() ) * 60 * 60 * 1000)) - now.getTimeInMillis();

                        startTimer(dataStartMinusNow);
                    }else {
                        String hr = String.valueOf( startTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
                        if (startTimeCalendar.get( Calendar.HOUR_OF_DAY ) < 10 || startTimeCalendar.get( Calendar.HOUR_OF_DAY ) == 0) {
                            hr = "0" + startTimeCalendar.get( Calendar.HOUR_OF_DAY );
                        }
                        String mnt = String.valueOf( startTimeCalendar.get( Calendar.MINUTE ) );
                        if (startTimeCalendar.get( Calendar.MINUTE ) < 10 || startTimeCalendar.get( Calendar.MINUTE ) == 0) {
                            mnt = "0" + startTimeCalendar.get( Calendar.MINUTE );
                        }
                        start.setText( hr + ":" + mnt );
                        stop.setEnabled( true );
                        stop.setText( "00:00" );
                        tv_result.setText( "0" );
                    }



                } else {

                    tv_sec.setText( "0" );
                    tv_min.setText( "0" );
                    tv_hour.setText( "0" );
                    tv_result.setText( "0" );

                }

                break;
            case 3:
                tv_otrabotano_start_txt.setText( getResources().getString( R.string.otrabotano ) );
                //setFinishButtonTimeCorrect();

                String hr_s = String.valueOf( startTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
                if (startTimeCalendar.get( Calendar.HOUR_OF_DAY ) < 10 || startTimeCalendar.get( Calendar.HOUR_OF_DAY ) == 0) {
                    hr_s = "0" + startTimeCalendar.get( Calendar.HOUR_OF_DAY );
                }
                String mnt_s = String.valueOf( startTimeCalendar.get( Calendar.MINUTE ) );
                if (startTimeCalendar.get( Calendar.MINUTE ) < 10 || startTimeCalendar.get( Calendar.MINUTE ) == 0) {
                    mnt_s = "0" + startTimeCalendar.get( Calendar.MINUTE );
                }
                start.setText( hr_s + ":" + mnt_s );

                String hr = String.valueOf( endTimeCalendar.get( Calendar.HOUR_OF_DAY ) );
                if (endTimeCalendar.get( Calendar.HOUR_OF_DAY ) < 10 || endTimeCalendar.get( Calendar.HOUR_OF_DAY ) == 0) {
                    hr = "0" + endTimeCalendar.get( Calendar.HOUR_OF_DAY );
                }
                String mnt = String.valueOf( endTimeCalendar.get( Calendar.MINUTE ) );
                if (endTimeCalendar.get( Calendar.MINUTE ) < 10 || endTimeCalendar.get( Calendar.MINUTE ) == 0) {
                    mnt = "0" + endTimeCalendar.get( Calendar.MINUTE );
                }
                int test = (int) endTimeCalendar.get( Calendar.MONTH ) + 1;
                String correctMonth = String.valueOf( test );
                if (test < 10) {
                    correctMonth = "0" + test;
                }
                String correctDayOfMonth = String.valueOf( endTimeCalendar.get( Calendar.DAY_OF_MONTH ) );
                if (endTimeCalendar.get( Calendar.DAY_OF_MONTH ) < 10) {
                    correctDayOfMonth = "0" + String.valueOf( endTimeCalendar.get( Calendar.DAY_OF_MONTH ) );

                }

                String date_to_button = correctDayOfMonth + "-" + correctMonth + "-" + endTimeCalendar.get( Calendar.YEAR );
                String time_to_button = hr + ":" + mnt;
                //start.setText( hr+ ":" +  mnt);
                stop.setText( date_to_button + "\n" + time_to_button );
                getSumma();



                break;
            default:
                break;
        }
    }

    private long roundetOnlyMiutes(long m) {


        long min = 0;
        long minutes = m;
        int roundTo = Integer.parseInt( et_okruglenie.getText().toString().trim() );
        if (roundTo == 0) {
            min = 60;
        }
        else if (roundTo == 1){
            min = m;

        } else {
            long sec_ostatok = minutes * 60;
            long sec_rounded = Long.valueOf( roundTo ) * 60;
            long halh_rounded = sec_rounded / 2;
            double polnih_rounded = Math.floor( sec_ostatok / sec_rounded );
            double ostatok_sec_no_polnih_raz = sec_ostatok - (polnih_rounded * sec_rounded);
            int full_raz_rounded = 0;
            if (ostatok_sec_no_polnih_raz >= halh_rounded) {
                full_raz_rounded++;

            }
            long res_raz = (long) polnih_rounded + full_raz_rounded;
            min = res_raz * roundTo;

        }

        return min;
    }

    private void getOvertimeSumma(String h_over, String m_over) {
        long hour = Long.parseLong( h_over );
        long minutes = Long.parseLong( m_over );


        long minute_rounded = roundetOnlyMiutes( minutes );

        if (minute_rounded == 60){
            minute_rounded=0;
            float oneHourPrice = (Float.parseFloat( et_price.getText().toString().trim() ) * Float.parseFloat( et_overtime_procent.getText().toString().trim() )) / 100;
            float oneMinuteOvertimePrice = oneHourPrice / 60;
             //System.out.println( hour + " " + minutes + " ROUNDED MINURES TO " + minute_rounded );
            overTimeSummaResult = (hour * oneHourPrice) + (minute_rounded * oneMinuteOvertimePrice);
             //System.out.println(overTimeSummaResult);
        } else {
            float oneHourPrice = (Float.parseFloat( et_price.getText().toString().trim() ) * Float.parseFloat( et_overtime_procent.getText().toString().trim() )) / 100;
            float oneMinuteOvertimePrice = oneHourPrice / 60;
             //System.out.println( hour + " " + minutes + " ROUNDED MINURES TO " + minute_rounded );
            overTimeSummaResult = (hour * oneHourPrice) + (minute_rounded * oneMinuteOvertimePrice);
             //System.out.println(overTimeSummaResult);
        }





        }




}

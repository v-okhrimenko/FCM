package com.example.fcm.MyCalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fcm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;

public class DatePickerSmall extends ConstraintLayout {

    public static ArrayList eventsList = new ArrayList<>();
    ImageButton ibtnNextMonth, ibtnPreviousMonth;
    TextView tvCurrentDate;
    GridView gridView;
    Integer month__ = 0;
    Integer year__ = 0;



    private static final int MAX_CALENDAR_DAYS = 42;

    Calendar calendar = Calendar.getInstance( getResources().getConfiguration().locale );

    //    Calendar calendar = Calendar.getInstance( Locale.getDefault() );
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.getDefault());
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    DatePickerSmall_GridAdapter myCalendarGridAdapter;


    ArrayList<String> dateFromEventsMore2 = new ArrayList<>();
    ArrayAdapter<String> adapter2;


    List<Date> dates = new ArrayList<>();
//    List<DatePicker_Events> eventsList = new ArrayList<>();
//    ArrayList<String> xxx = new ArrayList<>();



    public DatePickerSmall(Context context) {
        super( context );
    }

    public DatePickerSmall(final Context context, @Nullable AttributeSet attrs) {
        super( context, attrs );
        this.context = context;
        IntializeLayout();
        SetUpCalndar();



        ibtnPreviousMonth.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add( MONTH, -1 );
                SetUpCalndar();
            }
        } );


        ibtnNextMonth.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add( MONTH, 1 );
                SetUpCalndar();

            }
        } );
    }

    public DatePickerSmall(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );

    }


    private void SetUpCalndar() {

        String currentDate = dateFormat.format( calendar.getTime() );
        tvCurrentDate.setText( currentDate );
        dates.clear();

        Calendar monthCalendar = (Calendar) calendar.clone();
        month__ = monthCalendar.get( Calendar.MONTH );
        year__ = monthCalendar.get( Calendar.YEAR );


        monthCalendar.setFirstDayOfWeek( MONDAY );
        int fdw = monthCalendar.getFirstDayOfWeek();

        if (fdw == 1) {

            monthCalendar.set( MONTH, month__ );  // month is 0 based on calendar
            monthCalendar.set( YEAR, year__ );
            monthCalendar.set( DAY_OF_MONTH, 1 );
            monthCalendar.getTime();
            monthCalendar.set( DAY_OF_WEEK, SUNDAY );
            monthCalendar.add( DATE, 0 );

        }

        if (fdw == 2) {
            monthCalendar.set( MONTH, month__ );  // month is 0 based on calendar
            monthCalendar.set( YEAR, year__ );
            monthCalendar.set( DAY_OF_MONTH, 1 );
            monthCalendar.getTime();
            monthCalendar.set( DAY_OF_WEEK, MONDAY );
            monthCalendar.add( DATE, 0 );

        }

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add( monthCalendar.getTime() );
            monthCalendar.add( Calendar.DAY_OF_MONTH, 1 );
        }
//        CollectEventMonth();
        myCalendarGridAdapter = new DatePickerSmall_GridAdapter( context, dates, calendar );
        gridView.setAdapter( myCalendarGridAdapter );


    }
//    private void CollectEventMonth(){
//
//        eventsList.add( new DatePicker_Events("Х-Фактор тря ляля","10-02-2020", "0", "0") );
//        eventsList.add( new DatePicker_Events("второй","11-02-2020", "0", "0") );
//
//    }

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


    private void IntializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View view =  inflater.inflate( R.layout.datepicker_small_layout, this );
        ibtnNextMonth = view.findViewById( R.id.ib_nextMonth );
        ibtnPreviousMonth = view.findViewById( R.id.ib_previousMonth );
        tvCurrentDate = view.findViewById( R.id.tv_currrentDatainTopofLayout );
        gridView = view.findViewById( R.id.gridView );


    }
}

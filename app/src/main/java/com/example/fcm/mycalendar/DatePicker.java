package com.example.fcm.mycalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
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

public class DatePicker extends ConstraintLayout {


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

    DatePickerGridAdapter myCalendarGridAdapter;


    public static ArrayList<String> test  =new ArrayList<>();
    ArrayList<String> dateFromEventsMore2 = new ArrayList<>();
    ArrayAdapter<String> adapter2;
    Locale locale = getResources().getConfiguration().locale;


    List<Date> dates = new ArrayList<>();
//    List<DatePickerEvents> eventsList = new ArrayList<>();
//    ArrayList<String> xxx = new ArrayList<>();



    public DatePicker(Context context) {
        super( context );
    }

    public DatePicker(final Context context, @Nullable AttributeSet attrs) {
        super( context, attrs );
        this.context = context;
        localeSet();
        IntializeLayout();
        SetUpCalndar();



        ibtnPreviousMonth.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add( MONTH, -1 );
                SetUpCalndar();
            }
        } );


        ibtnNextMonth.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add( MONTH, 1 );
                SetUpCalndar();

            }
        } );
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        myCalendarGridAdapter = new DatePickerGridAdapter( context, dates, calendar, eventsList, test );
        gridView.setAdapter( myCalendarGridAdapter );

        myCalendarGridAdapter.setOnLongClick( new CalendarViewGridAdapter.onLongClickListener() {
            @Override
            public void onItemClick(String position) {
                //System.out.println( position );
                AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
                LayoutInflater inflater = LayoutInflater.from( getContext());
                final View regiserWindow = inflater.inflate(R.layout.calendarview_more_two_event_inflater, null);
                builder.setView(regiserWindow);

//                TextView tv1 = regiserWindow.findViewById(R.id.tv_event_1);
//                TextView tv2 = regiserWindow.findViewById(R.id.tv_event_2);
                ListView listView = regiserWindow.findViewById(R.id.listMoreTwoJobs);

//                tv1.setText( position.toString() );
//                tv2.setText( position.toString() );




                dateFromEventsMore2.clear();
                for (int i = 0; i< CalendarViewGridAdapter.events.size(); i++){
                    if(ConvertStringToDate( CalendarViewGridAdapter.events.get( i ).date).equals( ConvertStringToDate(position) )){
//                        Toast.makeText( getContext(),CalendarViewGridAdapter.events.get( i ).getEvent() , Toast.LENGTH_SHORT).show();
                        //System.out.println("EV "+ CalendarViewGridAdapter.events.get( i ).getEvent());
                        dateFromEventsMore2.add( CalendarViewGridAdapter.events.get( i ).getEvent() );

                    }}
//                System.out.println( dateFromEventsMore2 );


                adapter2 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, dateFromEventsMore2);

                // Привяжем массив через адаптер к ListView
                listView.setAdapter(adapter2);


//
//                if (dateFromEventsMore2.size()<2) {
////                    tv1.setText( dateFromEventsMore2.get( 0 ) );
//                } else {
////                tv1.setText( dateFromEventsMore2.get( 0 ) );
////                tv2.setText( dateFromEventsMore2.get( 1 ) );
//                }


                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( true );
                dialog.show();


            }
        } );


    }
//    private void CollectEventMonth(){
//
//        eventsList.add( new DatePickerEvents("Х-Фактор тря ляля","10-02-2020", "0", "0") );
//        eventsList.add( new DatePickerEvents("второй","11-02-2020", "0", "0") );
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
        final View view =  inflater.inflate( R.layout.datepicker_layout, this );
        ibtnNextMonth = view.findViewById( R.id.ib_nextMonth );
        ibtnPreviousMonth = view.findViewById( R.id.ib_previousMonth );
        tvCurrentDate = view.findViewById( R.id.tv_currrentDatainTopofLayout );
        gridView = view.findViewById( R.id.gridView );


    }
    private void localeSet() {

        Locale locale = Resources.getSystem().getConfiguration().locale;

//        if(locale.getCountry().equals( "UA" ) || locale.getCountry().equals( "RU" ) ){
//
//            Locale locale_new = new Locale("ru");
//            Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().
                updateConfiguration(config, getContext().getResources().getDisplayMetrics());

        //}
    }
}


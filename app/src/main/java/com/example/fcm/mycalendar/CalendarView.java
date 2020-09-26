package com.example.fcm.mycalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fcm.R;
import com.example.fcm.helper.DbConnection;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.rateFixed.FixedJobReview;
import com.example.fcm.rateHour.ForHourJobReview;
import com.example.fcm.rateSmena.ForSmenaJobReview;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;

public class CalendarView extends ConstraintLayout {

    private ArrayList <String> check_two_or_more_event = new ArrayList<>();
    public static ArrayList <String> two_or_more_event = new ArrayList<>();

    public static ArrayList eventsList = new ArrayList<>();
    ArrayList<String> dateFromEventsMore2 = new ArrayList<>();

    private ImageButton ibtnNextMonth, ibtnPreviousMonth;
    private TextView tvCurrentDate;
    private GridView gridView;
    private Integer month__ = 0;
    private Integer year__ = 0;

    private static final int MAX_CALENDAR_DAYS = 42;

    Locale locale = getResources().getConfiguration().locale;
    Calendar calendar = Calendar.getInstance( getResources().getConfiguration().locale );

//    Calendar calendar = Calendar.getInstance( Locale.getDefault() );
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL yyyy", locale);
    SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", locale);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", locale);

    CalendarViewGridAdapter calendarViewGridAdapter;
    ListView listView;


    ArrayAdapter<String> adapter2;

    private ArrayList<String> eventsDate = new ArrayList<>();
    private ArrayList<String> eventsName = new ArrayList<>();
    List<Date> dates = new ArrayList<>();
    private ArrayList<String> dateFromEventsMore2_new = new ArrayList<>();
    //ArrayList<String> dateFromEventsMore2_new = new ArrayList<>();

    public CalendarView(Context context) {
        super( context );
    }

    public CalendarView(final Context context, @Nullable AttributeSet attrs) {
        super( context, attrs );
        this.context = context;
        localeSet();
        IntializeLayout();
        SetUpCalndar();

        Calendar calendarToday = Calendar.getInstance();
        setCalendarCurrent( calendarToday.get( Calendar.YEAR ), calendarToday.get( Calendar.MONTH ) );

        ibtnPreviousMonth.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar.add( MONTH, -1 );
                setCalendarCurrent( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ) );
                SetUpCalndar();

            }
        } );
        ibtnNextMonth.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add( MONTH, 1 );
                setCalendarCurrent( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ) );
                SetUpCalndar();

            }
        } );
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

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );

    }

    public void SetUpCalndar() {

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

        calendarViewGridAdapter = new CalendarViewGridAdapter( context, dates, calendar, eventsList);
        gridView.setAdapter( calendarViewGridAdapter );
//
        calendarViewGridAdapter.setOnLongClick( new CalendarViewGridAdapter.onLongClickListener() {
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
                TextView textDate = regiserWindow.findViewById( R.id.tv_date_in_listView );


//                tv1.setText( position.toString() );
//                tv2.setText( position.toString() );

                dateFromEventsMore2.clear();
                dateFromEventsMore2_new.clear();
                eventsDate.clear();
                eventsName.clear();
                for (int i = 0; i< CalendarViewGridAdapter.events.size(); i++){
                    if(ConvertStringToDate( CalendarViewGridAdapter.events.get( i ).date).equals( ConvertStringToDate(position) )){

                        //dateFromEventsMore2.add( CalendarViewGridAdapter.events.get( i ).getEvent() );
                        eventsDate.add( CalendarViewGridAdapter.events.get( i ).getDate() );
                        eventsName.add( CalendarViewGridAdapter.events.get( i ).getEvent() );
                        dateFromEventsMore2_new.add( CalendarViewGridAdapter.events.get( i ).getEvent() );


                    }}
                String [] dateFromEventsMore2_new_string = new String[dateFromEventsMore2_new.size()];
                for(int i = 0; i<dateFromEventsMore2_new.size(); i++){
                    dateFromEventsMore2_new_string[i] = dateFromEventsMore2_new.get( i );
                }
//                adapter2 = new ArrayAdapter<String>(getContext(),
//                        //android.R.layout.simple_list_item_1, dateFromEventsMore2);
//                        android.R.layout.simple_list_item_1, dateFromEventsMore2);
//
//
                        // Привяжем массив через адаптер к ListView
                CustomAdapter customAdapter = new CustomAdapter( dateFromEventsMore2_new_string, regiserWindow.getContext() );

                //listView.setAdapter(adapter2);
                listView.setAdapter(customAdapter);
                textDate.setText( eventsDate.get( 0 ) );





                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( true );
                dialog.show();

                listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        DbConnection.DBJOBS.whereEqualTo( "name", eventsName.get( position )  ).whereEqualTo( "date", Helper.stringToData( eventsDate.get( position ) ) ).get(Source.CACHE).addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                                    MainWork mainWork = snapshot.toObject( MainWork.class );
                                    System.out.println(mainWork.getDate() + " " + mainWork.getName() + " " + mainWork.getStatus());

                                    String priceFix = String.valueOf(mainWork.getPrice_fixed());
                                    String priceHour = String.valueOf(mainWork.getPrice_hour());
                                    String priceSm = String.valueOf(mainWork.getPrice_smena());
                                    String durationSm = String.valueOf(mainWork.getSmena_duration());
                                    String overTimeProcent = String.valueOf(mainWork.getOvertime_pocent());
                                    String startTime = String.valueOf(mainWork.getStart());
                                    String endTime = String.valueOf(mainWork.getEnd());
                                    String finalCost = String.valueOf(mainWork.getZarabotanoFinal());

                                    String uid =String.valueOf( mainWork.getUniqId());
                                    String name = mainWork.getName();
                                    String date = String.valueOf(Helper.dataToString(mainWork.getDate()));
                                    String description = mainWork.getDiscription();
                                    String valuta = mainWork.getValuta();
                                    String status = String.valueOf(mainWork.getStatus());
                                    String alarm;
                                    String rounded_nimutes = String.valueOf( mainWork.getRounded_minut() );
                                    String half_shiht = String.valueOf( mainWork.getHalf_shift() );
                                    String half_shiht_hours = String.valueOf( mainWork.getHalf_shift_hours() );
                                    String documentName = snapshot.getId();
                                    String jobType = mainWork.getTempalte_type();

                                    if(mainWork.getAlarm1()==null){
                                        alarm = null;
                                    } else {
                                        alarm = String.valueOf(mainWork.getAlarm1());
                                    }

                                    switch (jobType){
                                        case "fixed":
                                            Intent intent = new Intent( getContext(), FixedJobReview.class);

                                            intent.putExtra("uid", uid);
                                            intent.putExtra("name", name);
                                            intent.putExtra("price", priceFix );
                                            intent.putExtra("date", date );
                                            intent.putExtra("description", description );
                                            intent.putExtra("valuta", valuta );
                                            intent.putExtra("status", status );
                                            intent.putExtra("alarm", alarm );
                                            intent.putExtra("documentName", documentName );

                                            context.startActivity(intent);
                                            break;

                                        case "for smena":

                                            Intent intent_forSmena = new Intent( getContext(), ForSmenaJobReview.class);

                                            intent_forSmena.putExtra("uid", uid);
                                            intent_forSmena.putExtra("name", name);
                                            intent_forSmena.putExtra("priceShift", priceSm);
                                            intent_forSmena.putExtra("durationSm", durationSm);
                                            intent_forSmena.putExtra("overTimeProcent", overTimeProcent);
                                            intent_forSmena.putExtra("startTime", startTime);
                                            intent_forSmena.putExtra("endTime", endTime);
                                            intent_forSmena.putExtra("finalCost", finalCost);
                                            intent_forSmena.putExtra("date", date );
                                            intent_forSmena.putExtra("description", description );
                                            intent_forSmena.putExtra("valuta", valuta );
                                            intent_forSmena.putExtra("status", status );
                                            intent_forSmena.putExtra("alarm", alarm );
                                            intent_forSmena.putExtra("documentName", documentName );
                                            intent_forSmena.putExtra("rounded_nimutes", rounded_nimutes );
                                            intent_forSmena.putExtra("half_shiht", half_shiht );
                                            intent_forSmena.putExtra("half_shiht_hours", half_shiht_hours );

                                            context.startActivity(intent_forSmena);
                                            break;
                                        case "for hour":

                                            Intent intent_forHour = new Intent( getContext(), ForHourJobReview.class);

                                            intent_forHour.putExtra("uid", uid);
                                            intent_forHour.putExtra("name", name);
                                            intent_forHour.putExtra("priceHour", priceHour);
                                            intent_forHour.putExtra("startTime", startTime);
                                            intent_forHour.putExtra("endTime", endTime);
                                            intent_forHour.putExtra("finalCost", finalCost);
                                            intent_forHour.putExtra("date", date );
                                            intent_forHour.putExtra("description", description );
                                            intent_forHour.putExtra("valuta", valuta );
                                            intent_forHour.putExtra("status", status );
                                            intent_forHour.putExtra("alarm", alarm );
                                            intent_forHour.putExtra("documentName", documentName );
                                            intent_forHour.putExtra("rounded_nimutes", rounded_nimutes );

                                            context.startActivity(intent_forHour);
                                            //context.overridePendingTransition(0, 0);
                                            break;
                                    }
                                }
                            }
                        } );
                    }
                } );

            }
        } );

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
    public void updateCalendar() {
        Calendar calendarToday = Calendar.getInstance();
        setCalendarCurrent( calendarToday.get( Calendar.YEAR ), calendarToday.get( Calendar.MONTH ) );
    }
    public void CollectEventMonth(){
        SetUpCalndar();
    }

    private void IntializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View view =  inflater.inflate( R.layout.calendarview_layout, this );
        ibtnNextMonth = view.findViewById( R.id.ib_nextMonthMain );
        ibtnPreviousMonth = view.findViewById( R.id.ib_previousMonthMain );
        tvCurrentDate = view.findViewById( R.id.tv_currrentDatainTopofLayoutMain );
        gridView = view.findViewById( R.id.gridViewMain );

    }

    public  void setCalendarCurrent (int year, int month) {

        eventsList.clear();
        check_two_or_more_event.clear();
        two_or_more_event.clear();

        Runnable run = new Runnable() {
            public void run() {
                Calendar cFirstDayInMonth = Calendar.getInstance();
                Calendar cLastDayInMonth = Calendar.getInstance();

                Date firesDayInMonth = new Date();
                Date lastDayInMonth = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, 1);

                int maxDay =  calendar.getActualMaximum( Calendar.DAY_OF_MONTH );

                switch (month){
                    case 0:

                        cFirstDayInMonth.set( Calendar.YEAR,year  );
                        cFirstDayInMonth.set( Calendar.MONTH, month-1 );
                        cFirstDayInMonth.set( Calendar.DAY_OF_MONTH, 26 );
                        cFirstDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
                        cFirstDayInMonth.set( Calendar.MINUTE,0 );
                        cFirstDayInMonth.set( Calendar.SECOND,0 );
                        cFirstDayInMonth.set( Calendar.MILLISECOND,0 );

                        firesDayInMonth.setTime( cFirstDayInMonth.getTimeInMillis() );

                        cLastDayInMonth.set( Calendar.YEAR,year);
                        cLastDayInMonth.set( Calendar.MONTH, 0 );
                        cLastDayInMonth.set( Calendar.DAY_OF_MONTH, 14 );
                        cLastDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
                        cLastDayInMonth.set( Calendar.MINUTE,0 );
                        cLastDayInMonth.set( Calendar.SECOND,0 );
                        cLastDayInMonth.set( Calendar.MILLISECOND,0 );

                        lastDayInMonth.setTime( cLastDayInMonth.getTimeInMillis() );
//                        System.out.println("FIRST     " + firesDayInMonth.getYear() + " month "+firesDayInMonth.getMonth());
//                        System.out.println("LAST     " + lastDayInMonth.getYear() + " month "+lastDayInMonth.getMonth());
                        break;
                    case 11:

                        cFirstDayInMonth.set( Calendar.YEAR,year  );
                        cFirstDayInMonth.set( Calendar.MONTH, month-1 );
                        cFirstDayInMonth.set( Calendar.DAY_OF_MONTH, 25 );
                        cFirstDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
                        cFirstDayInMonth.set( Calendar.MINUTE,0 );
                        cFirstDayInMonth.set( Calendar.SECOND,0 );
                        cFirstDayInMonth.set( Calendar.MILLISECOND,0 );

                        firesDayInMonth.setTime( cFirstDayInMonth.getTimeInMillis() );

                        cLastDayInMonth.set( Calendar.YEAR,year+1);
                        cLastDayInMonth.set( Calendar.MONTH, 0 );
                        cLastDayInMonth.set( Calendar.DAY_OF_MONTH, 11 );
                        cLastDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
                        cLastDayInMonth.set( Calendar.MINUTE,0 );
                        cLastDayInMonth.set( Calendar.SECOND,0 );
                        cLastDayInMonth.set( Calendar.MILLISECOND,0 );

                        lastDayInMonth.setTime( cLastDayInMonth.getTimeInMillis() );

//                        System.out.println("FIRST     " + firesDayInMonth.getYear() + " month "+firesDayInMonth.getMonth());
//                        System.out.println("LAST     " + lastDayInMonth.getYear() + " month "+lastDayInMonth.getMonth());

                        break;
                    default:
//                        System.out.println("OTHER     "+month);

                        cFirstDayInMonth.set( Calendar.YEAR,year  );
                        cFirstDayInMonth.set( Calendar.MONTH, month-1 );
                        cFirstDayInMonth.set( Calendar.DAY_OF_MONTH, 25 );
                        cFirstDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
                        cFirstDayInMonth.set( Calendar.MINUTE,0 );
                        cFirstDayInMonth.set( Calendar.SECOND,0 );
                        cFirstDayInMonth.set( Calendar.MILLISECOND,0 );

                        firesDayInMonth.setTime( cFirstDayInMonth.getTimeInMillis() );


                        cLastDayInMonth.set( Calendar.YEAR,year  );
                        cLastDayInMonth.set( Calendar.MONTH, month+1 );
                        cLastDayInMonth.set( Calendar.DAY_OF_MONTH, 11 );
                        cLastDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
                        cLastDayInMonth.set( Calendar.MINUTE,0 );
                        cLastDayInMonth.set( Calendar.SECOND,0 );
                        cLastDayInMonth.set( Calendar.MILLISECOND,0 );

                        lastDayInMonth.setTime( cLastDayInMonth.getTimeInMillis() );
//                        System.out.println("11     " + lastDayInMonth.getYear());

                        break;
                }
                DbConnection.DBJOBS
                        .whereGreaterThanOrEqualTo( "date",firesDayInMonth )
                        .whereLessThanOrEqualTo( "date", lastDayInMonth ).get( Source.CACHE)
                        .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                                    MainWork mainWork = snapshot.toObject( MainWork.class);
                                    Calendar c = Calendar.getInstance();
                                    c.setTime( mainWork.getDate() );
                                    String dateEvent = c.get( Calendar.DAY_OF_MONTH ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.YEAR );
                                    eventsList.add( new CalendarViewEvents( mainWork.getName(), dateEvent, mainWork.getStatus() ) );
                                    check_two_or_more_event.add( dateEvent );
                                    twoSets( check_two_or_more_event );
                                }
                                SetUpCalndar();
                            }
                        } );



            }};
        new Thread(run).start();

    }
    private static void twoSets(ArrayList<String> data) {
        Set<String> foundStrings = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (String str : data)
        {
            if (foundStrings.contains(str))
            {
                duplicates.add(str);
                two_or_more_event.add( str );
            }
            else
            {
                foundStrings.add(str);
            }
        }

    }

    class CustomAdapter extends BaseAdapter{
        String[] jobName;
        Context context;
        public CustomAdapter(String[] jobName, Context context) {
            this.jobName = jobName;
            this.context = context;
        }

        @Override
        public int getCount() {
            return jobName.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view =  inflater.inflate( R.layout.listviewrowitem, null );

            TextView textView = view.findViewById(R.id.tv_job_in_view);
            textView.setText(jobName[position] );
            return view;
        }
    }



}




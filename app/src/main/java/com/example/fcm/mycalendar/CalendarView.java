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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fcm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

public class CalendarView extends ConstraintLayout {





    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;


    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();

    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");



    public static ArrayList eventsList = new ArrayList<>();
    public static ArrayList eventsList_test = new ArrayList<>();

    ArrayList<String> dateFromEventsMore2 = new ArrayList<>();

    ImageButton ibtnNextMonth, ibtnPreviousMonth;
    TextView tvCurrentDate;
    GridView gridView;
    Integer month__ = 0;
    Integer year__ = 0;



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


    public static ArrayList<String> test  =new ArrayList<>();
    ArrayAdapter<String> adapter2;


    List<Date> dates = new ArrayList<>();
//    List<DatePickerEvents> eventsList = new ArrayList<>();
//    ArrayList<String> xxx = new ArrayList<>();


    public CalendarView(Context context) {
        super( context );
    }

    public CalendarView(final Context context, @Nullable AttributeSet attrs) {
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


    private void SetUpCalndar() {

        String currentDate = dateFormat.format( calendar.getTime() );
        //System.out.println( currentDate );

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
        //CollectEventMonth();
        calendarViewGridAdapter = new CalendarViewGridAdapter( context, dates, calendar, eventsList, test );
        gridView.setAdapter( calendarViewGridAdapter );

       calendarViewGridAdapter.setOnClick( new CalendarViewGridAdapter.onClickListener() {
           @Override
           public void onItemClick(String position) {
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

               final AlertDialog dialog = builder.create();
               dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
               dialog.setCancelable( true );
               dialog.show();

               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                       System.out.println("!!!!!!!!!!");
                   }
               });

           }

       } );




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


                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( true );
                dialog.show();


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


}




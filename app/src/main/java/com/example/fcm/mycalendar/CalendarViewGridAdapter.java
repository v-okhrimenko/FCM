package com.example.fcm.mycalendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fcm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarViewGridAdapter extends ArrayAdapter {



    ArrayList<String> datesAdd = new ArrayList<>();
    ArrayList<String> checkEvents = new ArrayList<String>();

    ArrayList<String> two_or_more_event = new ArrayList<>();
    List<Date> dates;
    Calendar currentDate;
    static List<CalendarViewEvents> events;
    LayoutInflater inflater;
    TextView sga;
    private int position_to;

    private CalendarViewGridAdapter.onLongClickListener listener;
    private CalendarViewGridAdapter.onClickListener listener_shot;



    public CalendarViewGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, ArrayList<CalendarViewEvents> events) {
        super( context,  R.layout.calendarview_singl_day );
//        this.test = test;
        this.dates=dates;
        this.currentDate=currentDate;
        this.events=events;
        inflater=LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {

//        Potok2 potok2 = new Potok2();
//        potok2.run();


        Date monthDate = dates.get( position );

        final Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar dataCalendar = Calendar.getInstance();
        dataCalendar.setFirstDayOfWeek( Calendar.MONDAY );
        dataCalendar.setTime(monthDate);
        int DayNo=dataCalendar.get(Calendar.DAY_OF_MONTH);
        final int dispalyMonth=dataCalendar.get(Calendar.MONTH);
        int dispalyYear=dataCalendar.get(Calendar.YEAR);
        final int displayDay = dataCalendar.get(Calendar.DAY_OF_MONTH);
        final int currentMonth=currentDate.get(Calendar.MONTH);
        int currentYear=currentDate.get(Calendar.YEAR);
        final int currentDay=currentDate.get(Calendar.DAY_OF_MONTH);


        View view = convertView;



//        if(test.size()>0){
//            for (int i = 0;  i< test.size(); i++){
//                if (datesAdd.contains( test.get( i ) )){
//
//
//                    break;
//                }
//                else{
//                    datesAdd.add( test.get( i ) );
//
//
//                }
//            }
//        }


        if(view == null) {
            view = inflater.inflate( R.layout.calendarview_singl_day, parent,false );


        }
        //Дни цифрами
        TextView Day_Number = view.findViewById( R.id.main_caledar_day );
        Day_Number.setText( String.valueOf( DayNo ) );
        Calendar eventCalendar = Calendar.getInstance();

        //ArrayList<String> arrayList = new ArrayList<>();

        if (dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH ) ) {

            view.setBackgroundColor( getContext().getResources().getColor( R.color.clear ) );
            TextView Dd = view.findViewById( R.id.main_caledar_day );
            Dd.setTextColor(getContext().getResources().getColor( R.color.white ));
            //view.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
            view.setBackground( getContext().getDrawable( R.drawable.todaybgclear ) );
        }

        if (dispalyMonth == currentMonth && dispalyYear == currentYear && displayDay != today.get( Calendar.DAY_OF_MONTH )) {

            TextView Dd = view.findViewById( R.id.main_caledar_day );
            Dd.setTextColor(getContext().getResources().getColor( R.color.black ));
        }

        else if (dispalyMonth!= currentMonth) {
            view.setBackgroundColor( getContext().getResources().getColor( R.color.clear ) );
            TextView Dd = view.findViewById( R.id.main_caledar_day );
            Dd.setTextColor(getContext().getResources().getColor( R.color.grey ));
            Dd.setTextSize( 10 );
        }

//        if(displayDay == today.get( Calendar.DAY_OF_MONTH ) && dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR )){
////                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
//            view.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
//            TextView Dd = view.findViewById( R.id.main_caledar_day );
//            Dd.setTextColor(getContext().getResources().getColor( R.color.white ));
////
//        }

        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date x = (Date) getItem( position );
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String d = formatter.format(x);

                TextView ev = v.findViewById( R.id.main_event_set );

                if (!ev.getText().toString().equals( "" )){
                    listener.onItemClick( d );
                }
            }
        } );
        view.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Date x = (Date) getItem( position );
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String d = formatter.format(x);

                TextView ev = v.findViewById( R.id.main_event_set );

                if (!ev.getText().toString().equals( "" )){
                    listener.onItemClick( d );
                }
                return true;
            }
        } );


//        TextView Day_Number = view.findViewById( R.id.main_caledar_day );
//        Day_Number.setText( String.valueOf( DayNo ) );
//        Calendar eventCalendar = Calendar.getInstance();
//        ArrayList<String> arrayList = new ArrayList<>();
//
//        for (int f = 0; f< test.size(); f++){
//            Calendar nc = currentDate.getInstance();
//            nc.setTime( ConvertStringToDate(test.get( f ) ));
//
//            if (displayDay==nc.get( Calendar.DAY_OF_MONTH ) && dispalyMonth == nc.get( Calendar.MONTH ) && dispalyYear == nc.get( Calendar.YEAR )){
//
//                view.setBackground( getContext().getDrawable( R.drawable.selected ) );
//                TextView Dd = view.findViewById( R.id.main_caledar_day );
//                Dd.setTextColor(getContext().getResources().getColor( R.color.white ));
//
//            }
//
//        }


        View finalView = view;
        if (CalendarView.two_or_more_event.size()<1){
            LinearLayout two_or_more_event = finalView.findViewById(R.id.calendar_if_many_dots );
            two_or_more_event.setVisibility( View.INVISIBLE );

        } else {



            // two or more events show dots
            for (int two = 0; two< CalendarView.two_or_more_event.size(); two++){
                Calendar calpovtor = Calendar.getInstance();
                Date twoDate = ConvertStringToDate( CalendarView.two_or_more_event.get( two ) );
                calpovtor.set( Calendar.DAY_OF_MONTH, twoDate.getDate() );
                calpovtor.set( Calendar.MONTH, ConvertStringToDate( CalendarView.two_or_more_event.get( two ) ).getMonth() );
                calpovtor.set( Calendar.YEAR, ConvertStringToDate( CalendarView.two_or_more_event.get( two ) ).getYear()+1900 );


                if (calpovtor.get( Calendar.YEAR )==dispalyYear &&calpovtor.get( Calendar.MONTH ) == dispalyMonth && calpovtor.get( Calendar.DAY_OF_MONTH ) == displayDay  ){
                    LinearLayout two_or_more_event = finalView.findViewById(R.id.calendar_if_many_dots );
                    two_or_more_event.setVisibility( View.VISIBLE );
                }
            }

        }
        //
        Calendar cal = Calendar.getInstance();
        Calendar calLast = cal.getInstance();
        calLast.set( Calendar.DAY_OF_MONTH, cal.get( Calendar.DAY_OF_MONTH-1 ) );
        calLast.set(Calendar.MONTH, cal.get( Calendar.MONTH ) );
        calLast.set( Calendar.YEAR, cal.get( Calendar.YEAR ) );
//
        eventCalendar.clear();
        for (int i = 0;  i< events.size(); i++){


            eventCalendar.setTime(ConvertStringToDate( events.get( i ).getDate() ));
            Calendar calCur = Calendar.getInstance();
            calCur.set( Calendar.DAY_OF_MONTH, (ConvertStringToDate( events.get( i ).date ).getDay()) );
            calCur.set( Calendar.MONTH, (ConvertStringToDate( events.get( i ).date ).getMonth()) );
            calCur.set( Calendar.YEAR, (ConvertStringToDate( events.get( i ).date ).getYear()) );
//
//
            if (eventCalendar.get( Calendar.YEAR )==dispalyYear && eventCalendar.get( Calendar.MONTH ) == dispalyMonth && eventCalendar.get( Calendar.DAY_OF_MONTH ) == displayDay){
                checkEvents.add(events.get( i ).getDate() );

                if(ConvertStringToDate(events.get( i ).date).after(cal.getTime())){
                    if (events.get( i ).status==false){
                        TextView ev = finalView.findViewById( R.id.main_event_set );
                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg_future_no_pay ) );
                        ev.setTextColor( Color.WHITE );

                        finalView.setBackground( getContext().getDrawable( R.color.clear ) );

                    }


                    if (events.get( i ).status==true){
                        TextView ev = finalView.findViewById( R.id.main_event_set );

                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg_pay ) );
                        ev.setTextColor( Color.WHITE );

                        finalView.setBackground( getContext().getDrawable( R.color.clear ) );

                    }

                }

                if(ConvertStringToDate(events.get( i ).date).before(cal.getTime())){

                    if (events.get( i ).status==true){
                        TextView ev = finalView.findViewById( R.id.main_event_set );

                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg_pay ) );
                        ev.setTextColor( Color.WHITE );

                        finalView.setBackground( getContext().getDrawable( R.color.clear ) );
                    }
                    if (events.get( i ).status==false && calCur.before( calLast )){
                        TextView ev = finalView.findViewById( R.id.main_event_set );

                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg ) );
                        ev.setTextColor( Color.WHITE );
                        finalView.setBackground( getContext().getDrawable( R.color.clear ) );

                    }
                }
            }
//
            if (eventCalendar.get( Calendar.YEAR )==dispalyYear && eventCalendar.get( Calendar.MONTH ) == dispalyMonth && eventCalendar.get( Calendar.DAY_OF_MONTH ) == displayDay && dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH )  )
            {   checkEvents.add(events.get( i ).getDate() );
                if(events.get( i ).status==true){
                    TextView ev = finalView.findViewById( R.id.main_event_set );

                    finalView.setBackground( getContext().getDrawable( R.drawable.todaybgclear ) );
                    ev.setText( events.get(i).event );
                    ev.setBackground( getContext().getDrawable( R.drawable.event_bg_pay ) );
                    ev.setTextColor( Color.WHITE );
                }

                if (events.get( i ).status==false){
                    TextView ev = finalView.findViewById( R.id.main_event_set );

                    finalView.setBackground( getContext().getDrawable( R.drawable.todaybgclear ) );
                    ev.setText( events.get(i).event );
                    ev.setBackground( getContext().getDrawable( R.drawable.event_bg ) );
                    ev.setTextColor( Color.WHITE );
                }
//
            }
        }

        return view;
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


    @Override
    public int getCount() {
        return dates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get( position );
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf( item );
    }

    public interface onClickListener {
        void onItemClick(String position);

    }
    public void setOnClick(onClickListener listener) {
        this.listener_shot = listener_shot;
    }

    public interface onLongClickListener {
        void onItemClick(String position);

    }
    public void setOnLongClick(onLongClickListener listener) {
        this.listener = listener;
    }
}






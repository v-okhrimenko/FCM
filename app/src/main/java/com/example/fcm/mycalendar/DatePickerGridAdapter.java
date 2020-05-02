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

import com.example.fcm.CalendarMainActivity;
import com.example.fcm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatePickerGridAdapter extends ArrayAdapter  {



    private final ArrayList<String> test;
    ArrayList<String> datesAdd = new ArrayList<>();
    ArrayList<String> checkEvents = new ArrayList<String>();
    List<Date> dates;
    Calendar currentDate;
    List<DatePickerEvents> events;
    LayoutInflater inflater;
    TextView sga;
    private int position_to;

    private CalendarViewGridAdapter.onLongClickListener listener;



    public DatePickerGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<DatePickerEvents> events, ArrayList<String> test) {
        super( context,  R.layout.datepicker_single_day );
        this.test = test;
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

        if(test.size()>0){
            for (int i = 0;  i< test.size(); i++){
                if (datesAdd.contains( test.get( i ) )){


                    break;
                }
                else{
                    datesAdd.add( test.get( i ) );


                }
            }
        }

        if(view == null) {
            view = inflater.inflate( R.layout.datepicker_single_day, parent,false );


        }

        if (dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH ) ) {

            view.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor(getContext().getResources().getColor( R.color.white ));
            view.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
        }

        if (dispalyMonth == currentMonth && dispalyYear == currentYear && displayDay != today.get( Calendar.DAY_OF_MONTH )) {

            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor(getContext().getResources().getColor( R.color.black ));
        }

        else if (dispalyMonth!= currentMonth) {
            view.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor(getContext().getResources().getColor( R.color.grey ));
            Dd.setTextSize( 12 );
        }

        if(displayDay == today.get( Calendar.DAY_OF_MONTH ) && dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR )){
//                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
            view.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor(getContext().getResources().getColor( R.color.white ));
//
        }

        view.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Date x = (Date) getItem( position );
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String d = formatter.format(x);

                TextView ev = v.findViewById( R.id.event_set );

                if (!ev.getText().toString().equals( "" )){
                    listener.onItemClick( d );

                }

                return true;

            }
        } );


        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                position_to = position;

                Date x = (Date) getItem( position );
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String d = formatter.format(x);



                if (!datesAdd.contains( d )){

                    v.setBackground( getContext().getDrawable( R.drawable.selected ) );
                    TextView Dd = v.findViewById( R.id.caledar_day );
                    Dd.setTextColor(getContext().getResources().getColor( R.color.white ));
                    datesAdd.add( d );
                    DatePicker.test.add( d );
                }

                else {


                    if(getItem( position ).equals( currentDate.getTime() )){
//                    if(today.get( Calendar.YEAR )==dispalyYear && today.get( Calendar.MONTH ) == dispalyMonth && today.get( Calendar.DAY_OF_MONTH ) == displayDay && dispalyMonth == today.get( Calendar.MONTH )
//                            && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH )){
                        v.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor(getContext().getResources().getColor( R.color.white ));
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                    // НЕ ТЕКУЩИЙ МЕСЯЦ (НО ТАКОЙ ЖЕ ДЕНЬ)
                    if(displayDay == today.get( Calendar.DAY_OF_MONTH ) && dispalyMonth != today.get( Calendar.MONTH )){
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor(getContext().getResources().getColor( R.color.black ));
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                        // Та же дата но не в месяце

                    if(dispalyMonth != today.get( Calendar.MONTH )){
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor(getContext().getResources().getColor( R.color.grey ));
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }
                    if(dispalyMonth == today.get( Calendar.MONTH ) && displayDay != today.get( Calendar.DAY_OF_MONTH )){
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor(getContext().getResources().getColor( R.color.black ));
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                    if (dispalyMonth != today.get( Calendar.MONTH ) && currentMonth != today.get( Calendar.MONTH )) {
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor(getContext().getResources().getColor( R.color.black ));
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                    if (currentMonth != dispalyMonth) {
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor(getContext().getResources().getColor( R.color.grey ));
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                }
                }

        } );

        TextView Day_Number = view.findViewById( R.id.caledar_day );
        Day_Number.setText( String.valueOf( DayNo ) );
        Calendar eventCalendar = Calendar.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();

        for (int f = 0; f< test.size(); f++){
            Calendar nc = currentDate.getInstance();
            nc.setTime( ConvertStringToDate(test.get( f ) ));

            if (displayDay==nc.get( Calendar.DAY_OF_MONTH ) && dispalyMonth == nc.get( Calendar.MONTH ) && dispalyYear == nc.get( Calendar.YEAR )){

                view.setBackground( getContext().getDrawable( R.drawable.selected ) );
                TextView Dd = view.findViewById( R.id.caledar_day );
                Dd.setTextColor(getContext().getResources().getColor( R.color.white ));

            }

        }

        View finalView = view;
//        Runnable run = new Runnable() {
//            public void run() {
//
//
//            }
//        };
//        new Thread(run).start();
        // two or more events show dots
        for (int two = 0; two< CalendarMainActivity.two_or_more_event.size(); two++){
            Calendar calpovtor = Calendar.getInstance();
            Date twoDate = ConvertStringToDate( CalendarMainActivity.two_or_more_event.get( two ) );
            calpovtor.set( Calendar.DAY_OF_MONTH, twoDate.getDate() );
            calpovtor.set( Calendar.MONTH, ConvertStringToDate( CalendarMainActivity.two_or_more_event.get( two ) ).getMonth() );
            calpovtor.set( Calendar.YEAR, ConvertStringToDate( CalendarMainActivity.two_or_more_event.get( two ) ).getYear()+1900 );


            if (calpovtor.get( Calendar.YEAR )==dispalyYear &&calpovtor.get( Calendar.MONTH ) == dispalyMonth && calpovtor.get( Calendar.DAY_OF_MONTH ) == displayDay  ){
                LinearLayout two_or_more_event = finalView.findViewById(R.id.calendar_if_many_dots );
                two_or_more_event.setVisibility( View.VISIBLE );
            }


        }

        if (CalendarMainActivity.two_or_more_event.size()<1){
            LinearLayout two_or_more_event = finalView.findViewById(R.id.calendar_if_many_dots );
            two_or_more_event.setVisibility( View.INVISIBLE );

        }



        Calendar cal = Calendar.getInstance();

        Calendar calLast = cal.getInstance();
        calLast.set( Calendar.DAY_OF_MONTH, cal.get( Calendar.DAY_OF_MONTH-1 ) );
        calLast.set(Calendar.MONTH, cal.get( Calendar.MONTH ) );
        calLast.set( Calendar.YEAR, cal.get( Calendar.YEAR ) );

        eventCalendar.clear();
        for (int i = 0;  i< events.size(); i++){
            eventCalendar.setTime(ConvertStringToDate( events.get( i ).getDate() ));


            Calendar calCur = Calendar.getInstance();
            calCur.set( Calendar.DAY_OF_MONTH, (ConvertStringToDate( events.get( i ).date ).getDay()) );
            calCur.set( Calendar.MONTH, (ConvertStringToDate( events.get( i ).date ).getMonth()) );
            calCur.set( Calendar.YEAR, (ConvertStringToDate( events.get( i ).date ).getYear()) );

            if (eventCalendar.get( Calendar.YEAR )==dispalyYear && eventCalendar.get( Calendar.MONTH ) == dispalyMonth && eventCalendar.get( Calendar.DAY_OF_MONTH ) == displayDay){
                checkEvents.add(events.get( i ).getDate() );

                if(ConvertStringToDate(events.get( i ).date).after(cal.getTime())){
                    if (events.get( i ).status==false){
                        TextView ev = finalView.findViewById( R.id.event_set );
                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg_future_no_pay ) );
                        ev.setTextColor( Color.WHITE );

                        finalView.setBackground( getContext().getDrawable( R.color.white ) );

                    }

                    if (events.get( i ).status==true){
                        TextView ev = finalView.findViewById( R.id.event_set );
                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg_pay ) );
                        ev.setTextColor( Color.WHITE );

                        finalView.setBackground( getContext().getDrawable( R.color.white ) );

                    }

                }

                if(ConvertStringToDate(events.get( i ).date).before(cal.getTime())){

                    if (events.get( i ).status==true){
                        TextView ev = finalView.findViewById( R.id.event_set );

                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg_pay ) );
                        ev.setTextColor( Color.WHITE );

                        finalView.setBackground( getContext().getDrawable( R.color.white ) );
                    }
                    if (events.get( i ).status==false && calCur.before( calLast )){
                        TextView ev = finalView.findViewById( R.id.event_set );

                        ev.setText( events.get(i).event );
                        ev.setBackground( getContext().getDrawable( R.drawable.event_bg ) );
                        ev.setTextColor( Color.WHITE );

                        finalView.setBackground( getContext().getDrawable( R.color.white ) );

                    }

                }

            }

            if (eventCalendar.get( Calendar.YEAR )==dispalyYear && eventCalendar.get( Calendar.MONTH ) == dispalyMonth && eventCalendar.get( Calendar.DAY_OF_MONTH ) == displayDay && dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH )  )
            {   checkEvents.add(events.get( i ).getDate() );
                if(events.get( i ).status==true){
                    TextView ev = finalView.findViewById( R.id.event_set );

                    finalView.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
                    ev.setText( events.get(i).event );
                    ev.setBackground( getContext().getDrawable( R.drawable.event_bg_pay ) );
                    ev.setTextColor( Color.WHITE );
                }

                if (events.get( i ).status==false){
                    TextView ev = finalView.findViewById( R.id.event_set );

                    finalView.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
                    ev.setText( events.get(i).event );
                    ev.setBackground( getContext().getDrawable( R.drawable.event_bg ) );
                    ev.setTextColor( Color.WHITE );
                }


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

    public interface onLongClickListener {
        void onItemClick(String position);

    }
    public void setOnLongClick(CalendarViewGridAdapter.onLongClickListener listener) {
        this.listener = listener;
    }

}



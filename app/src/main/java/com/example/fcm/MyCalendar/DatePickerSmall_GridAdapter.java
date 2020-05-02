package com.example.fcm.MyCalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class DatePickerSmall_GridAdapter extends ArrayAdapter {

    ArrayList<String> datesAdd = new ArrayList<>();
    ArrayList<String> checkEvents = new ArrayList<String>();
    List<Date> dates;
    Calendar currentDate;

    LayoutInflater inflater;
    TextView sga;
    private int position_to;

    //private CalendarView_GridAdapter.onLongClickListener listener;



    public DatePickerSmall_GridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate) {
        super( context,  R.layout.datepicker_single_day_small );
        this.dates=dates;
        this.currentDate=currentDate;
        inflater=LayoutInflater.from(context);



    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {

//        Potok2 potok2 = new Potok2();
//        potok2.run();


        Date monthDate = dates.get( position );

        final Calendar today = Calendar.getInstance();
        today.set( Calendar.HOUR_OF_DAY, 0 );
        today.set( Calendar.MINUTE, 0 );
        today.set( Calendar.SECOND, 0 );

        Calendar dataCalendar = Calendar.getInstance();
        dataCalendar.setFirstDayOfWeek( Calendar.MONDAY );
        dataCalendar.setTime( monthDate );
        int DayNo = dataCalendar.get( Calendar.DAY_OF_MONTH );
        final int dispalyMonth = dataCalendar.get( Calendar.MONTH );
        int dispalyYear = dataCalendar.get( Calendar.YEAR );
        final int displayDay = dataCalendar.get( Calendar.DAY_OF_MONTH );
        final int currentMonth = currentDate.get( Calendar.MONTH );
        int currentYear = currentDate.get( Calendar.YEAR );
        final int currentDay = currentDate.get( Calendar.DAY_OF_MONTH );


        View view = convertView;


        if (view == null) {
            view = inflater.inflate( R.layout.datepicker_single_day_small, parent, false );


        }

        if (dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH )) {

            view.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor( getContext().getResources().getColor( R.color.white ) );
            view.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
        }

        if (dispalyMonth == currentMonth && dispalyYear == currentYear && displayDay != today.get( Calendar.DAY_OF_MONTH )) {

            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor( getContext().getResources().getColor( R.color.black ) );
        } else if (dispalyMonth != currentMonth) {
            view.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor( getContext().getResources().getColor( R.color.grey ) );
            Dd.setTextSize( 9 );
        }

        if (displayDay == today.get( Calendar.DAY_OF_MONTH ) && dispalyMonth == today.get( Calendar.MONTH ) && dispalyYear == today.get( Calendar.YEAR )) {
//                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ));
            view.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
            TextView Dd = view.findViewById( R.id.caledar_day );
            Dd.setTextColor( getContext().getResources().getColor( R.color.white ) );
//
        }

        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position_to = position;

                Date x = (Date) getItem( position );
                SimpleDateFormat formatter = new SimpleDateFormat( "dd-MM-yyyy" );
                String d = formatter.format( x );


                if (!datesAdd.contains( d )) {

                    v.setBackground( getContext().getDrawable( R.drawable.selected ) );
                    TextView Dd = v.findViewById( R.id.caledar_day );
                    Dd.setTextColor( getContext().getResources().getColor( R.color.white ) );
                    datesAdd.add( d );
                    DatePicker.test.add( d );
                } else {


                    if (getItem( position ).equals( currentDate.getTime() )) {
//                    if(today.get( Calendar.YEAR )==dispalyYear && today.get( Calendar.MONTH ) == dispalyMonth && today.get( Calendar.DAY_OF_MONTH ) == displayDay && dispalyMonth == today.get( Calendar.MONTH )
//                            && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH )){
                        v.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor( getContext().getResources().getColor( R.color.white ) );
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                    // НЕ ТЕКУЩИЙ МЕСЯЦ (НО ТАКОЙ ЖЕ ДЕНЬ)
                    if (displayDay == today.get( Calendar.DAY_OF_MONTH ) && dispalyMonth != today.get( Calendar.MONTH )) {
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor( getContext().getResources().getColor( R.color.black ) );
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                    // Та же дата но не в месяце

                    if (dispalyMonth != today.get( Calendar.MONTH )) {
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor( getContext().getResources().getColor( R.color.grey ) );
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }
                    if (dispalyMonth == today.get( Calendar.MONTH ) && displayDay != today.get( Calendar.DAY_OF_MONTH )) {
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor( getContext().getResources().getColor( R.color.black ) );
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                    if (dispalyMonth != today.get( Calendar.MONTH ) && currentMonth != today.get( Calendar.MONTH )) {
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor( getContext().getResources().getColor( R.color.black ) );
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                    if (currentMonth != dispalyMonth) {
                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
                        TextView Dd = v.findViewById( R.id.caledar_day );
                        Dd.setTextColor( getContext().getResources().getColor( R.color.grey ) );
                        datesAdd.remove( d );
                        DatePicker.test.remove( d );
                    }

                }


            }
        } );

//        view.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                position_to = position;
//
//                Date x = (Date) getItem( position );
//                SimpleDateFormat formatter = new SimpleDateFormat( "dd-MM-yyyy" );
//                String d = formatter.format( x );
//
//
//                if (!datesAdd.contains( d )) {
//
//                    v.setBackground( getContext().getDrawable( R.drawable.selected ) );
//                    TextView Dd = v.findViewById( R.id.caledar_day );
//                    Dd.setTextColor( getContext().getResources().getColor( R.color.white ) );
//                    datesAdd.add( d );
//                    DatePicker.test.add( d );
//                } else {
//
//
//                    if (getItem( position ).equals( currentDate.getTime() )) {
////                    if(today.get( Calendar.YEAR )==dispalyYear && today.get( Calendar.MONTH ) == dispalyMonth && today.get( Calendar.DAY_OF_MONTH ) == displayDay && dispalyMonth == today.get( Calendar.MONTH )
////                            && dispalyYear == today.get( Calendar.YEAR ) && displayDay == today.get( Calendar.DAY_OF_MONTH )){
//                        v.setBackground( getContext().getDrawable( R.drawable.today_calendar ) );
//                        TextView Dd = v.findViewById( R.id.caledar_day );
//                        Dd.setTextColor( getContext().getResources().getColor( R.color.white ) );
//                        datesAdd.remove( d );
//                        DatePicker.test.remove( d );
//                    }
//
//                    // НЕ ТЕКУЩИЙ МЕСЯЦ (НО ТАКОЙ ЖЕ ДЕНЬ)
//                    if (displayDay == today.get( Calendar.DAY_OF_MONTH ) && dispalyMonth != today.get( Calendar.MONTH )) {
//                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
//                        TextView Dd = v.findViewById( R.id.caledar_day );
//                        Dd.setTextColor( getContext().getResources().getColor( R.color.black ) );
//                        datesAdd.remove( d );
//                        DatePicker.test.remove( d );
//                    }
//
//                    // Та же дата но не в месяце
//
//                    if (dispalyMonth != today.get( Calendar.MONTH )) {
//                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
//                        TextView Dd = v.findViewById( R.id.caledar_day );
//                        Dd.setTextColor( getContext().getResources().getColor( R.color.grey ) );
//                        datesAdd.remove( d );
//                        DatePicker.test.remove( d );
//                    }
//                    if (dispalyMonth == today.get( Calendar.MONTH ) && displayDay != today.get( Calendar.DAY_OF_MONTH )) {
//                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
//                        TextView Dd = v.findViewById( R.id.caledar_day );
//                        Dd.setTextColor( getContext().getResources().getColor( R.color.black ) );
//                        datesAdd.remove( d );
//                        DatePicker.test.remove( d );
//                    }
//
//                    if (dispalyMonth != today.get( Calendar.MONTH ) && currentMonth != today.get( Calendar.MONTH )) {
//                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
//                        TextView Dd = v.findViewById( R.id.caledar_day );
//                        Dd.setTextColor( getContext().getResources().getColor( R.color.black ) );
//                        datesAdd.remove( d );
//                        DatePicker.test.remove( d );
//                    }
//
//                    if (currentMonth != dispalyMonth) {
//                        v.setBackgroundColor( getContext().getResources().getColor( R.color.white ) );
//                        TextView Dd = v.findViewById( R.id.caledar_day );
//                        Dd.setTextColor( getContext().getResources().getColor( R.color.grey ) );
//                        datesAdd.remove( d );
//                        DatePicker.test.remove( d );
//                    }
//
//                }
//            }
//
//        } );
//
        TextView Day_Number = view.findViewById( R.id.caledar_day );
        Day_Number.setText( String.valueOf( DayNo ) );
//
//

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


}

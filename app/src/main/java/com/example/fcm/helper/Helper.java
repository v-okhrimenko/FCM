package com.example.fcm.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.fcm.R;
import com.github.mmin18.widget.RealtimeBlurView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public abstract class Helper {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Date emptyDate() {
         return ConvertStringToDate( "01-01-1970" );
    }

    public static float fcm_rounded_minutes(long minutes, int price, int xRound){
        float summa = 0.0f;
        if(xRound==0){
            summa = price;

        } else{
            float sec_ostatok = minutes *60;
            float sec_rounded = Float.valueOf( xRound )*60;
            //System.out.println(sec_ostatok + " "+ minutes);
            //System.out.println(sec_rounded +" "+ xRound + " "+ Float.valueOf( xRound ) + " " + Float.valueOf( xRound ));

            float halh_rounded = sec_rounded/2;
            double polnih_rounded = Math.floor(sec_ostatok/sec_rounded);
            double ostatok_sec_no_polnih_raz = sec_ostatok-(polnih_rounded*sec_rounded);
            int full_raz_rounded = 0;

            if(ostatok_sec_no_polnih_raz>halh_rounded) {
                full_raz_rounded++;

            }
            float res_raz = (float) polnih_rounded+full_raz_rounded;

            summa = ((xRound*res_raz)*price)/60;
        }

        return summa;
    }

    private static Date ConvertStringToDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("ddd-MM-yyy", Locale.getDefault() );
        Date date  = null;
        try {
            date = format.parse( eventDate );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void template_already_exits(Context context, String tmp_name ){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
        LayoutInflater inflater = LayoutInflater.from(context );
        final View regiserWindow = inflater.inflate( R.layout.alert_no_correct_date_set, null );
        builder.setView( regiserWindow );

        final Button update = regiserWindow.findViewById( R.id.btn_update );
        final Button ok = regiserWindow.findViewById( R.id.btn_ok );
        final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka );
        final TextView txt = regiserWindow.findViewById( R.id.tv_txt );
        shapka.setText( tmp_name );
        txt.setText( context.getResources().getString( R.string.template_name_already_exits_txt ) );

        final androidx.appcompat.app.AlertDialog dialog3 = builder.create();
        dialog3.setCancelable( false );
        dialog3.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog3.show();

        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();
            }
        } );
    }

    public static void showCustomToast(Context context, String txt, int color, int icon, int duration ){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast,
                null);
        LinearLayout ln = layout.findViewById( R.id.custom_toast_container );
        ln.setBackground(context.getResources().getDrawable( R.drawable.event_bg ) );
        ln.setBackgroundTintList( ContextCompat.getColorStateList( context, color) );
        ImageView iv = layout.findViewById( R.id.iv );
        iv.setBackground( context.getResources().getDrawable( icon ) );
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(txt);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();

    }
    public static void showInfoRounded(Context context, RealtimeBlurView bv) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
        LayoutInflater inflater = LayoutInflater.from( context );

        final View regiserWindow = inflater.inflate( R.layout.info_rounded, null );
        builder.setView( regiserWindow );
        final Button ok = regiserWindow.findViewById( R.id.btn_ok );
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        //dialog.getWindow().setDimAmount(0.9f);
        dialog.show();
        bv.setVisibility( View.VISIBLE );
        ok.setOnClickListener( new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       bv.setVisibility( View.INVISIBLE );
                                       dialog.dismiss() ;
                                   }
                               }
        );
    }


    public static void showInfoRounded(Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
        LayoutInflater inflater = LayoutInflater.from( context );
        final View regiserWindow = inflater.inflate( R.layout.info_rounded, null );
        builder.setView( regiserWindow );
        final Button ok = regiserWindow.findViewById( R.id.btn_ok );
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        //dialog.getWindow().setDimAmount(0.9f);
        dialog.show();
        ok.setOnClickListener( v1 ->

                dialog.dismiss() );
    }

    public static void showInfoNoDateAdd(Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
        LayoutInflater inflater = LayoutInflater.from(context );
        final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
        builder.setView( regiserWindow );
        final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
        shapka_txt.setText( context.getResources().getString( R.string.Add_a_date ) );
        final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
        txt_txt.setText(  context.getResources().getString( R.string.No_Dates_sekected ) );
        final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        dialog.show();
        ok.setOnClickListener( v -> dialog.dismiss() );


    }

    public static void showInfoNoTemplatePresent(Context context) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
        LayoutInflater inflater = LayoutInflater.from( context );
        final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
        builder.setView( regiserWindow );
        final TextView shapka = regiserWindow.findViewById( R.id.tv_info_shapka );
        shapka.setText( context.getResources().getString( R.string.No_templates ) );
        final TextView txt = regiserWindow.findViewById( R.id.tv_info_txt );
        txt.setText( context.getResources().getString( R.string.No_templates_txt ) );
        final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );

        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        dialog.show();
        ok.setOnClickListener( v1 -> dialog.dismiss() );

    }

    public static String getDayOfWeekName(Context context, Date date, Locale locale) throws IllegalArgumentException {

       String returnedDayName;
        try{

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            returnedDayName = c.getDisplayName( Calendar.DAY_OF_WEEK, Calendar.LONG, locale );
            return returnedDayName;
        }
        catch (java.lang.NullPointerException ex){
            return ex.getMessage();
        }

    }

    public static String getNumOfMonth(String m, Locale locale, Context context) throws IllegalArgumentException {
        String mc;
        String mm;
        try{

            if(m.equals( context.getString( R.string.January ) )){
                mm = "0";
                return  mm;
            }
            if(m.equals(context.getString( R.string.February ) )) {
                mm = "1";
                return  mm;
            }
            if(m.equals( context.getString( R.string.March ) )){
                mm = "2";
                return  mm;
            }
            if(m.equals( context.getString( R.string.April ) )){
                mm = "3";
                return  mm;
            }
            if(m.equals( context.getString( R.string.May ) )){
                mm = "4";
                return  mm;
            }
            if(m.equals( context.getString( R.string.June ) )){
                mm = "5";
                return  mm;
            }
            if(m.equals( context.getString( R.string.July ) )){
                mm = "6";
                return  mm;
            }
            if(m.equals( context.getString( R.string.August ) )){
                mm = "7";
                return  mm;
            }
            if(m.equals( context.getString( R.string.September ) )){
                mm = "8";
                return  mm;
            }
            if(m.equals( context.getString( R.string.October ) )){
                mm = "9";
                return  mm;
            }
            if(m.equals( context.getString( R.string.November ) )){
                mm = "10";
                return  mm;
            }
            if(m.equals( context.getString( R.string.December ) )){
                mm = "11";
                return  mm;
            }
        } catch (java.lang.NullPointerException ex) {
            m=null;
        } finally {
        }
        return m;

    }

    public static String getNameOfMonth(int month, Locale locale) throws IllegalArgumentException {
        Calendar c;
        String s;
        String n;
        try {
            c=Calendar.getInstance();
            c.set(Calendar.MONTH,month);

            s=c.getDisplayName(Calendar.MONTH,Calendar.LONG,locale);
            if(s.equals( "января" )){
                n = "Январь";
                return n;}
            if(s.equals( "февраля" )){
                n = "Февраль";
                return n;}
            if(s.equals( "марта" )){
                n = "Март";
                return n;}
            if(s.equals( "апреля" )){
                n = "Апрель";
                return n;}
            if(s.equals( "мая" )){
                n = "Май";
                return n;}
            if(s.equals( "июня" )){
                n = "Июнь";
                return n;}
            if(s.equals( "июля" )){
                n = "Июль";
                return n;}
            if(s.equals( "августа" )){
                n = "Август";
                return n;}
            if(s.equals( "сентября" )){
                n = "Сентябрь";
                return n;}
            if(s.equals( "отктября" )){
                n = "Октябрь";
                return n;}
            if(s.equals( "ноября" )){
                n = "Ноябрь";
                return n;}
            if(s.equals( "декабря" )){
                n = "Декабрь";
                return n;}
        } catch (java.lang.NullPointerException ex) {
            s=null;
        } finally {

        }
        return s;

    }

    public static ArrayList convertMinutestToHours(int minuts){
        ArrayList<Integer> minutesToHours = new ArrayList<>();
        int hours = minuts/60;
        int min = minuts%60;
        minutesToHours.add(hours);
        minutesToHours.add(min);

        return minutesToHours;
    }

    public static ArrayList getHoursMinutes (Date start_time, Date stop_time){
        ArrayList<String> hourMinutes = new ArrayList();
        Calendar start = Calendar.getInstance();
        start.setTime( start_time );

        Calendar stop = Calendar.getInstance();
        stop.setTime( stop_time );

        long hours_sum = stop.getTimeInMillis() - start.getTimeInMillis();
        String h = String.format( "%d", TimeUnit.MILLISECONDS.toHours( hours_sum ) );
        String m = String.format( "%d", TimeUnit.MILLISECONDS.toMinutes( hours_sum ) - TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( hours_sum ) ) );
//                String s = String.format( "%02d", TimeUnit.MILLISECONDS.toSeconds(hours_sum) -
//                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(hours_sum)) );
        hourMinutes.add( h );
        hourMinutes.add( m );
        return hourMinutes;
    }

    public static void showInfoOvertime(Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
        LayoutInflater inflater = LayoutInflater.from( context );
        final View regiserWindow = inflater.inflate( R.layout.info_overtime, null );
        builder.setView( regiserWindow );
        final Button ok = regiserWindow.findViewById( R.id.btn_ok );
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        dialog.show();
        ok.setOnClickListener( v1 -> dialog.dismiss() );
    }

    public static int getValutaIndex(String valuta){
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
        return valutaIndex;
    }

    public static Date stringToData(String dateInString){  //идентификатор доступа, функция статична, тип возвращаемого значения и имя функции без параметров

        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
//        String dateInString = "09-12-2019";

        try {
            Date dataForCheck = formatter2.parse(dateInString);
            return dataForCheck;

        } catch (ParseException e) {
            e.printStackTrace();
        }return null;
    }

    public static String dataToString(Date x){

        java.text.SimpleDateFormat d = new java.text.SimpleDateFormat("MM");

        java.text.SimpleDateFormat m = new java.text.SimpleDateFormat("dd");

        java.text.SimpleDateFormat y = new java.text.SimpleDateFormat("yyyy");

        String day = (m.format(x.getTime()));
        String month = (d.format(x.getTime()));
        String year = (y.format(x.getTime()));
        String seldata = day+"-"+month+"-"+year;

        return seldata;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static String getConvertedTemplateNameFromFirebase(String templateType, Context context){
        String convtedTempalteName="";
        switch (templateType){
            case "for hour":
                convtedTempalteName = context.getString( R.string.for_hour );
                break;
            case "fixed":
                convtedTempalteName = context.getString( R.string.fixed_rate );
                break;
            case "for smena":
                convtedTempalteName = context.getString( R.string.posmennyu_rate );
                break;
        }

        return convtedTempalteName;
    }

    public static void hideSystemUI(View decorView) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY

//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}

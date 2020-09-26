package com.example.fcm.statistic_ui.statistic_year;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.other.MyValueFormatterOld;
import com.example.fcm.R;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.recycleviewadapter.StatisticaNoPayRv;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    Thread x;

    private TextView year_txt;
    private Integer totalM;
    private int ii;
    private String stringvsegozarabotano;

    private Integer min;
    private Integer max;

    private TextView tvAllZarabotano;
    private TextView tvoplacheno;
    private TextView tvneoplacheno;

    private TextView zarZaGod, vypZaGod, neVypZaGod, dneyOtrabZaGod,
             maxDneyZaGod, maxDneyMonthName,  minDneyZaGod,
             minDneyMonthName;

    private ImageButton ibNoPAyaAlert;

    private ObjectAnimator objectAnimator3;
    private ObjectAnimator objectAnimator4;
    private String stringNeoplachenovsegozarabotano;
    private String stringoplacheno;
    private double StrNopay;
    private ConstraintLayout clOplacheno;
    private TextView clstrringoplacheno;
    private String oplacheno_new;
    private ConstraintLayout clZarabotano;
    private TextView clStringzarabotano;
    private String zarabotano_new;
    private ConstraintLayout clNeoplacheno;
    private TextView clStringNeoplacheno;
    private String neOplacheno_new;
    private static final String TAG = "MyLogs";
    private int yearFromPicker;
    private int old_;
    private int new_;
    private int testX;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs");
    public CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");


    private ArrayList<Float> yearSummaZarabotanoToCircle = new ArrayList<>() ;
    private ArrayList<Float> yearNeoplacheno = new ArrayList<>();
    private ArrayList<Float> toPie = new ArrayList<Float>();
    private ArrayList<Float> dayOnWorkYear = new ArrayList<>();
    private HashMap<Object, Integer> minMax = new HashMap<Object, Integer>();
    private ArrayList<Entry> NoOfEmp = new ArrayList<>();
    private ArrayList<Integer> year_to_choose  = new ArrayList<>();
    private List<Entry> pieEntries = new ArrayList<>();
    private ArrayList year = new ArrayList();
    private ArrayList sumByMonth = new ArrayList();
    private ArrayList monthToPie = new ArrayList();
    private ArrayList chekMonth = new ArrayList();
    private ArrayList<Integer> chekYear = new ArrayList();
    private ArrayList<Float> monthPay = new ArrayList();

    private String datachek_1, datachek_2;
    private Date date_start, date_end;
    private PieChart pieChart;

    private Integer min_year = 0;
    private Integer max_year = 0;
    private View root;
    private ObjectAnimator objectAnimator;
    private ObjectAnimator objectAnimator2;
    private ImageView yearpick_test;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of( this ).get( DashboardViewModel.class );
        View root = inflater.inflate( R.layout.fragment_dashboard, container, false );

        pieChart = root.findViewById( R.id.piechart );
        pieChart.setNoDataText(getString( R.string.year_data_not_avialable ));
        year_txt = root.findViewById(R.id.text_view_year_stat_select_id);
        yearpick_test = root.findViewById( R.id.button_yearSelect );
        tvAllZarabotano = root.findViewById( R.id.textView_tvAllZarabotano );
        tvoplacheno = root.findViewById( R.id.textView_oplacheno2Circle );
        tvneoplacheno = root.findViewById( R.id.textView_tvneoplacheno );
        clOplacheno = root.findViewById( R.id.constLay_oplacheno );
        clstrringoplacheno = root.findViewById( R.id.textViewsumma_oplachenogo_in_circle );
        clZarabotano = root.findViewById( R.id.constLay_zarabotano );
        clStringzarabotano = root.findViewById( R.id.textViewZarabotano_in_circle );
        clNeoplacheno = root.findViewById( R.id.consLay_neoplacheno );
        clStringNeoplacheno = root.findViewById( R.id.textViewNeoplacheno_in_circle );
        zarZaGod = root.findViewById( R.id.tvZarabotanoZaGod );
        vypZaGod = root.findViewById( R.id.tvViplachenoZaGod );
        neVypZaGod = root.findViewById( R.id.tvNeViplachenoZaGod );
        dneyOtrabZaGod = root.findViewById( R.id.tvDneyOtrabotanoZaGod );
        maxDneyZaGod = root.findViewById( R.id.tvMaxDneyOtrabotanoZaGod );
        minDneyZaGod = root.findViewById( R.id.tvMinDneyOtrabotanoZaGod );
        maxDneyMonthName = root.findViewById( R.id.tvMaxMonthName );
        minDneyMonthName = root.findViewById( R.id.tvMinMonthName );
        ibNoPAyaAlert = root.findViewById( R.id.ibNoPAyaAlert );


        ibNoPAyaAlert.setVisibility( View.INVISIBLE );
        ibNoPAyaAlert.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noPayShow();
            }
        } );


        yearpick_test.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                testX = 0;

                Calendar calendar = Calendar.getInstance();
                int yearSelected = calendar.get( Calendar.YEAR );

                AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View regiserWindow = inflater.inflate(R.layout.picker_year, null);
                builder.setView(regiserWindow);

                final Button ok = regiserWindow.findViewById(R.id.button_picker_year_ok);
                final Button cnc = regiserWindow.findViewById(R.id.button_picker_year_cencel);
                final com.example.fcm.other.NumberPicker numberPicker_ = regiserWindow.findViewById(R.id.year_picker2);

                numberPicker_.setMaxValue( max );
                numberPicker_.setMinValue( min );
                numberPicker_.setValue( max );
                numberPicker_.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                setDividerColor( numberPicker_, Color.BLACK);

                numberPicker_.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                        yearFromPicker = newVal;
                        old_ = oldVal;
                        new_ = newVal;
                        testX = 1;
                    }

                } );

                final AlertDialog dialog = builder.create();

                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( false );
                dialog.show();

                cnc.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );

                ok.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (testX==1){
                            year_txt.setText( Integer.toString( yearFromPicker) );
                            infoYear( yearFromPicker );
                            dialog.dismiss();
                        }

                        if (testX==0){
                            year_txt.setText( Integer.toString( yearSelected) );
                            infoYear( yearSelected );
                            dialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                } );

            }
        } );



        checkDataForYear();

        pieChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                String y =  year_txt.getText().toString();
                String m = (String) year.get( e.getXIndex() );
                dannyVCardView(y, m, h, e);
            }

            @Override
            public void onNothingSelected() {

            }
        } );

        dashboardViewModel.getText().observe( this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText( s );
            }
        } );
        return root;
//        ArrayList NoOfEmp = new ArrayList();

    }
    private void noPayShow() {


        AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View regiserWindow = inflater.inflate(R.layout.statistic_no_pay_inflater, null);
        builder.setView(regiserWindow);

        final RecyclerView rv = regiserWindow.findViewById(R.id.rv_no_pay_inflater);
//        final Button ok = regiserWindow.findViewById(R.id.btnOk);

        Calendar c = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date );
        Date date_ok = Helper.stringToData( d1 );

        Locale locale = Resources.getSystem().getConfiguration().locale;

        System.out.println( c.get( Calendar.YEAR ) );

        if(Integer.valueOf( year_txt.getText().toString() ).equals(c.get(Calendar.YEAR))){
            String datachek_1 = "01"+"-"+"01"+"-"+year_txt.getText().toString();
            String datachek_2 = (c.get(Calendar.DATE)-1)+"-"+(c.get( Calendar.MONTH )+1)+"-"+c.get( Calendar.YEAR );
            date_start = Helper.stringToData( datachek_1 );
            date_end = Helper.stringToData( datachek_2 );


        } else {

            String datachek_1 = "01"+"-"+"01"+"-"+year_txt.getText().toString();
            String datachek_2 = "31"+"-"+"12"+"-"+year_txt.getText().toString();
            date_start = Helper.stringToData( datachek_1 );
            date_end = Helper.stringToData( datachek_2 );

        }


        System.out.println("S E" +date_start + " "+date_end );


        Query query = noteRef_addWork_Full.whereEqualTo( "status", false ).whereGreaterThanOrEqualTo( "date", date_start ).whereLessThanOrEqualTo( "date", date_end ).orderBy( "date", Query.Direction.DESCENDING );


        FirestoreRecyclerOptions<MainWork> options = new FirestoreRecyclerOptions.Builder<MainWork>()
                .setQuery(query, MainWork.class)
                .build();
        StatisticaNoPayRv adapter = new StatisticaNoPayRv( options );
        rv.setHasFixedSize( true );
        rv.setLayoutManager( new LinearLayoutManager( getContext()) );
        rv.setAdapter( adapter );
        adapter.startListening();
        rv.getAdapter().notifyDataSetChanged();


        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        dialog.show();

//        ok.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        } );
    }

    private void dannyVCardView(String y, String m, Highlight h, Entry e) {

        ArrayList<Float> nopay = new ArrayList<>();
        ArrayList<Float> pay = new ArrayList<>();
        Locale locale = Resources.getSystem().getConfiguration().locale;

        Date date = new Date(System.currentTimeMillis());
        if((date.getYear()+1900) == Integer.parseInt( y )){
            if((Integer.parseInt( getNumOfMonth( m, locale ) )-1 ) == date.getMonth()){

                String datachek_1 = "01"+ "-"+(date.getMonth()+1)+"-"+Integer.parseInt( y );
                String datachek_2 = date.getDate()-1+"-"+(date.getMonth()+1)+"-"+Integer.parseInt( y );
                date_start = Helper.stringToData( datachek_1 );
                date_end = Helper.stringToData( datachek_2 );
            }
            if((Integer.parseInt( getNumOfMonth( m, locale ) )-1 ) != date.getMonth()){

                Calendar c = new GregorianCalendar();
                c.set(Calendar.YEAR, Integer.parseInt( y ) );
                c.set(Calendar.MONTH, Integer.parseInt( getNumOfMonth( m, locale ) )-1 );

                int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                String datachek_1 = "01"+ "-"+(c.getTime().getMonth()+1)+"-"+Integer.parseInt( y );
                String datachek_2 = maximum+"-"+(c.getTime().getMonth()+1)+"-"+Integer.parseInt( y );
                date_start = Helper.stringToData( datachek_1 );
                date_end = Helper.stringToData( datachek_2 );
            }
        }
        if((date.getYear()+1900) != Integer.parseInt( y )){
            Calendar c = new GregorianCalendar();
            c.set(Calendar.YEAR, Integer.parseInt( y ) );
            c.set(Calendar.MONTH, Integer.parseInt( getNumOfMonth( m, locale ) )-1 );
            int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            String datachek_1 = "01"+ "-"+(c.getTime().getMonth()+1)+"-"+Integer.parseInt( y );
            String datachek_2 = maximum+"-"+(c.getTime().getMonth()+1)+"-"+Integer.parseInt( y );
            date_start = Helper.stringToData( datachek_1 );
            date_end = Helper.stringToData( datachek_2 );
        }

        noteRef_addWork_Full.whereGreaterThanOrEqualTo( "date", date_start ).whereLessThanOrEqualTo( "date", date_end ).orderBy( "date", Query.Direction.DESCENDING ).get( Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );
                            if(main_work.getStatus().equals( false )){
                                float n = main_work.getZarabotanoFinal();

                                nopay.add(n);
                            }

                            if(main_work.getStatus().equals( true )){
                                float p = main_work.getZarabotanoFinal();
                                pay.add(p);
                            }
                        }

                        StrNopay = sum( nopay );
                        String StrPay = String.valueOf( sum(pay) );
                        String dayOnWork = String.valueOf( (nopay.size() + pay.size()) );

                        AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        final View regiserWindow = inflater.inflate(R.layout.pichartinfo, null);
                        builder.setView(regiserWindow);

                        final TextView name_month = regiserWindow.findViewById(R.id.TextView_month_pChart_id);
                        final TextView name_price = regiserWindow.findViewById(R.id.textView_price_chart);
                        final TextView name_year = regiserWindow.findViewById(R.id.textView_year);
                        final TextView name_nopay = regiserWindow.findViewById(R.id.textViewNeVyplacheno_cardView);
                        final TextView name_pay = regiserWindow.findViewById(R.id.textViewVyplacheno_cardView);
                        final TextView hwd = regiserWindow.findViewById(R.id.textViewNeVyplacheno_cardView2);

                        name_year.setText( year_txt.getText().toString() );
                        name_price.setText( Float.toString(pieEntries.get(e.getXIndex()).getVal()) );
                        name_month.setText( (year.get( e.getXIndex() ).toString()));
                        name_nopay.setText( Float.toString( (float) StrNopay ) );
                        name_pay.setText( StrPay );
                        hwd.setText( dayOnWork );

                        final AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                        dialog.show();
                    }});
    }

    private void makeAnimation3() {
        objectAnimator2 = ObjectAnimator.ofFloat( clNeoplacheno, "alpha", 1,0 );
        long animspeed;
        animspeed = 500;
        objectAnimator2.setDuration( animspeed );
        objectAnimator2.start();
        objectAnimator4 = ObjectAnimator.ofFloat( clZarabotano, "alpha", 0,1 );
        objectAnimator4.setDuration( animspeed );
        objectAnimator4.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pieChart.setHoleColor(getContext().getColor(R.color.green_lite));
        }

    }

    private void makeAnimation2() {

        objectAnimator2 = ObjectAnimator.ofFloat( clOplacheno, "alpha", 1,0 );
        long animspeed;
        animspeed = 500;
        objectAnimator2.setDuration( animspeed );
        objectAnimator2.start();
        objectAnimator4 = ObjectAnimator.ofFloat( clNeoplacheno, "alpha", 0,1 );
        objectAnimator4.setDuration( animspeed );
        objectAnimator4.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pieChart.setHoleColor(getContext().getColor(R.color.red_lite));
        }
    }

    private void makeAnimation() {

        objectAnimator2 = ObjectAnimator.ofFloat( clZarabotano, "alpha", 1,0 );
        long animspeed;
        animspeed = 500;
        objectAnimator2.setDuration( animspeed );
        objectAnimator2.start();
        objectAnimator4 = ObjectAnimator.ofFloat( clOplacheno, "alpha", 0,1 );
        objectAnimator4.setDuration( animspeed );
        objectAnimator4.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pieChart.setHoleColor(getContext().getColor(R.color.yellow_lite));
        }
    }

    private void infoYear(int newVal) {

        monthToPie.clear();
        sumByMonth.clear();
        chekMonth.clear();
        chekYear.clear();
        yearSummaZarabotanoToCircle.clear();

        Date date = new Date(System.currentTimeMillis());
        if(newVal == (date.getYear()+1900)){
            String datachek_1 = "01"+"-"+"01"+"-"+newVal;
            String datachek_2 = date.getDate()-1+"-"+(date.getMonth()+1)+"-"+newVal;
            date_start = Helper.stringToData( datachek_1 );
            date_end = Helper.stringToData( datachek_2 );

        }
        if(newVal != (date.getYear()+1900)){
            String datachek_1 = "01"+"-"+"01"+"-"+newVal;
            String datachek_2 = "31"+"-"+"12"+"-"+newVal;
            date_start = Helper.stringToData( datachek_1 );
            date_end = Helper.stringToData( datachek_2 );
        }
        chekYear.add( newVal );

        noteRef_addWork_Full.whereGreaterThanOrEqualTo( "date", date_start ).whereLessThanOrEqualTo( "date", date_end ).orderBy( "date", Query.Direction.ASCENDING ).get( Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );
                            Locale locale = Resources.getSystem().getConfiguration().locale;

                            if ((locale.toLanguageTag().equals(  "ru-RU"  )) || (locale.toLanguageTag().equals(  "ru-UA"  )) ){
                                String a = getNameOfMonth(main_work.getDate().getMonth(), locale);
                                String s = getNameOfMonthPravilno(a, locale );
                                if (!monthToPie.contains( s )) {
                                    monthToPie.add( s );
                                    if (!chekMonth.contains( main_work.getDate() )){
                                        chekMonth.add( main_work.getDate().getMonth() );

                                    }
                                }

                            }
                            if (locale.toLanguageTag().equals(  "uk-UA"  )){
                                String a = getNameOfMonth(main_work.getDate().getMonth(), locale);
                                String s = getNameOfMonthPravilnoUA(a, locale );
                                if (!monthToPie.contains( s )) {
                                    monthToPie.add( s );
                                    if (!chekMonth.contains( main_work.getDate() )){
                                        chekMonth.add( main_work.getDate().getMonth());
                                    }
                                }
                            }

                            if (locale.getLanguage().equals(  "en"  )) {
                                String s = getNameOfMonth(main_work.getDate().getMonth(),locale);

                                if (!monthToPie.contains( s )) {
                                    monthToPie.add( s );
                                    if (!chekMonth.contains( main_work.getDate() )){
                                        chekMonth.add( (main_work.getDate().getMonth()) );
                                    }
                                }
                            }
                        }
                        addDataToSumByMonth();
                    }
                } );
    }

    private void addDataToSumByMonth() {

        yearNeoplacheno.clear();
        pieEntries.clear();
        year.clear();
        dayOnWorkYear.clear();
        minMax.clear();



        Runnable run = new Runnable() {
            public void run() {
                System.out.println("start " +Calendar.getInstance().getTime());
                for (int i=0; i< monthToPie.size(); i++){

                    totalM = 0;
                    monthPay.clear();

                    Date date = new Date(System.currentTimeMillis());

                    if(chekYear.get( 0 ) == (date.getYear()+1900)) {
                        if (chekMonth.get( i ).equals( date.getMonth() )){

                            datachek_1 = "01"+"-"+Integer.valueOf( (Integer) chekMonth.get( i )+1 )+"-"+Integer.valueOf( chekYear.get( 0 ));
                            datachek_2 = (date.getDate()-1)+"-"+Integer.valueOf( (Integer) chekMonth.get( i )+1 )+"-"+Integer.valueOf( chekYear.get( 0 ) );

                            date_start = Helper.stringToData( datachek_1 );
                            date_end = Helper.stringToData( datachek_2 );

                        } if(!chekMonth.get( i ).equals(date.getMonth())) {
                            Calendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR, chekYear.get( 0 ));
                            c.set(Calendar.MONTH, Integer.valueOf(chekMonth.get( i ).toString()) );

                            int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                            datachek_1 = "01"+"-"+Integer.valueOf( (Integer) chekMonth.get( i )+1 )+"-"+Integer.valueOf( chekYear.get( 0 ));
                            datachek_2 = maximum+"-"+Integer.valueOf( (Integer) chekMonth.get( i )+1 )+"-"+Integer.valueOf( chekYear.get( 0 ) );
                            date_start = Helper.stringToData( datachek_1 );
                            date_end = Helper.stringToData( datachek_2 );

                        }
                    } if(chekYear.get( 0 ) != (date.getYear()+1900)) {

                        Calendar c = new GregorianCalendar();
                        c.set(Calendar.YEAR, chekYear.get( 0 ));
                        c.set(Calendar.MONTH, Integer.valueOf(chekMonth.get( i ).toString()) );

                        int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                        datachek_1 = "01"+"-"+Integer.valueOf( (Integer) chekMonth.get( i )+1 )+"-"+Integer.valueOf( chekYear.get( 0 ));
                        datachek_2 = maximum+"-"+Integer.valueOf( (Integer) chekMonth.get( i )+1 )+"-"+Integer.valueOf( chekYear.get( 0 ) );
                        date_start = Helper.stringToData( datachek_1 );
                        date_end = Helper.stringToData( datachek_2 );

                    }

                    ii = -1;
                    int z = i;

                    noteRef_addWork_Full.whereGreaterThanOrEqualTo( "date", date_start ).whereLessThanOrEqualTo( "date", date_end ).orderBy( "date", Query.Direction.ASCENDING ).get( Source.CACHE)
                            .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                                                       @Override
                                                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                           for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                                               MainWork main_work = documentSnapshot.toObject( MainWork.class );
//                                System.out.println(main_work.getPrice()+"   "+main_work.getDate().getMonth());
                                                               monthPay.add(main_work.getZarabotanoFinal());
                                                               dayOnWorkYear.add(Float.valueOf( main_work.getZarabotanoFinal() ));

                                                               if(main_work.getStatus().equals( false )){
                                                                   yearNeoplacheno.add(main_work.getZarabotanoFinal());
                                                               }
                                                           }
                                                           ii = ii+1;
                                                           minMax.put(chekMonth.get( z ), monthPay.size() );

                                                           float q = sum(monthPay);
                                                           //Float b = (float)q;
                                                           toPie.add(q);
                                                           monthPay.clear();

                                                           yearSummaZarabotanoToCircle.add(q);
                                                           pieEntries.add( new Entry( (Float) q, ii ) );
                                                           year.add( monthToPie.get( z ) );


                                                           showDiagram();

                                                       }
                                                   }
                            );

                }

            }

    };
        //new Thread(run).start();

        x = new Thread(run);
        x.start();


    }

    private void showDiagram() {


        clZarabotano.setAlpha( 0 );
        clOplacheno.setAlpha( 0 );
        clNeoplacheno.setAlpha( 0 );


        try {
            Thread.sleep(500); //Приостанавливает поток на 1 секунду
            if(pieEntries.size() <1){

                Toast.makeText( getContext(),"За " + String.valueOf(max).toString() + " год, данные пока что отсутствуют", Toast.LENGTH_LONG  ).show();

                year_txt.setText( String.valueOf(max-1) );
                infoYear(max-1);

            }

            if (pieEntries.size()>=1){

                pieChart.invalidate();


                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setValueFormatter( new MyValueFormatterOld() );
                PieData data = new PieData(year, dataSet);
                pieChart.setData(data);
                Legend legend = pieChart.getLegend();
                legend.setEnabled( false );

                pieChart.setDescription(null);
                pieChart.setTouchEnabled(true);
                pieChart.setHighlightPerTapEnabled(true);

                pieChart.getRenderer().getPaintRender().setShadowLayer(25, 0, 22, ContextCompat.getColor(getContext(), R.color.neomorph_shadow_color));
                pieChart.setHoleColor(Color.TRANSPARENT);
                pieChart.setNoDataText(getString( R.string.year_data_not_avialable ));
                pieChart.setDragDecelerationFrictionCoef( 0.2f );
                pieChart.setRotationAngle(1f);
                pieChart.setDrawHoleEnabled(true);
                pieChart.setRotationEnabled( false );



//        ПРОЗРАЧНЫЙ ЦЕНТР
//        pieChart.setHoleColor(getContext().getColor(R.color.clear));

//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    pieChart.setHoleColor(getContext().getColor(R.color.green_lite));
//                }
//            } catch (Throwable t )
//            {}


                dataSet.setColors( ColorTemplate.PASTEL_COLORS);
                dataSet.setValueTextSize( 12 );
                dataSet.setValueTextColor( Color.BLACK );
                dataSet.setValueTypeface( Typeface.DEFAULT_BOLD );

                //pieChart.animateXY(400, 400);
                pieChart.setVisibility( View.VISIBLE );
                clZarabotano.setAlpha( 1 );


                double s = sum(yearSummaZarabotanoToCircle);
                zarabotano_new = String.valueOf( (float)s );
                clStringzarabotano.setText(zarabotano_new );


                double n = sum(yearNeoplacheno);
                neOplacheno_new = String.valueOf( (float) n );
                clStringNeoplacheno.setText(neOplacheno_new );

                if(n==0f){
                    ibNoPAyaAlert.setVisibility( View.INVISIBLE );
                    ibNoPAyaAlert.setEnabled( false );
                } else {
                    ibNoPAyaAlert.setVisibility( View.VISIBLE );
                    ibNoPAyaAlert.setEnabled( true );
                }

                double o = (s-n);
                oplacheno_new = String.valueOf( o );
                clstrringoplacheno.setText(oplacheno_new );

                zarZaGod.setText( zarabotano_new );
                neVypZaGod.setText(  neOplacheno_new );
                vypZaGod.setText( oplacheno_new );
                dneyOtrabZaGod.setText( Integer.toString( dayOnWorkYear.size() ) );

                ArrayList<Object> keys = new ArrayList<>();
                keys.addAll(minMax.keySet());
                ArrayList<Integer> val = new ArrayList<>(minMax.values());

                maxDneyZaGod.setText(String.valueOf(Collections.max(val)));

                int indexOfMax = 0;
                int indexOfMin = 0;
                for (int i = 1; i < val.size(); i++)
                {
                    if (val.get( i ) > val.get(indexOfMax))
                    {
                        indexOfMax = i;
                    }
                    else if (val.get( i ) < val.get(indexOfMin))
                    {
                        indexOfMin = i;
                    }
                }
                Locale locale = Resources.getSystem().getConfiguration().locale;
                maxDneyZaGod.setText(String.valueOf(Collections.max(val)));
                minDneyZaGod.setText(String.valueOf(Collections.min(val)));

                if (locale.getLanguage().equals(  "en"  )) {
                    maxDneyMonthName.setText(getNameOfMonth(Integer.valueOf( String.valueOf( keys.get(indexOfMax) )) ,locale));
                    minDneyMonthName.setText(getNameOfMonth(Integer.valueOf( String.valueOf( keys.get(indexOfMin) )) ,locale));

                } if ((locale.toLanguageTag().equals(  "ru-RU"  )) || (locale.toLanguageTag().equals(  "ru-UA"  )) ){
                    maxDneyMonthName.setText(getNameOfMonthPravilno(getNameOfMonth(Integer.valueOf( String.valueOf( keys.get(indexOfMax) ) ),locale),locale));
                    minDneyMonthName.setText(getNameOfMonthPravilno(getNameOfMonth(Integer.valueOf( String.valueOf( keys.get(indexOfMin) ) ),locale),locale));

                }if (locale.toLanguageTag().equals(  "uk-UA"  )){
                    maxDneyMonthName.setText(getNameOfMonthPravilnoUA(getNameOfMonth(Integer.valueOf( String.valueOf( keys.get(indexOfMax) ) ),locale),locale));
                    minDneyMonthName.setText(getNameOfMonthPravilnoUA(getNameOfMonth(Integer.valueOf( String.valueOf( keys.get(indexOfMin) ) ),locale),locale));
                }
            }


        } catch (Exception e) {
            System.out.println( e.getMessage() );
//
        }


    }

    private void checkDataForYear() {


        yearNeoplacheno.clear();
        pieEntries.clear();
        year.clear();
        dayOnWorkYear.clear();
        minMax.clear();


        noteRef_addWork_Full.get( Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );

                            if(!year_to_choose.contains( (main_work.getDate().getYear())+1900 )){
                                year_to_choose.add((main_work.getDate().getYear())+1900);
                            } else {
                                continue;
                            }
                        }
                        min_year = (Collections.min(year_to_choose));
                        max_year = (Collections.max(year_to_choose));

                        min = min_year;
                        max = max_year;
                        year_txt.setText( Integer.toString( max_year) );
                        infoYear( max_year );

                    }
                } );

    }

    public static float sum(ArrayList<Float> m) {
        float sum = 0;
        for(int i = 0; i < m.size(); i++)
        {
            sum = sum + m.get(i);
        }
        return sum;
    }

    String getNameOfMonthPravilno(String month, Locale locale) throws IllegalArgumentException {
        Calendar c;
        String s = null;
        String n;
        try {

            if(month.equals( "января" )){
                n = "Январь";
                return n;}
            if(month.equals( "февраля" )){
                n = "Февраль";
                return n;}
            if(month.equals( "марта" )){
                n = "Март";
                return n;}
            if(month.equals( "апреля" )){
                n = "Апрель";
                return n;}
            if(month.equals( "мая" )){
                n = "Май";
                return n;}
            if(month.equals( "июня" )){
                n = "Июнь";
                return n;}
            if(month.equals( "июля" )){
                n = "Июль";
                return n;}
            if(month.equals( "августа" )){
                n = "Август";
                return n;}
            if(month.equals( "сентября" )){
                n = "Сентябрь";
                return n;}
            if(month.equals( "октября" )){
                n = "Октябрь";
                return n;}
            if(month.equals( "ноября" )){
                n = "Ноябрь";
                return n;}
            if(month.equals( "декабря" )){
                n = "Декабрь";
                return n;}
        } catch (java.lang.NullPointerException ex) {
            s=null;
        } finally {

        }
        return s;

    }

    String getNameOfMonthPravilnoUA(String month, Locale locale) throws IllegalArgumentException {
        Calendar c;
        String s = null;
        String n;
        try {

            if(month.equals( "січня" )){
                n = "Січень";
                return n;}
            if(month.equals( "лютого" )){
                n = "Лютий";
                return n;}
            if(month.equals( "березня" )){
                n = "Березень";
                return n;}
            if(month.equals( "квітня" )){
                n = "Квітень";
                return n;}
            if(month.equals( "травня" )){
                n = "Травень";
                return n;}
            if(month.equals( "червня" )){
                n = "Червень";
                return n;}
            if(month.equals( "липня" )){
                n = "Липень";
                return n;}
            if(month.equals( "серпня" )){
                n = "Серпень";
                return n;}
            if(month.equals( "вересня" )){
                n = "Вересень";
                return n;}
            if(month.equals( "жовтня" )){
                n = "Жовтень";
                return n;}
            if(month.equals( "листопада" )){
                n = "Листопад";
                return n;}
            if(month.equals( "грудня" )){
                n = "Грудень";
                return n;}
        } catch (java.lang.NullPointerException ex) {
            s=null;
        } finally {

        }
        return s;

    }

    public String getNumOfMonth(String m, Locale locale) throws IllegalArgumentException {
        String mc;
        String mm;
        try{

            if(m.equals( getString( R.string.January ) )){
                mm = "1";
                return  mm;
            }
            if(m.equals( getString( R.string.February ) )) {
                mm = "2";
                return  mm;
            }
            if(m.equals( getString( R.string.March ) )){
                mm = "3";
                return  mm;
            }
            if(m.equals( getString( R.string.April ) )){
                mm = "4";
                return  mm;
            }
            if(m.equals( getString( R.string.May ) )){
                mm = "5";
                return  mm;
            }
            if(m.equals( getString( R.string.June ) )){
                mm = "6";
                return  mm;
            }
            if(m.equals( getString( R.string.July ) )){
                mm = "7";
                return  mm;
            }
            if(m.equals( getString( R.string.August ) )){
                mm = "8";
                return  mm;
            }
            if(m.equals( getString( R.string.September ) )){
                mm = "9";
                return  mm;
            }
            if(m.equals( getString( R.string.October ) )){
                mm = "10";
                return  mm;
            }
            if(m.equals( getString( R.string.November ) )){
                mm = "11";
                return  mm;
            }
            if(m.equals( getString( R.string.December ) )){
                mm = "12";
                return  mm;
            }
        } catch (java.lang.NullPointerException ex) {
            m=null;
        } finally {
        }
        return m;

        }

    String getNameOfMonth(int month, Locale locale) throws IllegalArgumentException {
        Calendar c;
        String s;
        String n;
        try {
            c=Calendar.getInstance();
            c.set(Calendar.MONTH,month);

            s=c.getDisplayName(Calendar.MONTH,Calendar.LONG,locale);
            if(s.equals( 0 )){
                n = getString( R.string.January );
                return n;}
            if(s.equals( 1 )){
                n = getString( R.string.February );
                return n;}
            if(s.equals( 2 )){
                n = getString( R.string.March );
                return n;}
            if(s.equals( 3 )){
                n = getString( R.string.April );
                return n;}
            if(s.equals( 4 )){
                n = getString( R.string.May );
                return n;}
            if(s.equals( 5 )){
                n = getString( R.string.June );
                return n;}
            if(s.equals( 6 )){
                n = getString( R.string.July );
                return n;}
            if(s.equals( 7 )){
                n = getString( R.string.August );
                return n;}
            if(s.equals(8 )){
                n = getString( R.string.September );
                return n;}
            if(s.equals( 9 )){
                n = getString( R.string.October );
                return n;}
            if(s.equals( 10 )){
                n = getString( R.string.November );
                return n;}
            if(s.equals( 11 )){
                n = getString( R.string.December );
                return n;}
        } catch (java.lang.NullPointerException ex) {
            s=null;
        } finally {
        }
        return s;

    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
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


}
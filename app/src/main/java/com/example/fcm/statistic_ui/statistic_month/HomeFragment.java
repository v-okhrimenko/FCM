package com.example.fcm.statistic_ui.statistic_month;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.R;
import com.example.fcm.ScreensActivity.CalendarMainActivity;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.other.NumberPicker;
import com.example.fcm.rateFixed.FixedFragmentReviewTest;
import com.example.fcm.rateHour.HourFragmentReviewTest;
import com.example.fcm.rateSmena.SmenaFragmentReviewTest;
import com.example.fcm.recycleviewadapter.StatisticaRv;
import com.example.fcm.statistic_ui.statistic_year.DashboardFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;



public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private ArrayList<Float> allSumm = new ArrayList<>();
    private ArrayList<Float> allPay = new ArrayList<>();
    private ArrayList<Float> allSumm_pay = new ArrayList<>();
    private ArrayList<Float> allSumm_Nopay = new ArrayList<>();


    int mSelectPicker;
    int ySelectPicker;

    int monthSelected;
    int yearSelected;

    String datachek_1, datachek_2;
    Date date_start, date_end;

    private StatisticaRv adapter;

    //Года в пикер
    private Integer min_year;
    private Integer max_year;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private int position;
    private String name_ud;
    private String desc_ud;
    private float price_ud;
    private Date date_ud;
    private Boolean chek_ud;
    private float sum_PayAll;
    private float sum_noPay;
    private String avatarname;
    private DocumentSnapshot ds;
    private boolean fromFragment;

    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs");
    public CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");
    private TextView month_txt, year_txt;
    View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of( this ).get( HomeViewModel.class );
        root = inflater.inflate( R.layout.fragment_home, container, false );
        //final FloatingActionButton mYsel = root.findViewById(R.id.button_month_year_picker);
        final ImageView mYsel = root.findViewById(R.id.iv_click_calendarselect);
        month_txt = root.findViewById( R.id.allsum_edittext );
        year_txt = root.findViewById( R.id.Year_select_textView );
        final RecyclerView recyclerView = root.findViewById( R.id.month_year_rv_id );

//        checkDataForYear();
        //checkNotEmpty();


        Calendar calendar = Calendar.getInstance();
        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH);

        isFromIntent();


//        rv(monthSelected,yearSelected);
        mYsel.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                int monthSelected;
//                int yearSelected;

                Locale locale = Resources.getSystem().getConfiguration().locale;

//                Calendar calendar = Calendar.getInstance();
//                yearSelected = calendar.get(Calendar.YEAR);
//                monthSelected = calendar.get(Calendar.MONTH);

                AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View regiserWindow = inflater.inflate(R.layout.picker_month_year, null);
//                regiserWindow.setAnimation( AnimationUtils.loadAnimation( getContext(), R.anim.botton_r_to_center ) );
//                regiserWindow.setElevation( 30f );
                builder.setView(regiserWindow);


                final NumberPicker numbMonth = regiserWindow.findViewById( R.id.MyNPmonthSelect );
                final NumberPicker numbYear = regiserWindow.findViewById( R.id.MyNPyearSelect );

                final Button cancel = regiserWindow.findViewById(R.id.cancel_my_np);
                final Button ok = regiserWindow.findViewById(R.id.ok_my_np);

                numbYear.setMinValue( min_year );
                numbYear.setMaxValue( max_year );
                numbYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                numbYear.setWrapSelectorWheel(true);
                numbYear.setValue( yearSelected );

                String mValues[] = { getString( R.string.January ),getString( R.string.February ),getString( R.string.March ),
                        getString( R.string.April ),getString( R.string.May ),getString( R.string.June ),getString( R.string.July ),
                        getString( R.string.August ),getString( R.string.September ),getString( R.string.October ),getString( R.string.November ),
                        getString( R.string.December )};
                setNubmerPicker(numbMonth,mValues);
                numbMonth.setValue(monthSelected );
                numbMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                setDividerColor(numbMonth, Color.BLACK);
                setDividerColor(numbYear, Color.BLACK);

                ySelectPicker=yearSelected;
                mSelectPicker=monthSelected;


                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( false );
                dialog.show();

                numbYear.setOnValueChangedListener( new android.widget.NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                        ySelectPicker = newVal;
                        if(ySelectPicker==yearSelected && mSelectPicker>monthSelected){
                            numbYear.setValue( yearSelected-1 );
                            ySelectPicker=yearSelected;
                        }
                    }
                } );

                numbMonth.setOnValueChangedListener( new android.widget.NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                        mSelectPicker = newVal;
                        System.out.println(mSelectPicker);
                        if(ySelectPicker==yearSelected && mSelectPicker>monthSelected){
                            numbYear.setValue( yearSelected );
                            ySelectPicker=yearSelected;
                        }
                    }
                } );

                cancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );
                ok.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        System.out.println( mTst + "  " + yTst );
                        rv(mSelectPicker,ySelectPicker);

                            month_txt.setText( getNameOfMonth(mSelectPicker, locale ) );
                            year_txt.setText( Integer.toString(ySelectPicker) );
                            dialog.dismiss();
                            monthSelected = mSelectPicker;
                            yearSelected = ySelectPicker;

                    }
                } );
            }

        } );


        homeViewModel.getText().observe( this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText( s );
            }
        } );
        return root;
    }

    private void checkNotEmpty() {

        Runnable run = new Runnable() {
            public void run() {



        ArrayList<Float> check = new ArrayList<>();

        Date datachek;

        Date date = new Date(System.currentTimeMillis());
        Locale locale = Resources.getSystem().getConfiguration().locale;
        Calendar c = Calendar.getInstance( locale );
        c.setTime(date);


        String datachek_ = (c.get( Calendar.DAY_OF_MONTH )-1)+"-"+(c.get( Calendar.MONTH)+1 )+"-"+c.get( Calendar.YEAR );
        datachek = Helper.stringToData( datachek_ );
        //String datachek = (date.getDate()-1)+"-"+(date.getMonth()+1)+"-"+c.get( Calendar.YEAR );

        noteRef_addWork_Full.whereLessThanOrEqualTo( "date", datachek ).orderBy( "date", Query.Direction.ASCENDING ).get( Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );
                            check.add(main_work.getZarabotanoFinal());

                        }

                        if (check.size()<1) {
                            showNoInfoDialog();

                        } else {

                            checkDataForYear();
                            rv(monthSelected,yearSelected);

                        }

                    }
                } );




    }}; new Thread(run).start();

    }

    private void updateInfoAferDelete(int monthSelected, int yearSelected){

        allSumm.clear();
        allSumm_pay.clear();
        allSumm_Nopay.clear();
        allPay.clear();

        Date date = new Date(System.currentTimeMillis());

        if(yearSelected == (date.getYear()+1900)) {
            if ((monthSelected+1) == (date.getMonth()+1)){

                datachek_1 = "01"+"-"+(monthSelected+1)+"-"+yearSelected;
                datachek_2 = (date.getDate()-1)+"-"+(monthSelected+1)+"- "+yearSelected;

                date_start = Helper.stringToData( datachek_1 );
                date_end = Helper.stringToData( datachek_2 );

            } if((monthSelected+1) != (date.getMonth()+1)) {
                Calendar c = new GregorianCalendar();
                c.set(Calendar.YEAR, yearSelected);
                c.set(Calendar.MONTH, monthSelected);
                int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                datachek_1 = "01"+"-"+(monthSelected+1)+"-"+yearSelected;
                datachek_2 = maximum+"-"+(monthSelected+1)+"-"+yearSelected;

                date_start = Helper.stringToData( datachek_1 );
                date_end = Helper.stringToData( datachek_2 );
            }
        } if(yearSelected != (date.getYear()+1900)) {

            Calendar c = new GregorianCalendar();
            c.set(Calendar.YEAR, yearSelected);
            c.set(Calendar.MONTH, monthSelected);
            int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            datachek_1 = "01"+"-"+(monthSelected+1)+"-"+yearSelected;
            datachek_2 = maximum+"-"+(monthSelected+1)+"-"+yearSelected;

            date_start = Helper.stringToData( datachek_1 );
            date_end = Helper.stringToData( datachek_2 );
        }

        noteRef_addWork_Full.whereGreaterThanOrEqualTo( "date", date_start ).whereLessThanOrEqualTo( "date", date_end ).orderBy( "date", Query.Direction.ASCENDING ).get( Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );

                            if(main_work.getStatus()==true){
                                allSumm.add( main_work.getZarabotanoFinal());
                                allSumm_pay.add( main_work.getZarabotanoFinal());
                            }
                            if(main_work.getStatus()==false){
                                allSumm.add( main_work.getZarabotanoFinal());
                                allSumm_Nopay.add(main_work.getZarabotanoFinal());
                            }
                        }
                        if(allSumm.size()<1){
                            TextView nothing = (TextView) root.findViewById( R.id.textView_nothing_to_show );
                            nothing.setVisibility( View.VISIBLE );}
                        if(allSumm.size()>=1){
                            TextView nothing = (TextView) root.findViewById( R.id.textView_nothing_to_show );
                            nothing.setVisibility( View.INVISIBLE );}

                        String all_zarabotano =  String.valueOf(DashboardFragment.sum(allSumm));
                        String vyplacheno =  String.valueOf(DashboardFragment.sum(allSumm_pay));
                        String neVyplacheno =  String.valueOf(DashboardFragment.sum(allSumm_Nopay));

                        TextView no_pay = (TextView) root.findViewById( R.id.needPay_id );
                        no_pay.setText(neVyplacheno);
                        if(DashboardFragment.sum(allSumm_Nopay)!=0.0){
                            no_pay.setTextColor( getResources().getColor( R.color.red_lite ) );
                        }
                        if(DashboardFragment.sum(allSumm_Nopay)==0.0){
                            no_pay.setTextColor( getResources().getColor( R.color.colorSecondaryText ) );
                        }

                        TextView vsego = (TextView) root.findViewById( R.id.zarabotano_id );
                        vsego.setText( all_zarabotano );

                        TextView dhw = (TextView) root.findViewById( R.id.day_hard_work );
                        dhw.setText( Integer.toString( allSumm.size() ) );

                        TextView oplacheno = (TextView) root.findViewById(R.id.vyplacheno_id);
                        oplacheno.setText( vyplacheno );

                    }

                });



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

    private void rv(int monthSelected, int yearSelected) {

        allSumm.clear();
        allSumm_pay.clear();
        allSumm_Nopay.clear();
        allPay.clear();

        Date date = new Date(System.currentTimeMillis());
        int mon = monthSelected+1;
        System.out.println("mon"+mon);
        int dat = date.getMonth()+1;

        //System.out.println("monthSelected + 1  "+mon + "   "+"date_get month +1  "+dat);
        //System.out.println("monthSelected   "+monthSelected + "   "+"date_get month  "+date.getMonth());
        if(yearSelected == (date.getYear()+1900)) {
            if ((mon) == (dat)){

                datachek_1 = "01"+"-"+(mon)+"-"+yearSelected;
                datachek_2 = (date.getDate()-1)+"-"+mon+"- "+yearSelected;

                date_start = Helper.stringToData( datachek_1 );
                date_end = Helper.stringToData( datachek_2 );
                System.out.println("C0");

            }
            if(mon != dat )
             {
                 if(mon<dat){
                     Calendar c = Calendar.getInstance();
                     c.set(Calendar.YEAR, yearSelected);
                     c.set(Calendar.MONTH, monthSelected);
                     int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                     System.out.println(maximum);

                     datachek_1 = "01"+"-"+(mon)+"-"+yearSelected;
                     datachek_2 = maximum+"-"+(mon)+"-"+yearSelected;

                     date_start = Helper.stringToData( datachek_1 );
                     date_end = Helper.stringToData( datachek_2 );
                     System.out.println("C1");
                 }
                 if (mon>dat){
                     datachek_1 = "00"+"-"+00+"-"+yearSelected;
                     datachek_2 =00+"-"+00+"-"+yearSelected;

                     date_start = Helper.stringToData( datachek_1 );
                     date_end = Helper.stringToData( datachek_2 );
                     System.out.println("C2");

                 }

            }
        }
        if(yearSelected != (date.getYear()+1900)) {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, yearSelected);
            c.set(Calendar.MONTH, monthSelected);
            int maximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            datachek_1 = "01"+"-"+(mon)+"-"+yearSelected;
            datachek_2 = maximum+"-"+(mon)+"-"+yearSelected;

            date_start = Helper.stringToData( datachek_1 );
            date_end = Helper.stringToData( datachek_2 );
        }

        noteRef_addWork_Full.whereGreaterThanOrEqualTo( "date", date_start ).whereLessThanOrEqualTo( "date", date_end ).orderBy( "date", Query.Direction.ASCENDING ).get(Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );



                            if(main_work.getStatus()==true){
                                allSumm.add( main_work.getZarabotanoFinal());
                                allSumm_pay.add( main_work.getZarabotanoFinal());
                            }
                            if(main_work.getStatus()==false){
                                allSumm.add( main_work.getZarabotanoFinal());
                                allSumm_Nopay.add(main_work.getZarabotanoFinal());
                            }
                        }
                        if(allSumm.size()<1){
                            TextView nothing = (TextView) root.findViewById( R.id.textView_nothing_to_show );
                            nothing.setVisibility( View.VISIBLE );}
                        if(allSumm.size()>=1){
                            TextView nothing = (TextView) root.findViewById( R.id.textView_nothing_to_show );
                            nothing.setVisibility( View.INVISIBLE );}

                        String all_zarabotano =  String.valueOf(DashboardFragment.sum(allSumm));
                        String vyplacheno =  String.valueOf(DashboardFragment.sum(allSumm_pay));
                        String neVyplacheno =  String.valueOf(DashboardFragment.sum(allSumm_Nopay));

                        TextView no_pay = (TextView) root.findViewById( R.id.needPay_id );
                        no_pay.setText(neVyplacheno);
                        if(DashboardFragment.sum(allSumm_Nopay)!=0.0){
                            try {
                                no_pay.setTextColor( getResources().getColor( R.color.red_lite ) );
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        if(DashboardFragment.sum(allSumm_Nopay)==0.0){
                            no_pay.setTextColor( getResources().getColor( R.color.colorSecondaryText ) );
                        }

                        TextView vsego = (TextView) root.findViewById( R.id.zarabotano_id );
                        vsego.setText( all_zarabotano );

                        TextView dhw = (TextView) root.findViewById( R.id.day_hard_work );
                        dhw.setText( Integer.toString( allSumm.size() ) );

                        TextView oplacheno = (TextView) root.findViewById(R.id.vyplacheno_id);
                        oplacheno.setText( vyplacheno );

                    }

                });

        rv2(date_start, date_end );


    }

    private void rv2(Date date_start, Date date_end) {

        Query query = noteRef_addWork_Full.whereGreaterThanOrEqualTo( "date", date_start ).whereLessThanOrEqualTo( "date", date_end ).orderBy( "date", Query.Direction.ASCENDING );
        FirestoreRecyclerOptions<MainWork> options = new FirestoreRecyclerOptions.Builder<MainWork>()
                .setQuery(query, MainWork.class)
                .build();
        adapter = new StatisticaRv( options );
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.month_year_rv_id);
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        recyclerView.setAdapter( adapter );
        adapter.startListening();

        adapter.setOnItemClickListener( new StatisticaRv.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                MainWork mainWork = documentSnapshot.toObject( MainWork.class );
                System.out.println(mainWork.getTempalte_type() +" "+ mainWork.getName());
                startFragment(mainWork, documentSnapshot);
            }
        } );

        new ItemTouchHelper( new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return 0.6f;
            }


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
                switch (direction) {

                    case ItemTouchHelper.RIGHT:
                        System.out.println( "RIGHT" );
                        add_to_undelete_data(position);
                        delete_item(position);
                        showUndoSnackbar();
                        checkNotEmpty();
//
//                        String id___ = adapter.get_id( position );
//                        System.out.println("id1: " + id___ );
//                        alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);
//                        Intent my_intent = new Intent(CalendarMainActivity.this, AlarmResiver.class);
//                        my_intent.putExtra( "jobId", id___);
//                        Integer uniqId = adapter.getItem( position ).getUniqId();
//                        pendingIntent = pendingIntent.getBroadcast( CalendarMainActivity.this, uniqId, my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
////                                alarmManager.set(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent);
//                        alarmManager.cancel(pendingIntent);
////
//                        Toast.makeText( CalendarMainActivity.this,getString( R.string.AlarmCancel ),Toast.LENGTH_SHORT ).show();
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);

                        break;

                }}



            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightActionIcon( R.drawable.del_icon_red )
                        .addSwipeRightBackgroundColor( ContextCompat.getColor( getContext(), R.color.clear ))
                        .create()
                        .decorate();
                super.onChildDraw( c, recyclerView, viewHolder, dX/5, dY, actionState, isCurrentlyActive );


            }

        } ).attachToRecyclerView( recyclerView );


    }

    private void startFragment(MainWork mainWork, DocumentSnapshot jobSnapshot) {

        String priceFix = String.valueOf( mainWork.getPrice_fixed() );
        String priceHour = String.valueOf( mainWork.getPrice_hour() );
        String priceSm = String.valueOf( mainWork.getPrice_smena() );
        String durationSm = String.valueOf( mainWork.getSmena_duration() );
        String overTimeProcent = String.valueOf( mainWork.getOvertime_pocent() );
        String startTime = String.valueOf( mainWork.getStart() );
        String endTime = String.valueOf( mainWork.getEnd() );
        String finalCost = String.valueOf( mainWork.getZarabotanoFinal() );
        String uid = String.valueOf( mainWork.getUniqId() );
        String name = mainWork.getName();
        String date_ = String.valueOf( Helper.dataToString( mainWork.getDate() ) );
        String description = mainWork.getDiscription();
        String valuta = mainWork.getValuta();
        String status = String.valueOf( mainWork.getStatus() );
        String alarm;
        String rounded_nimutes = String.valueOf( mainWork.getRounded_minut() );
        String half_shiht = String.valueOf( mainWork.getHalf_shift() );
        String half_shiht_hours = String.valueOf( mainWork.getHalf_shift_hours() );
        String documentName = jobSnapshot.getId();

        String jobType = mainWork.getTempalte_type();

        switch (jobType) {

            case "fixed":

                FixedFragmentReviewTest fixedFragmentReviewTest= new FixedFragmentReviewTest();

                Bundle sendNameToFixedFragment = new Bundle();
                sendNameToFixedFragment.putString( "uid", uid );
                sendNameToFixedFragment.putString( "name", name );
                sendNameToFixedFragment.putString("price", priceFix );
                sendNameToFixedFragment.putString( "date", date_ );
                sendNameToFixedFragment.putString( "description", description );
                sendNameToFixedFragment.putString( "valuta", valuta );
                sendNameToFixedFragment.putString( "status", status );
                //sendNameToFixedFragment.putString( "alarm", alarm );
                sendNameToFixedFragment.putString( "documentName", documentName );
                sendNameToFixedFragment.putString( "fromFragment", "true" );

                fixedFragmentReviewTest.setArguments( sendNameToFixedFragment );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, fixedFragmentReviewTest, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;

            case "for smena":

                SmenaFragmentReviewTest smenaFragmentReviewTest= new SmenaFragmentReviewTest();

                Bundle sendNameToSmenaFragment = new Bundle();
                sendNameToSmenaFragment.putString( "uid", uid );
                sendNameToSmenaFragment.putString( "name", name );
                sendNameToSmenaFragment.putString( "priceShift", priceSm );
                sendNameToSmenaFragment.putString( "durationSm", durationSm );
                sendNameToSmenaFragment.putString( "overTimeProcent", overTimeProcent );
                sendNameToSmenaFragment.putString( "startTime", startTime );
                sendNameToSmenaFragment.putString( "endTime", endTime );
                sendNameToSmenaFragment.putString( "finalCost", finalCost );
                sendNameToSmenaFragment.putString( "date", date_ );
                sendNameToSmenaFragment.putString( "description", description );
                sendNameToSmenaFragment.putString( "valuta", valuta );
                sendNameToSmenaFragment.putString( "status", status );
                //sendNameToSmenaFragment.putString( "alarm", alarm );
                sendNameToSmenaFragment.putString( "documentName", documentName );
                sendNameToSmenaFragment.putString( "rounded_nimutes", rounded_nimutes );
                sendNameToSmenaFragment.putString( "half_shiht", half_shiht );
                sendNameToSmenaFragment.putString( "half_shiht_hours", half_shiht_hours );
                sendNameToSmenaFragment.putString( "fromFragment", "true" );

                smenaFragmentReviewTest.setArguments( sendNameToSmenaFragment );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, smenaFragmentReviewTest, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;

            case "for hour":

                HourFragmentReviewTest hourFragmentReviewTest= new HourFragmentReviewTest();
                Bundle sendNameToOtherFragment = new Bundle();
                sendNameToOtherFragment.putString( "uid", uid );
                sendNameToOtherFragment.putString( "name", name );
                sendNameToOtherFragment.putString( "priceHour", priceHour );
                sendNameToOtherFragment.putString( "startTime", startTime );
                sendNameToOtherFragment.putString( "endTime", endTime );
                sendNameToOtherFragment.putString( "finalCost", finalCost );
                sendNameToOtherFragment.putString( "date", date_ );
                sendNameToOtherFragment.putString( "description", description );
                sendNameToOtherFragment.putString( "valuta", valuta );
                sendNameToOtherFragment.putString( "status", status );
                //sendNameToOtherFragment.putString( "alarm", alarm );
                sendNameToOtherFragment.putString( "documentName", documentName );
                sendNameToOtherFragment.putString( "rounded_nimutes", rounded_nimutes );
                sendNameToOtherFragment.putString( "fromFragment", "true" );

                hourFragmentReviewTest.setArguments( sendNameToOtherFragment );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, hourFragmentReviewTest, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;

        }

    }

    private void add_to_undelete_data(Integer position) {

        ds = adapter.getSnapshots().getSnapshot( position );

    }

    private void delete_item(int position) {
        adapter.delete( position );

        updateInfoAferDelete(monthSelected,yearSelected);
        dataToDrawer();
        adapter.notifyItemRemoved( position );
        adapter.notifyDataSetChanged();

    }

    private void dataToDrawer() {


        Runnable datatodrawer = new Runnable() {
            public void run() {

        Date date1 = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date1 );
        Date date_ok = Helper.stringToData( d1 );
        ArrayList<Float> allSumm = new ArrayList<>();
        ArrayList<Float> allSummP = new ArrayList<>();
        allSumm.clear();
        allSummP.clear();

        sum_PayAll = 0;
        sum_noPay = 0;

        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING ).get( Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );
                            allSumm.add( main_work.getZarabotanoFinal());

                        }
//                        System.out.println( allSumm );
                        sum_noPay=0;
                        for(int i=0; i<allSumm.size(); i++) {
                            sum_noPay=sum_noPay+ allSumm.get( i );
                        }
                        TextView nopay = (TextView) getActivity().findViewById(R.id.header_noPay_id_textView);
                        nopay.setText( Float.toString(sum_noPay) );

                    }


                } );


    }
        };
        new Thread(datatodrawer).start();

    }
    private void showUndoSnackbar() {

        String undo = getString(R.string.UNDO);
        String itemdel = getString(R.string.Itemdeleted);
        Snackbar snackbar = Snackbar.make(getView(), itemdel, Snackbar.LENGTH_LONG);
        snackbar.setAction(undo, v ->undo(position));
        snackbar.show();

    }

    private void undo(int position) {
        MainWork main_work = ds.toObject( MainWork.class);
        noteRef_addWork_Full.document().set(main_work);

        updateInfoAferDelete(monthSelected,yearSelected);
        dataToDrawer();

    }
        String getNameOfMonth(int month, Locale locale) throws IllegalArgumentException {

        Calendar c;
        String s;
        String n;


        try {
            c=Calendar.getInstance();
            c.set(Calendar.MONTH,month);
            c.set( Calendar.YEAR, ySelectPicker );
            System.out.println(c);
            s=c.getDisplayName(Calendar.MONTH,Calendar.LONG,locale);
            System.out.println("INT MONTH" + month + " DispName "+s);


            if(s.equals( "января" )){
                n = "Январь";
                return n;}
            if(s.equals( "февраля" )){
                n = "Февраль";
                return n; }
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

    private void setNubmerPicker(NumberPicker nubmerPicker,String [] numbers ){
        nubmerPicker.setMaxValue(numbers.length-1);
        nubmerPicker.setMinValue(0);
        nubmerPicker.setWrapSelectorWheel(true);
        nubmerPicker.setDisplayedValues(numbers);
    }

    private void setDividerColor(android.widget.NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = android.widget.NumberPicker.class.getDeclaredFields();
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

    private void checkDataForYear() {

        ArrayList<Integer> year_to_choose = new ArrayList<>();
        noteRef_addWork_Full.get( Source.CACHE)

                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){



                            MainWork main_work = documentSnapshot.toObject( MainWork.class );

                            //System.out.println( "___" + main_work.getDate() );

                            if(!year_to_choose.contains( (main_work.getDate().getYear())+1900 )){
                                year_to_choose.add((main_work.getDate().getYear())+1900);
                            } else {
                                continue;
                            }
                        }
                        if (year_to_choose.size()<1){
                            System.out.println( "EXEPTEIO" );

                        } else {
                            min_year = (Collections.min(year_to_choose));
                            max_year = (Collections.max(year_to_choose));}

                    }
                });
    }

    private void showNoInfoDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( getContext() );
        LayoutInflater inflater = LayoutInflater.from( getContext() );
        final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
        builder.setView( regiserWindow );
        final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
        shapka_txt.setText( getContext().getResources().getString( R.string.Statistics) );
        final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
        txt_txt.setText(  getContext().getResources().getString( R.string.no_statistick_alert ) );
        final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setCancelable( false );
        dialog.show();
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent( getContext(), CalendarMainActivity.class));
                dialog.dismiss();
                getActivity().getFragmentManager().popBackStack();

            } });

    }

    private void isFromIntent() {


        String year = "null";
        String month;

        Bundle getNameFromFragment = this.getArguments();
        if(getNameFromFragment != null) {

            year = getNameFromFragment.getString( "year" );
            month = getNameFromFragment.getString( "month" );

            if(year!=null){
                fromFragment = true;
                Locale locale = Resources.getSystem().getConfiguration().locale;

                yearSelected = Integer.parseInt( year );
                monthSelected = Integer.parseInt( month );

                month_txt.setText( getNameOfMonth( Integer.parseInt(month), locale ) );
                year_txt.setText(Integer.toString(Integer.parseInt( year )));

                checkDataForYear();
                rv(Integer.parseInt(month),Integer.parseInt(year));

            } else {


//                tvJobName.setText( getName() );
//                setSpinnerYear(getName());
//
//                Calendar todayDate = Calendar.getInstance();
//                String todayYear = String.valueOf( todayDate.get(Calendar.YEAR) );
//                String todayMonth = month = Helper.getNameOfMonth(todayDate.get( Calendar.MONTH ), locale);
//                setMonthDate( todayMonth, todayYear );


            }

        } else {

            Locale locale = Resources.getSystem().getConfiguration().locale;
            Calendar calendar = Calendar.getInstance();
            yearSelected = calendar.get(Calendar.YEAR);
            monthSelected = calendar.get(Calendar.MONTH);
            month_txt.setText( getNameOfMonth( monthSelected, locale ) );
            year_txt.setText(Integer.toString(yearSelected));

            checkNotEmpty();

        }
    }



}
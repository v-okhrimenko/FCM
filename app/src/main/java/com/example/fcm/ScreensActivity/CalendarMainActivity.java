package com.example.fcm.ScreensActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.R;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.models.TemplateJob;
import com.example.fcm.models.UserInfoToFirestore;
import com.example.fcm.mycalendar.CalendarView;
import com.example.fcm.mycalendar.CalendarViewGridAdapter;
import com.example.fcm.other.AlarmResiver;
import com.example.fcm.other.NumberPicker;
import com.example.fcm.rateFixed.AddFixedRate;
import com.example.fcm.rateFixed.FixedJobReview;
import com.example.fcm.rateHour.AddHourRate;
import com.example.fcm.rateHour.ForHourJobReview;
import com.example.fcm.rateSmena.AddSmenaRate;
import com.example.fcm.rateSmena.ForSmenaJobReview;
import com.example.fcm.recycleviewadapter.CalendarWorkRv;
import com.example.fcm.recycleviewadapter.TemplateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;




public class CalendarMainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

//    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Avatar");

    private final int RequestCameraPermissionId = 1;
    private float sum_noPay;
    //private float sum_PayAll;


    //private FloatingActionButton newWork;

    private int STORAGE_PERMISSION_CODE = 1;
    //private Object selectedImagePath;
    private CalendarViewGridAdapter adapter_cal;
    
    //ЧАСЫ И НАПОМИНАНИЯ
//    private int h;
//    private int m;
//    private boolean remind1DayChek = false;
//    private boolean remindCustomDayChek = false;
//    private Integer remindSetCustomDay;
////    private Date alarmFromDate;
//    private String a1txt = "";

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    //private int notId = 1;
    private CalendarView cw;
    private ImageView fab_show_menu, fab_shift,fab_hour, fab_fixed, fab_template;
    //FloatingActionButton fab_template;
    private Boolean isOpen=false;
    private TextView tv_fixed_txt, tv_hour_txt, tv_shift_txt, tv_template;
    private ConstraintLayout cl_main;
    private ConstraintLayout cl_disable_rv;
    private ConstraintLayout bg_rv;


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private boolean showTemplateInFloatActionButtonMenu = true;
    private Context context;
    private boolean blurTrue;


    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();

    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");
    private CollectionReference noteRef_full = db_fstore.collection( user.getUid() ).document( "My DB" ).collection( "Jobs_full" );

    DocumentSnapshot ds;


    private RealtimeBlurView blurView, blurViewAll;
    public static CalendarWorkRv adapter;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int position;
//    private ImageButton menu;
//
//    Integer uniq_ud;
//    String name_ud, desc_ud;
//    Integer price_ud;
//    Date date_ud;
//    Boolean chek_ud;
    public static String[] test = new String[2];
    //DataToDrawer fh = new DataToDrawer();

    private RecyclerView recyclerView;

    private final int Pick_image = 1;
    private final int Load_image = 2;
    private CircleImageView avatar;
    //String img_;

    private String emailname;

    private CalendarViewGridAdapter calendarViewGridAdapter;

    private ArrayList <String> check_two_or_more_event = new ArrayList<>();
    public static ArrayList <String> two_or_more_event = new ArrayList<>();
    private TemplateAdapter adapter_new;

    private TextView incompleatEvents;
    private TextView noPayEvents;

    private ImageButton showDrawer;

    private void localeSet() {

        Locale locale = Resources.getSystem().getConfiguration().locale;
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().
                    updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_calendar_main );

//        View decorView = getWindow().getDecorView();
//        Helper.hideSystemUI( decorView );

        showDrawer = findViewById( R.id.ib_showDrawer );

        context = CalendarMainActivity.this;
        blurView = findViewById( R.id.blurview );
        blurViewAll = findViewById( R.id.blurview_all );

        fab_fixed =findViewById( R.id.fab1 );
        fab_hour =findViewById( R.id.fab2 );
        fab_shift =findViewById( R.id.fab3 );
        fab_template =findViewById( R.id.fab_template );

        fab_show_menu =findViewById( R.id.fab4 );
        fab_show_menu.setClickable( true );
        tv_fixed_txt =findViewById(R.id.tv_fixed_test);
        tv_hour_txt =findViewById(R.id.tv_hour_test);
        tv_shift_txt =findViewById(R.id.tv_smena_test);
        tv_template =findViewById( R.id.tv_template_test );
        cl_disable_rv = findViewById( R.id.cl_black_test );
        cl_main = findViewById( R.id.cl_main );

        bg_rv = findViewById( R.id.bg_rv );

        tv_fixed_txt.setClickable( false );
        tv_hour_txt.setClickable( false );
        tv_shift_txt.setClickable( false );
        tv_template.setClickable( false );

        blurView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen=false;
                closeMenu();
            }
        } );
        tv_fixed_txt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen=false;
                startActivity(new Intent( CalendarMainActivity.this, AddFixedRate.class));
//                customType(CalendarMainActivity.this,"fadein-to-fadeout");
                overridePendingTransition(0, 0);
                finish();

            }
        } );
        tv_hour_txt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen=false;

                startActivity(new Intent( CalendarMainActivity.this, AddHourRate.class));
//                customType(CalendarMainActivity.this,"fadein-to-fadeout");
                overridePendingTransition(0, 0);
                finish();

            }
        } );
        tv_shift_txt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( CalendarMainActivity.this, AddSmenaRate.class));
//                customType(CalendarMainActivity.this,"fadein-to-fadeout");
                overridePendingTransition(0, 0);
                finish();

            }
        } );
        tv_template.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen=false;
                showTemplate();
                closeMenu();
            }
        } );

        cl_disable_rv.setAlpha( 0 );
        cl_disable_rv.setClickable( false );
        cl_disable_rv.setFocusable( false );

        fab_template.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen=false;
                showTemplate();
                closeMenu();
            }
        } );
        fab_shift.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( CalendarMainActivity.this, AddSmenaRate.class));
//                customType(CalendarMainActivity.this,"fadein-to-fadeout");
                overridePendingTransition(0, 0);
                finish();
            }
        } );
        fab_hour.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen=false;

                startActivity(new Intent( CalendarMainActivity.this, AddHourRate.class));
//                customType(CalendarMainActivity.this,"fadein-to-fadeout");
                overridePendingTransition(0, 0);
                finish();

            }
        } );
        fab_fixed.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen=false;

                startActivity(new Intent( CalendarMainActivity.this, AddFixedRate.class));
//                customType(CalendarMainActivity.this,"fadein-to-fadeout");
                overridePendingTransition(0, 0);
                finish();
            }
        } );
        fab_show_menu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!isOpen){
                   openMenu();

               } else {
                   closeMenu();
                   

               }
            }
        } );

        recyclerView = findViewById( R.id.main_work_rv );



        checkPerm();

        cw = findViewById( R.id.mainCalendarView );
        cw.CollectEventMonth();
        updateList();

//        Calendar calendarToday = Calendar.getInstance();
//
//
//        setCalendarCurrent( calendarToday.get( Calendar.YEAR ), calendarToday.get( Calendar.MONTH ) );
        //setCalendar();


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        avatar = (CircleImageView) headerView.findViewById(R.id.profile_image);
        incompleatEvents = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.need_finish));

        noPayEvents= (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.not_pay));
        drawerLayout = findViewById(R.id.Drawer_layo);

        navigationView = findViewById(R.id.navigationView);
        //navigationView.setItemIconTintList( null );

        showDrawer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer( Gravity.LEFT );
            }
        } );

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case  R.id.statistika:
                        item.setChecked( true );
                        startActivity(new Intent( CalendarMainActivity.this, StatisticActivity.class));
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.need_finish:
                        item.setChecked( true );
                        startActivity(new Intent( CalendarMainActivity.this, OldNoFinishActivity.class));
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.nastroiki:
                        item.setChecked( true );
                        startActivity(new Intent( CalendarMainActivity.this, SettingsActivity.class));
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.not_pay:
                        item.setChecked( true );
                        startActivity(new Intent( CalendarMainActivity.this, NoPayActivity.class));
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;


                    case R.id.calendar_work:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;


                    case R.id.new_work:
                        item.setChecked(true);
                        startActivity(new Intent( CalendarMainActivity.this, AddJobActivity_new.class));
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.add_job:
                        item.setChecked(true);
                        startActivity(new Intent( CalendarMainActivity.this, AddTemplateActivity.class));
//                        customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.exit_to_login:
                        item.setChecked(true);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent( CalendarMainActivity.this, MainActivity.class));
//                        customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                }

                return false;
            }
        });
    }


    private void showTemplate() {

        if (showTemplateInFloatActionButtonMenu){

            //blurViewAll.setVisibility( View.VISIBLE );
            AlertDialog.Builder builder = new AlertDialog.Builder( CalendarMainActivity.this);
            LayoutInflater inflater = LayoutInflater.from( CalendarMainActivity.this);
            final View regiserWindow = inflater.inflate(R.layout.template_inflter, null);
            builder.setView(regiserWindow);

            View decorView = regiserWindow.getRootView();
            Helper.hideSystemUI( decorView );

            final RecyclerView recyclerView_new = regiserWindow.findViewById( R.id.recWieJobs_id_new );
            final Button cancel = regiserWindow.findViewById( R.id.btn_close_tamplate_rv );

            final AlertDialog dialog = builder.create();
            dialog.setCancelable( false );
            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            //dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            //dialog.getWindow().setDimAmount(0.0f);
            dialog.show();

            cancel.setOnClickListener( v1 -> {
                blurViewAll.setVisibility( View.INVISIBLE );
                dialog.dismiss();} );

            Query query = noteRef_full.orderBy( "template_name", Query.Direction.DESCENDING );;
            FirestoreRecyclerOptions<TemplateJob> options = new FirestoreRecyclerOptions.Builder<TemplateJob>()
                    .setQuery(query, TemplateJob.class)
                    .build();
            adapter_new = new TemplateAdapter( options );
            adapter_new.startListening();
            recyclerView_new.setHasFixedSize( true );
            recyclerView_new.setLayoutManager( new LinearLayoutManager( getBaseContext() ) );
            recyclerView_new.setAdapter( adapter_new );

            adapter_new.setOnItemClickListener( new TemplateAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    TemplateJob tmp = documentSnapshot.toObject( TemplateJob.class );

                    String jobType = tmp.getTempalte_type();
                    switch (jobType){
                        case "fixed":
                            Intent intent = new Intent( CalendarMainActivity.this, AddFixedRate.class);

                            intent.putExtra("isFromTemplate", "true");
                            intent.putExtra("name", tmp.getTemplate_name());
                            intent.putExtra("price", String.valueOf(tmp.getPrice_fixed()) );
                            intent.putExtra("valuta", tmp.getValuta() );

                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;

                        case "for smena":

                            Intent intent_forSmena = new Intent( CalendarMainActivity.this, AddSmenaRate.class);
                            intent_forSmena.putExtra("isFromTemplate", "true");
                            intent_forSmena.putExtra("name", tmp.getTemplate_name());
                            intent_forSmena.putExtra("priceShift", String.valueOf( tmp.getPrice_smena() ));
                            intent_forSmena.putExtra("durationSm", String.valueOf( tmp.getSmena_duration() ));
                            intent_forSmena.putExtra("overTimeProcent", String.valueOf( tmp.getOvertime_pocent() ));
                            intent_forSmena.putExtra("valuta", tmp.getValuta() );
                            intent_forSmena.putExtra("rounded_nimutes", String.valueOf( tmp.getRounded_minutes() ) );
                            intent_forSmena.putExtra("half_shiht", String.valueOf( tmp.getHalf_shift() ) );
                            intent_forSmena.putExtra("half_shiht_hours",String.valueOf( tmp.getHalf_shift_hours() ) );

                            startActivity(intent_forSmena);
                            overridePendingTransition(0, 0);
                            break;
                        case "for hour":

                            Intent intent_forHour = new Intent( CalendarMainActivity.this, AddHourRate.class);
                            intent_forHour.putExtra("isFromTemplate", "true");
                            intent_forHour.putExtra("name", tmp.getTemplate_name());
                            intent_forHour.putExtra("priceHour", String.valueOf( tmp.getPrice_hour() ));
                            intent_forHour.putExtra("valuta", tmp.getValuta() );
                            intent_forHour.putExtra("rounded_nimutes", String.valueOf( tmp.getRounded_minutes() ) );

                            startActivity(intent_forHour);
                            overridePendingTransition(0, 0);
                            break;
                    }

                }
            } );

        } else {
            Helper.showInfoNoTemplatePresent( context );
        }


    }

    private void closeMenu() {

        tv_fixed_txt.setClickable( false );
        tv_hour_txt.setClickable( false );
        tv_shift_txt.setClickable( false );
        tv_template.setClickable( false );

        tv_fixed_txt.setVisibility( View.GONE );
        tv_hour_txt.setVisibility( View.GONE  );
        tv_shift_txt.setVisibility( View.GONE  );
        tv_template.setVisibility( View.GONE  );

        isOpen=false;
        tv_fixed_txt.setAlpha( 0 );
        tv_hour_txt.setAlpha( 0 );
        tv_shift_txt.setAlpha( 0 );
        tv_template.setAlpha( 0 );

        //blurView.setVisibility( View.INVISIBLE );
        cl_disable_rv.setAlpha( 0 );
        cl_disable_rv.setClickable( false );
        cl_disable_rv.setFocusable( false );

        fab_show_menu.animate().rotation( 0 );
        //fab_show_menu.setImageDrawable( getDrawable( R.drawable.buttonaddtest ) );
        fab_fixed.animate().translationY( 0 ).rotation( 0 );
        fab_hour.animate().translationY( 0 ).rotation( 0 );
        fab_shift.animate().translationY( 0 ).rotation( 0 );
        fab_template.animate().translationY( 0 ).rotation( 0 ).setDuration( 300 );
        fab_template.animate().translationX( 0 ).rotation( 0 ).setDuration( 300 );

        tv_fixed_txt.animate().translationY( 0 ).rotation( 0 );
        tv_hour_txt.animate().translationY( 0 ).rotation( 0 );
        tv_shift_txt.animate().translationY( 0 ).rotation( 0 );
        tv_template.animate().translationY( 0 ).rotation( 0 );
    }

    private void openMenu() {

        tv_fixed_txt.setClickable( true );
        tv_hour_txt.setClickable( true );
        tv_shift_txt.setClickable( true );
        tv_template.setClickable( true );

        tv_fixed_txt.setVisibility( View.VISIBLE );
        tv_hour_txt.setVisibility( View.VISIBLE );
        tv_shift_txt.setVisibility( View.VISIBLE );
        tv_template.setVisibility( View.VISIBLE );

        cl_disable_rv.setOnClickListener( v -> closeMenu() );
        isOpen=true;
        //tv_template.animate().translationY( -565 ).setDuration(100 );
        tv_fixed_txt.animate().translationY( -430 ).setDuration(300 );
        tv_fixed_txt.setAlpha( 1 );
        tv_hour_txt.animate().translationY( -295 ).setDuration(200 );
        tv_hour_txt.setAlpha( 1 );
        tv_shift_txt.animate().translationY( -160 ).setDuration(100 );
        tv_shift_txt.setAlpha( 1 );
        tv_template.animate().translationX( -160 ).setDuration(300 );
        tv_template.setAlpha( 1 );

        cl_disable_rv.setAlpha( 1 );
        cl_disable_rv.setClickable( true );
        cl_disable_rv.setFocusable( true );

        fab_show_menu.animate().rotation( 180 );
        //fab_show_menu.setImageDrawable( getDrawable( R.drawable.buttonaddpressedwhite ) );

        fab_fixed.animate().translationY( -430 ).rotation( 360 ).setDuration(300 );
        fab_hour.animate().translationY( -295 ).rotation( 360 ).setDuration(200 );
        fab_shift.animate().translationY( -160 ).rotation( 360 ).setDuration(100 );
        //fab_template.animate().translationY( -565 ).rotation( 360 ).setDuration(100 );
        fab_template.animate().translationX( -160 ).rotation( 360 ).setDuration(300 );



    }

    private void showBlur() {

        blurView.setVisibility( View.VISIBLE );
        blurTrue = true;
    }

//    public  void setCalendarCurrent (int year, int month) {
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month, 1);
//
//        int maxDay =  calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
//
//        Calendar cFirstDayInMonth = Calendar.getInstance();
//        cFirstDayInMonth.set( Calendar.YEAR,year  );
//        cFirstDayInMonth.set( Calendar.MONTH, month );
//        cFirstDayInMonth.set( Calendar.DAY_OF_MONTH, 1 );
//        cFirstDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
//        cFirstDayInMonth.set( Calendar.MINUTE,0 );
//        cFirstDayInMonth.set( Calendar.SECOND,0 );
//        cFirstDayInMonth.set( Calendar.MILLISECOND,0 );
//
//        Date firesDayInMonth = new Date();
//        firesDayInMonth.setTime( cFirstDayInMonth.getTimeInMillis() );
//
//        Calendar cLastDayInMonth = Calendar.getInstance();
//        cLastDayInMonth.set( Calendar.YEAR,year  );
//        cLastDayInMonth.set( Calendar.MONTH, month );
//        cLastDayInMonth.set( Calendar.DAY_OF_MONTH, maxDay );
//        cLastDayInMonth.set( Calendar.HOUR_OF_DAY,0 );
//        cLastDayInMonth.set( Calendar.MINUTE,0 );
//        cLastDayInMonth.set( Calendar.SECOND,0 );
//        cLastDayInMonth.set( Calendar.MILLISECOND,0 );
//
//        Date lastDayInMonth = new Date();
//        lastDayInMonth.setTime( cLastDayInMonth.getTimeInMillis() );
//
//        DbConnection.DBJOBS
//                .whereGreaterThanOrEqualTo( "date",firesDayInMonth )
//                .whereLessThanOrEqualTo( "date", lastDayInMonth ).get( Source.CACHE)
//                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
//
//                            MainWork mainWork = snapshot.toObject( MainWork.class);
//
//                            System.out.println(mainWork.getDate());
//
//                            Calendar c = Calendar.getInstance();
//                            c.setTime( mainWork.getDate() );
//                            String dateEvent = c.get( Calendar.DAY_OF_MONTH ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.YEAR );
//                            CalendarView.eventsList.add( new CalendarViewEvents( mainWork.getName(), dateEvent, mainWork.getStatus() ) );
//                            check_two_or_more_event.add( dateEvent );
//                            twoSets( check_two_or_more_event );
//
//                        }
//                        cw.CollectEventMonth();
//                    }
//                } );
//
//
//
//    }

//    private void setCalendar() {
//
//        CalendarView.eventsList.clear();
//        check_two_or_more_event.clear();
//        two_or_more_event.clear();
//
//        Runnable run = new Runnable() {
//            public void run() {
//
////                CalendarView.eventsList.clear();
////                check_two_or_more_event.clear();
////                two_or_more_event.clear();
////
////
//                    noteRef_addWork_Full.get( Source.CACHE).addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
//                     @Override
//                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                         for(QueryDocumentSnapshot ds: queryDocumentSnapshots) {
//                             MainWork main_work = ds.toObject( MainWork.class );
////                            System.out.println( main_work.getDate() );
//
//                             Calendar c = Calendar.getInstance();
//                             c.setTime( main_work.getDate() );
//
//                             String dateEvent = c.get( Calendar.DAY_OF_MONTH ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.YEAR );
////                            System.out.println( dateEvent );
//                             CalendarView.eventsList.add( new CalendarViewEvents( main_work.getName(), dateEvent, main_work.getStatus() ) );
//                             ///CalendarView.eventsList.add( new CalendarViewEvents( main_work.getJobName(), dateEvent, main_work.getStatus() ) );
//                             check_two_or_more_event.add( dateEvent );
//////                            ArrayList <String> two_or_more_event = new ArrayList<>();
//                             twoSets( check_two_or_more_event );
//                         }
//                         cw.CollectEventMonth();
//                     }
//
//                 }
//                );
//
//
//                ///noteRef_addWork.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
//
//            }};
//        new Thread(run).start();
//
//
//    }
//
//    private static void twoSets(ArrayList<String> data)
//    {
//        Set<String> foundStrings = new HashSet<>();
//        Set<String> duplicates = new HashSet<>();
//        for (String str : data)
//        {
//            if (foundStrings.contains(str))
//            {
//                duplicates.add(str);
//                two_or_more_event.add( str );
//            }
//            else
//            {
//                foundStrings.add(str);
//            }
//        }
//
//    }

    @AfterPermissionGranted( 123 )
    private void checkPerm() {

        Runnable perm = new Runnable() {
            public void run() {

                if (ActivityCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( CalendarMainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCameraPermissionId );
                    return;
                }
                try {

                }
                catch (Exception e)
                {
                    Toast.makeText( CalendarMainActivity.this, e.getMessage(), Toast.LENGTH_SHORT ).show();
                }

            }};
        new Thread(perm).start();


    }

    private void dataToDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.email_id);
        navUsername.setText(auth.getCurrentUser().getEmail());
        emailname = auth.getCurrentUser().getEmail();

        Date date = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date );
        Date date_ok = Helper.stringToData( d1 );
        ArrayList<Float> allSumm = new ArrayList<>();
        ArrayList<Float> allSummP = new ArrayList<>();
        allSumm.clear();
        allSummP.clear();

        //sum_PayAll = 0f;
        sum_noPay = 0f;

        Date date_ = new Date(System.currentTimeMillis());
        String d1_ = Helper.dataToString( date_ );
        Date date_ok_ = Helper.stringToData( d1_ );

        noteRef_addWork_Full.whereLessThan("date", date_ok_ ).whereEqualTo( "needFinish", true )
                .whereEqualTo( "end",null ).orderBy( "date", Query.Direction.ASCENDING ).get(Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.size() != 0) {
                            incompleatEvents.setGravity( Gravity.CENTER_VERTICAL);
                            //incompleatEvents.
                            incompleatEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                            incompleatEvents.setTypeface(null, Typeface.BOLD);
                            incompleatEvents.setTextColor(getResources().getColor(R.color.white));
                            incompleatEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );

                            incompleatEvents.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY));
                            incompleatEvents.setText( String.valueOf( queryDocumentSnapshots.size() ) );

                        } else {
                            try {
                                incompleatEvents.setGravity( Gravity.CENTER_VERTICAL);
                                //incompleatEvents.
                                incompleatEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                                incompleatEvents.setTypeface(null, Typeface.BOLD);
                                incompleatEvents.setTextColor(getResources().getColor(R.color.white));
                                incompleatEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );

                                //incompleatEvents.setLayoutParams(new DrawerLayout.LayoutParams( DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
                                incompleatEvents.setBackground( getResources().getDrawable( R.drawable.clear , null) );
                                incompleatEvents.setText( "" );
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

        noteRef_data.get(Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {

                       UserInfoToFirestore userInfoToFirestore = documentSnapshot.toObject( UserInfoToFirestore.class );
                       try {
                           if(userInfoToFirestore.getNickname() == null || userInfoToFirestore.getNickname()=="") {
                               navUsername.setText(auth.getCurrentUser().getEmail());

                           } else {
                               navUsername.setText( userInfoToFirestore.getNickname());
                           }

                           if(userInfoToFirestore.getImg() != null){
                               final Uri myUri = Uri.parse( userInfoToFirestore.getImg() );
                               Picasso.with( CalendarMainActivity.this ).load( myUri ).into( avatar );

                           } else {
                               avatar.setImageResource( R.drawable.ic_account_circle_black_24dp );
                           }

                       }catch (Exception e) {
                           UserInfoToFirestore userInfoToFirestore1 = new UserInfoToFirestore();
                           userInfoToFirestore1.setNickname( "" );
                           userInfoToFirestore1.setMelody( "" );

                           noteRef_data.set( userInfoToFirestore1 );
                       }
                   }
               }
                );

        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING ).get(Source.CACHE)
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                    }
                } )
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            noPayEvents.setGravity( Gravity.CENTER_VERTICAL);
                            //incompleatEvents.
                            noPayEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                            noPayEvents.setTypeface(null, Typeface.BOLD);
                            noPayEvents.setTextColor(getResources().getColor(R.color.white));
                            noPayEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );
                            noPayEvents.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY));
                            noPayEvents.setText( String.valueOf( queryDocumentSnapshots.size() ) );

                            for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                MainWork main_work = documentSnapshot.toObject( MainWork.class );
                                allSumm.add( main_work.getZarabotanoFinal() );
                            }
                            sum_noPay=0;
                            if(allSumm.size()<1){
                                TextView nopay = (TextView) headerView.findViewById(R.id.header_noPay_id_textView);

                                nopay.setText( "0");

                            } else {
                                for(int i=0; i<allSumm.size(); i++) {
                                    sum_noPay=sum_noPay+ allSumm.get( i );
                                }
                                TextView nopay = (TextView) headerView.findViewById(R.id.header_noPay_id_textView);
                                nopay.setText( String.valueOf(sum_noPay ));

                            }

                        } else {

                            TextView nopay = (TextView) headerView.findViewById(R.id.header_noPay_id_textView);
                            nopay.setText( "0" );

                            try {
                                noPayEvents.setGravity( Gravity.CENTER_VERTICAL);
                                noPayEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                                noPayEvents.setTypeface(null, Typeface.BOLD);
                                noPayEvents.setTextColor(getResources().getColor(R.color.white));
                                noPayEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );

                                noPayEvents.setBackground( getResources().getDrawable( R.drawable.clear , null) );
                                noPayEvents.setText( "" );
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        }
                } );
    }

    public void updateList() {

        Runnable run = new Runnable() {
            public void run() {

                //      ИЩЕМ ПО ДАТЕ И СОРТИРУЕМ ПО ВОЗРАСТАНИЮ
                Date date = new Date(System.currentTimeMillis());
                String d1 = Helper.dataToString( date );
                Date date_ok = Helper.stringToData( d1 );


//        System.out.println( date_ok );
                //Query query = noteRef_addWork.whereGreaterThanOrEqualTo("date", date_ok ).orderBy( "date", Query.Direction.ASCENDING );
                Query query = noteRef_addWork_Full.whereGreaterThanOrEqualTo("date", date_ok ).orderBy( "date", Query.Direction.ASCENDING );

                //Query query2 = noteRef_addWork_Full.whereLessThanOrEqualTo( "date", date_ok  ).orderBy( "date", Query.Direction.ASCENDING );

                FirestoreRecyclerOptions<MainWork> options = new FirestoreRecyclerOptions.Builder<MainWork>()
                        .setQuery(query, MainWork.class)
                        .build();
                adapter = new CalendarWorkRv( options );
                RecyclerView recyclerView = findViewById( R.id.main_work_rv );
                recyclerView.setHasFixedSize( true );
                recyclerView.setLayoutManager( new LinearLayoutManager( getBaseContext() ) );
                recyclerView.setAdapter( adapter );
                //recyclerView.setLayoutAnimation( controller );
                //recyclerView.getAdapter().notifyDataSetChanged();

                //runAnimation(recyclerView, 0);

                new ItemTouchHelper( new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

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

                            case ItemTouchHelper.LEFT:

                                adapter.update( viewHolder.getAdapterPosition() );
                                adapter.notifyDataSetChanged();
                                //recyclerView.setAdapter(adapter);
                                //setCalendar();
                                cw.updateCalendar();

                                //dataToDrawer();
                                recyclerView.setAdapter(adapter);
                                break;
//
                            case ItemTouchHelper.RIGHT:
                                adapter.delete( position );


                                showUndoSnackbar(position);
                                //delete_item(position);
//                                adapter.notifyItemRemoved( position );
//                                add_to_undelete_data(position);

                                //delete_item(position);

                                //setCalendar();
                                cw.updateCalendar();
                                recyclerView.setAdapter(adapter);

                                String id___ = adapter.get_id( position );
                                //System.out.println("id1: " + id___ );
                                alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);
                                Intent my_intent = new Intent( CalendarMainActivity.this, AlarmResiver.class);
                                my_intent.putExtra( "jobId", id___);
                                Integer uniqId = adapter.getItem( position ).getUniqId();
                                pendingIntent = pendingIntent.getBroadcast( CalendarMainActivity.this, uniqId, my_intent, PendingIntent.FLAG_UPDATE_CURRENT );
//                                alarmManager.set(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent);
                                alarmManager.cancel(pendingIntent);
//
                                //Toast.makeText( CalendarMainActivity.this,getString( R.string.AlarmCancel ),Toast.LENGTH_SHORT ).show();

                                break;

                        }}

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                .addSwipeLeftBackgroundColor( ContextCompat.getColor( CalendarMainActivity.this, R.color.clear ) )
                                .addSwipeLeftActionIcon( R.drawable.ic_monetization )
                                .addSwipeRightActionIcon( R.drawable.del_icon_red )
                                .addSwipeRightBackgroundColor( ContextCompat.getColor( CalendarMainActivity.this, R.color.clear ))
                                .create()
                                .decorate();
                        super.onChildDraw( c, recyclerView, viewHolder, dX/5, dY, actionState, isCurrentlyActive );

                    }


                } ).attachToRecyclerView( recyclerView );
                adapter.setOnItemClickListener( new CalendarWorkRv.onItemClickListener() {

                    @Override
                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                        MainWork main_work = documentSnapshot.toObject( MainWork.class );
//                System.out.println(main_work.getUniqId());
//
                        String priceFix = String.valueOf(main_work.getPrice_fixed());
                        String priceHour = String.valueOf(main_work.getPrice_hour());
                        String priceSm = String.valueOf(main_work.getPrice_smena());
                        String durationSm = String.valueOf(main_work.getSmena_duration());
                        String overTimeProcent = String.valueOf(main_work.getOvertime_pocent());
                        String startTime = String.valueOf(main_work.getStart());
                        String endTime = String.valueOf(main_work.getEnd());
                        String finalCost = String.valueOf(main_work.getZarabotanoFinal());

                        String uid =String.valueOf( main_work.getUniqId());
                        String name = main_work.getName();
                        String date = String.valueOf(Helper.dataToString(main_work.getDate()));
                        String description = main_work.getDiscription();
                        String valuta = main_work.getValuta();
                        String status = String.valueOf(main_work.getStatus());
                        String alarm;
                        String rounded_nimutes = String.valueOf( main_work.getRounded_minut() );
                        String half_shiht = String.valueOf( main_work.getHalf_shift() );
                        String half_shiht_hours = String.valueOf( main_work.getHalf_shift_hours() );


                        String documentName = documentSnapshot.getId();

                        if(main_work.getAlarm1()==null){
                            alarm = null;
                        } else {
                            alarm = String.valueOf(main_work.getAlarm1());
                        }

                        String jobType = main_work.getTempalte_type();
                        switch (jobType){
                            case "fixed":
                                Intent intent = new Intent( CalendarMainActivity.this, FixedJobReview.class);
                                intent.putExtra("uid", uid);
                                intent.putExtra("name", name);
                                intent.putExtra("price", priceFix );
                                intent.putExtra("date", date );
                                intent.putExtra("description", description );
                                intent.putExtra("valuta", valuta );
                                intent.putExtra("status", status );
                                intent.putExtra("alarm", alarm );
                                intent.putExtra("documentName", documentName );

                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                break;

                            case "for smena":

                                Intent intent_forSmena = new Intent( CalendarMainActivity.this, ForSmenaJobReview.class);

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

                                startActivity(intent_forSmena);
                                overridePendingTransition(0, 0);
                                break;
                            case "for hour":

                                Intent intent_forHour = new Intent( CalendarMainActivity.this, ForHourJobReview.class);

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

                                startActivity(intent_forHour);
                                overridePendingTransition(0, 0);
                                break;
                        }
                    }
                } );

            }
        };
        run.run();

    }

    private void add_to_undelete_data(Integer position) {

        Runnable run = new Runnable() {
            public void run() {
        ds = adapter.getSnapshots().getSnapshot( position );
            }};
        new Thread(run).start();
    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if(type ==0)
            controller = AnimationUtils.loadLayoutAnimation( context, R.anim.layout_slide_from_rigth );
            recyclerView.setAdapter( adapter );
            recyclerView.setLayoutAnimation( controller );
            recyclerView.getAdapter().notifyDataSetChanged();

    }

    private void showUndoSnackbar(int position) {
        add_to_undelete_data(position);
        Runnable run = new Runnable() {
            public void run() {
        //CoordinatorLayout view = findViewById(R.id.coordinate_layout_fab);
        DrawerLayout view = findViewById(R.id.Drawer_layo);
        String undo = getString(R.string.UNDO);
        String itemdel = getString(R.string.Itemdeleted);
        Snackbar snackbar = Snackbar.make(view, itemdel, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setPadding( 0, 0, 0, 80);
        layout.setBackgroundColor( getResources().getColor( R.color.black_effective_100 ) );
        snackbar.setAction(undo, v ->undo());
        snackbar.show();

                //adapter.notifyItemRemoved( position );
            }};
        new Thread(run).run();

    }

    private void undo() {
        Runnable run = new Runnable() {
            public void run() {
        MainWork main_work = ds.toObject( MainWork.class);
        noteRef_addWork_Full.document().set( main_work );


        //setCalendar();
            }};
        new Thread(run).start();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        cw.updateCalendar();


    }


    @Override
    protected void onStart() {
        super.onStart();
        dataToDrawer();
        //checkTemplate();
        adapter.startListening();
        //recyclerView.setAdapter(adapter);
    }

    private void checkTemplate() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                noteRef_full.get(Source.CACHE).addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         //System.out.println(queryDocumentSnapshots.size());
                         if(queryDocumentSnapshots.size()!=0){
                             showTemplateInFloatActionButtonMenu = true;

                         } else {
                             showTemplateInFloatActionButtonMenu = false;
                         }

                     }

                 }
            );
            }
        };
        runnable.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataToDrawer();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
        }

        switch(requestCode) {

            case Pick_image:
                if (resultCode == RESULT_OK) {
                    try {
                        //Получаем URI изображения, преобразуем его в Bitmap
                        //объект и отображаем в элементе ImageView нашего интерфейса:
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream( imageUri );
                        final Bitmap selectedImage = BitmapFactory.decodeStream( imageStream );
                        avatar.setImageBitmap( selectedImage );


                        UserInfoToFirestore userInfoToFirestore = new UserInfoToFirestore();
                        userInfoToFirestore.setImg( imageUri.toString() );

                        noteRef_data.set( userInfoToFirestore )
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText( CalendarMainActivity.this, getString( R.string.avatar_add ) , Toast.LENGTH_SHORT).show();


//                                    result.add( rabotaName );
//                                    myAdapter.notifyDataSetChanged();

                                    }
                                } )

                                .addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText( CalendarMainActivity.this, getString( R.string.somsing_wrong ) , Toast.LENGTH_SHORT).show();

                                    }
                                } );


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }}

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) { }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied( this,perms )) {
            new AppSettingsDialog.Builder( this  ).build().show();
        }


    }

    private void setNubmerPicker(NumberPicker nubmerPicker, String [] numbers ){
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case RequestCameraPermissionId:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ActivityCompat.checkSelfPermission( getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                    try {
                        //System.out.println( "______!!!!!!________" );
                    }
                    catch (Exception e)
                    {
                        Toast.makeText( this, e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                }
        }
    }



}


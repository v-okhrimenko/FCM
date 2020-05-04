package com.example.fcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.models.TemplateJob;
import com.example.fcm.recycleviewadapter.TemplateAdapter;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.UserInfoToFirestore;
import com.example.fcm.models.MainWork;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class AddTemplateActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPrice;
    //    private EditText editTextNum;
    private Button add, add_template_infl, cancel;
    int position;


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private float sum_PayAll;
    private float sum_noPay;
    RecyclerView recyclerView_new;
    private DocumentSnapshot ds;


    {
        user = auth.getCurrentUser();
    }

    private Context context;


    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    //private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document( "My DB" ).collection( "MyWorks" );
    private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document( "My DB" ).collection( "Jobs" );
    private CollectionReference noteRef_full = db_fstore.collection( user.getUid() ).document( "My DB" ).collection( "Jobs_full" );


    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");


    //private CollectionReference noteRef_full = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs_full");


    private TemplateAdapter adapter_new;
    private ArrayList arrayList_name;

    private String avatarname;
    private CircleImageView avatar_image;
    private CircleImageView avatar;
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document( "Avatar" );



    FloatingActionButton fab_fixed, fab_hour, fab_shift,fab_template, fab_show_menu;
    private Boolean isOpen=false;
    TextView tv_fixed_txt, tv_hour_txt, tv_shift_txt, tv_template;
    ConstraintLayout cl_main;
    ConstraintLayout cl_disable_rv;
    TextView incompleatEvents;
    TextView noPayEvents;
    String emailname;


    private void setLocale(String lang) {
        Locale locale = new Locale( lang );
        Locale.setDefault( locale );
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration( config, getBaseContext().getResources().getDisplayMetrics() );

        SharedPreferences.Editor editor = getSharedPreferences( "Settings", MODE_PRIVATE ).edit();
        editor.putString( "My_Lang", lang );
        editor.apply();

    }


    public void loadLockale() {
        SharedPreferences prefs = getSharedPreferences( "Settings", Activity.MODE_PRIVATE );
        String language = prefs.getString( "My_Lang", "" );
        setLocale( language );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        loadLockale();
        setContentView( R.layout.activity_add_work_template );

        NavigationView navigationView = (NavigationView) findViewById( R.id.navigationView );
        View headerView = navigationView.getHeaderView( 0 );
        avatar = (CircleImageView) headerView.findViewById( R.id.profile_image );
        incompleatEvents = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.need_finish));

        noPayEvents= (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.not_pay));

        context = AddTemplateActivity.this;

        recyclerView_new = findViewById( R.id.recWieJobs_id_new );


        fab_fixed =findViewById( R.id.fab );
        fab_hour =findViewById( R.id.fab5 );
        fab_shift =findViewById( R.id.fab6 );
        //fab_template =findViewById( R.id.fab_template2 );

        fab_show_menu =findViewById( R.id.fab7 );
        tv_fixed_txt =findViewById(R.id.tv_fixed_test2);
        tv_hour_txt =findViewById(R.id.tv_hour_test2);
        tv_shift_txt =findViewById(R.id.tv_smena_test2);
        //tv_template =findViewById( R.id.tv_template_test2 );
        cl_disable_rv = findViewById( R.id.cl_black_test );
        cl_main = findViewById( R.id.constr_id );

        tv_fixed_txt.setClickable( true );
        tv_fixed_txt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                isOpen=false;

                open_fixed_add ();


//                startActivity(new Intent( context, AddFixedRate.class));
////                customType(CalendarMainActivity.this,"fadein-to-fadeout");
//                overridePendingTransition(0, 0);
//                finish();

            }
        } );

        tv_hour_txt.setClickable( true );
        tv_hour_txt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                isOpen=false;

                open_hour_add ();

            }
        } );

        tv_shift_txt.setClickable( true );
        tv_shift_txt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                isOpen=false;

                open_shift_add ();

            }
        } );

        cl_disable_rv.setAlpha( 0 );
        cl_disable_rv.setClickable( false );
        cl_disable_rv.setFocusable( false );

        fab_shift.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                isOpen=false;

                open_shift_add ();

            }
        } );

        fab_hour.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                isOpen=false;

                open_hour_add ();

            }
        } );
        fab_fixed.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                isOpen=false;

                open_fixed_add ();
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


        //cancel = findViewById( R.id.btn_exit_to_mainPage );


        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN );

        dataToDrawer();
        //setUpRecyclerView();

        makeRV();


        arrayList_name = new ArrayList<String>();

        checkName();



        drawerLayout = findViewById( R.id.Drawer_layo );
        navigationView = findViewById( R.id.navigationView );


        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.statistika:
                        item.setChecked( true );
                        startActivity( new Intent( AddTemplateActivity.this, StatisticActivity.class ) );
                        //customType(AddTemplateActivity.this,"fadein-to-fadeout");
                        overridePendingTransition( 0, 0 );
                        finish();
                        return true;

                    case R.id.need_finish:
                        item.setChecked( true );
                        startActivity( new Intent( AddTemplateActivity.this, OldNoFinishActivity.class ) );
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition( 0, 0 );
                        finish();
                        return true;

                    case R.id.nastroiki:
                        item.setChecked( true );
                        startActivity( new Intent( AddTemplateActivity.this, SettingsActivity.class ) );
                        //customType(AddTemplateActivity.this,"fadein-to-fadeout");
                        overridePendingTransition( 0, 0 );
                        finish();
                        return true;

                    case R.id.not_pay:
                        item.setChecked( true );
                        startActivity( new Intent( AddTemplateActivity.this, NoPayActivity.class ) );
                        //customType(AddTemplateActivity.this,"fadein-to-fadeout");
                        overridePendingTransition( 0, 0 );
                        finish();
                        return true;

                    case R.id.calendar_work:

                        item.setChecked( true );
                        startActivity( new Intent( AddTemplateActivity.this, CalendarMainActivity.class ) );
                        //customType(AddTemplateActivity.this,"fadein-to-fadeout");
                        overridePendingTransition( 0, 0 );
                        finish();
//                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.new_work:

                        item.setChecked( true );
                        startActivity( new Intent( AddTemplateActivity.this, AddJobActivity_new.class ) );
                        //customType(AddTemplateActivity.this,"fadein-to-fadeout");

                        overridePendingTransition( 0, 0 );
                        finish();

//                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.add_job:
                        item.setChecked( true );
                        drawerLayout.closeDrawers();
                        return true;


                    case R.id.exit_to_login:
                        item.setChecked( true );
                        FirebaseAuth.getInstance().signOut();
                        startActivity( new Intent( AddTemplateActivity.this, MainActivity.class ) );
                        //customType(AddTemplateActivity.this,"fadein-to-fadeout");

                        overridePendingTransition( 0, 0 );
                        finish();

                        return true;

                }

                return false;
            }
        } );
    }

    private void open_shift_add() {

        AlertDialog.Builder builder3 = new AlertDialog.Builder( AddTemplateActivity.this );
        LayoutInflater inflater3 = LayoutInflater.from( AddTemplateActivity.this );
        final View regiserWindow3 = inflater3.inflate( R.layout.activity_shift_template, null );
        builder3.setView( regiserWindow3 );

        final Button ok_shift = regiserWindow3.findViewById( R.id.btn_ok );
        final Button cancel_shift = regiserWindow3.findViewById( R.id.btn_cancel );
        final Button btn_infoRounded = regiserWindow3.findViewById( R.id.btn_info );
        final Button btn_infoOverTime = regiserWindow3.findViewById( R.id.btn_info_overtime );
        final EditText et_templateName_shift = regiserWindow3.findViewById( R.id.tv_name_jrFixed );
        final EditText et_templatePrice_shift = regiserWindow3.findViewById( R.id.et_price_for_shift );
        final EditText et_duration_shift = regiserWindow3.findViewById( R.id.et_dlitelnost_smeny );
        final EditText et_overTimeHours_shift = regiserWindow3.findViewById( R.id.et_procent_overtime );
        final EditText et_half_shift = regiserWindow3.findViewById( R.id.et_half_shift );
        final EditText et_rounded_shift = regiserWindow3.findViewById( R.id.et_okruglenie_minut );
        final Switch sw_half_shift = regiserWindow3.findViewById( R.id.switch_half_shift );
        final Spinner sp_templateValuta_shift = regiserWindow3.findViewById( R.id.spinner_valuta );

        et_half_shift.setEnabled( false );
        sw_half_shift.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_half_shift.setEnabled( true );
                } else {
                    et_half_shift.setEnabled( false );
                }
            }
        } );
        et_templateName_shift.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_templateName_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        et_templatePrice_shift.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_templatePrice_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        final AlertDialog dialog_shift = builder3.create();
        dialog_shift.setCancelable( false );
        dialog_shift.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog_shift.show();

        cancel_shift.setOnClickListener( v -> dialog_shift.dismiss() );
        //btn_infoRounded.setOnClickListener( v -> showInfo() );
        btn_infoRounded.setOnClickListener( v -> Helper.showInfoRounded( AddTemplateActivity.this ) );
        btn_infoOverTime.setOnClickListener( v -> Helper.showInfoOvertime( AddTemplateActivity.this ) );
        ok_shift.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemplateJob tmp = new TemplateJob();

                if (et_templateName_shift.getText().toString().isEmpty()) {
                    et_templateName_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                } else {

                    if (et_templatePrice_shift.getText().toString().isEmpty()) {
                        et_templatePrice_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    } else {

                            if (et_duration_shift.getText().toString().trim().isEmpty()) {
                                tmp.setSmena_duration( 0 );
                                et_duration_shift.setText( "0" );
                            } else {
                                tmp.setSmena_duration( Integer.parseInt( et_duration_shift.getText().toString().trim() ) );
                            }

                            if (et_overTimeHours_shift.getText().toString().trim().isEmpty()) {
                                tmp.setOvertime_pocent( 0 );
                            } else {
                                tmp.setOvertime_pocent( Integer.parseInt( et_overTimeHours_shift.getText().toString().trim() ) );
                            }

                            if (sw_half_shift.isChecked()) {
                                if (et_half_shift.getText().toString().isEmpty()) {
                                    tmp.setHalf_shift( false );
                                } else {
                                    if(Integer.parseInt( et_half_shift.getText().toString().trim() )>Integer.parseInt( et_duration_shift.getText().toString().trim() )){
                                        //Helper.errorHalfShiftMoreDurati0n( AddTemplateActivity.this );
                                        checkName();
                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
                                        LayoutInflater inflater = LayoutInflater.from( context );
                                        final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                                        builder.setView( regiserWindow );
                                        final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                                        shapka_txt.setText( getResources().getString( R.string.Duration_of_half_shift_is_incorrect )  );
                                        final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                                        txt_txt.setText(  getResources().getString( R.string.Duration_half_shift_txt ) );
                                        final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                        final androidx.appcompat.app.AlertDialog dialog = builder.create();
                                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                        dialog.setCancelable( false );
                                        dialog.show();
                                        ok.setOnClickListener( new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();

                                            } });




                                        return;
                                    }
                                    else {
                                        tmp.setHalf_shift( true );
                                        tmp.setHalf_shift_hours( Integer.parseInt( et_half_shift.getText().toString().trim() ) );
                                    } }
                            }
                            else {
                                    tmp.setHalf_shift( false );
                                }

                            if (et_rounded_shift.getText().toString().trim().isEmpty()) {
                                tmp.setRounded_minutes( 15 );

                            } else {
                                if (Integer.parseInt( et_rounded_shift.getText().toString().trim() ) > 59) {
                                    tmp.setRounded_minutes( 15 );
                                } else {
                                    tmp.setRounded_minutes( Integer.parseInt( et_rounded_shift.getText().toString().trim() ) );
                                }
                            }

                        if (arrayList_name.contains( et_templateName_shift.getText().toString().trim().toUpperCase() )) {

                            //Helper.template_already_exits( AddTemplateActivity.this, et_templateName_shift.getText().toString().trim().toUpperCase() );


                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            final View regiserWindow = inflater.inflate(R.layout.template_is_present_info_inflater, null);
                            builder.setView(regiserWindow);

                            final Button update = regiserWindow.findViewById(R.id.btn_update);
                            final Button change_name = regiserWindow.findViewById(R.id.btn_change_name_1 );
                            final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka_name );
                            shapka.setText( et_templateName_shift.getText().toString() );

                            final AlertDialog dialog2 = builder.create();
                            dialog2.setCancelable( false );
                            dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                            dialog2.show();

                            update.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    tmp.setTemplate_name( et_templateName_shift.getText().toString() );
                                    tmp.setTempalte_type( "for smena" );
                                    tmp.setPrice_smena( Integer.valueOf( et_templatePrice_shift.getText().toString() ) );
                                    tmp.setValuta( sp_templateValuta_shift.getSelectedItem().toString() );

                                    noteRef_full.document( et_templateName_shift.getText().toString().trim().toUpperCase() ).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            noteRef_full.document( et_templateName_shift.getText().toString().trim().toUpperCase() ).set( tmp );
                                            //System.out.println( name + " СТАРОЕ ИМЯ - ПО НЕМУ УДАЛЯЕМ" );
                                            dialog2.dismiss();
                                            dialog_shift.dismiss();
                                            adapter_new.notifyDataSetChanged();
                                            checkName();
                                        }
                                    } );
                                }
                            });

                            change_name.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog2.dismiss();
                                    et_templateName_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                }
                            } );


                        } else {

                            tmp.setTempalte_type( "for smena" );
                            tmp.setTemplate_name( et_templateName_shift.getText().toString().trim().toUpperCase() );
                            tmp.setPrice_smena( Integer.parseInt( et_templatePrice_shift.getText().toString().trim() ) );
                            tmp.setValuta( sp_templateValuta_shift.getSelectedItem().toString() );
                            noteRef_full.document( et_templateName_shift.getText().toString().trim().toUpperCase() ).set( tmp );
                            //System.out.println( name + " СТАРОЕ ИМЯ - ПО НЕМУ УДАЛЯЕМ" );
                            dialog_shift.dismiss();
                            adapter_new.notifyDataSetChanged();
                            checkName();


                                    }}}}});}

    private void open_hour_add() {

        AlertDialog.Builder builder2 = new AlertDialog.Builder( AddTemplateActivity.this );
        LayoutInflater inflater2 = LayoutInflater.from( AddTemplateActivity.this );
        final View regiserWindow2 = inflater2.inflate( R.layout.activity_hours_template, null );
        builder2.setView( regiserWindow2 );

        final Button ok_hour = regiserWindow2.findViewById( R.id.btn_ok );
        final Button cancel_hour = regiserWindow2.findViewById( R.id.btn_cancel );
        final Button info_hour = regiserWindow2.findViewById( R.id.btn_info );
        final EditText et_templateName_hour = regiserWindow2.findViewById( R.id.tv_name_jrFixed );
        final EditText et_templatePrice_hour = regiserWindow2.findViewById( R.id.et_price_for_hour );
        final EditText et_TemplateOkruglenie_minut_hour = regiserWindow2.findViewById( R.id.et_okruglenie_minut );
        final Spinner sp_templateValuta_hour = regiserWindow2.findViewById( R.id.spinner_valuta );

        et_templateName_hour.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_templateName_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        et_templatePrice_hour.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_templatePrice_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        final AlertDialog dialog_hour = builder2.create();
        dialog_hour.setCancelable( false );
        dialog_hour.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog_hour.show();

        cancel_hour.setOnClickListener( v -> dialog_hour.dismiss() );
        info_hour.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Helper.showInfoRounded( AddTemplateActivity.this );

            }
        } );
        ok_hour.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemplateJob tmp = new TemplateJob();
                if (et_templateName_hour.getText().toString().isEmpty()) {
                    et_templateName_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                } else {

                    if (et_templatePrice_hour.getText().toString().isEmpty()) {
                        et_templatePrice_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    } else {
                        if (arrayList_name.contains( et_templateName_hour.getText().toString().trim().toUpperCase() )) {
                            //System.out.println( "ИМЯ ЕСТЬ и ИЗМЕНЕНО" );
                            //Helper.template_already_exits( AddTemplateActivity.this, et_templateName_hour.getText().toString().trim().toUpperCase() );

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            final View regiserWindow = inflater.inflate(R.layout.template_is_present_info_inflater, null);
                            builder.setView(regiserWindow);

                            final Button update = regiserWindow.findViewById(R.id.btn_update);
                            final Button change_name = regiserWindow.findViewById(R.id.btn_change_name_1 );
                            final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka_name );
                            shapka.setText( et_templateName_hour.getText().toString() );

                            final AlertDialog dialog3 = builder.create();
                            dialog3.setCancelable( false );
                            dialog3.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                            dialog3.show();

                            update.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    tmp.setTemplate_name( et_templateName_hour.getText().toString() );
                                    tmp.setTempalte_type( "for hour" );
                                    tmp.setPrice_hour( Integer.valueOf(et_templatePrice_hour.getText().toString()) );
                                    tmp.setValuta( sp_templateValuta_hour.getSelectedItem().toString() );
                                    if(et_TemplateOkruglenie_minut_hour.getText().toString().isEmpty() || Integer.valueOf(et_TemplateOkruglenie_minut_hour.getText().toString().trim())>59){
                                        tmp.setRounded_minutes( 15 );
                                    } else {
                                        tmp.setRounded_minutes( Integer.valueOf( et_TemplateOkruglenie_minut_hour.getText().toString().trim() ) );
                                    }
                                    noteRef_full.document(et_templateName_hour.getText().toString().trim().toUpperCase()).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            noteRef_full.document( et_templateName_hour.getText().toString().trim().toUpperCase() ).set( tmp );
                                            dialog3.dismiss();
                                            dialog_hour.dismiss();
                                            adapter_new.notifyDataSetChanged();
                                            checkName();

                                        }
                                    } );
                                }
                            } );
                            change_name.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    et_templateName_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                    dialog3.dismiss();

                                }
                            } );



                        } else {
                            //System.out.println( "ПЕРЕИМЕНОВЫВЕМ" );
                            if (et_TemplateOkruglenie_minut_hour.getText().toString().trim().isEmpty()) {
                                tmp.setRounded_minutes( 15 );
                            } else {
                                if (Integer.parseInt( et_TemplateOkruglenie_minut_hour.getText().toString().trim() ) > 59) {
                                    tmp.setRounded_minutes( 15 );
                                } else {
                                    tmp.setRounded_minutes( Integer.parseInt( et_TemplateOkruglenie_minut_hour.getText().toString().trim() ) );
                                }
                            }
                            tmp.setTempalte_type( "for hour" );
                            tmp.setTemplate_name( et_templateName_hour.getText().toString().trim().toUpperCase() );
                            tmp.setPrice_hour( Integer.parseInt( et_templatePrice_hour.getText().toString().trim() ) );
                            tmp.setValuta( sp_templateValuta_hour.getSelectedItem().toString() );
                            noteRef_full.document( et_templateName_hour.getText().toString().trim().toUpperCase() ).set( tmp );
                            dialog_hour.dismiss();
                            adapter_new.notifyDataSetChanged();
                            checkName();
                        }

                    }}}
        } );

    }
    private void open_fixed_add() {

        AlertDialog.Builder builder = new AlertDialog.Builder( AddTemplateActivity.this );
        LayoutInflater inflater = LayoutInflater.from( AddTemplateActivity.this );
        final View regiserWindow = inflater.inflate( R.layout.activity_fixed_template, null );
        builder.setView( regiserWindow );

        final Button ok = regiserWindow.findViewById( R.id.button_ok_edit );
        final Button cancel = regiserWindow.findViewById( R.id.btn_cancel );
        final EditText et_templateName = regiserWindow.findViewById( R.id.tv_name_jrFixed );
        final EditText et_templatePrice = regiserWindow.findViewById( R.id.et_price_jrFixed );
        final Spinner sp_templateValuta = regiserWindow.findViewById( R.id.spinner_valuta );


        et_templateName.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_templateName.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        et_templatePrice.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_templatePrice.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        final AlertDialog dialog2 = builder.create();
        dialog2.setCancelable( false );
        dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog2.show();

        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_templateName.getText().toString().isEmpty()) {
                    et_templateName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                } else {

                    if (et_templatePrice.getText().toString().isEmpty()) {
                        et_templatePrice.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    } else {
                        if (arrayList_name.contains( et_templateName.getText().toString().trim().toUpperCase() )) {

                            //System.out.println( "ИМЯ ЕСТЬ и ИЗМЕНЕНО" );
                            //Helper.template_already_exits( AddTemplateActivity.this, et_templateName.getText().toString().trim().toUpperCase() );
                            TemplateJob tmp = new TemplateJob();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            final View regiserWindow = inflater.inflate(R.layout.template_is_present_info_inflater, null);
                            builder.setView(regiserWindow);

                            final Button update = regiserWindow.findViewById(R.id.btn_update);
                            final Button change_name = regiserWindow.findViewById(R.id.btn_change_name_1 );
                            final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka_name );
                            shapka.setText( et_templateName.getText().toString() );

                            final AlertDialog dialog3 = builder.create();
                            dialog3.setCancelable( false );
                            dialog3.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                            dialog3.show();

                            update.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    tmp.setTemplate_name( et_templateName.getText().toString() );
                                    tmp.setTempalte_type( "fixed" );
                                    tmp.setPrice_fixed( Integer.valueOf(et_templatePrice.getText().toString()) );
                                    tmp.setValuta( sp_templateValuta.getSelectedItem().toString() );

                                    noteRef_full.document(et_templateName.getText().toString().trim().toUpperCase()).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            //documentSnapshot.getReference().set( tmp );
                                            //dialog2.dismiss();
                                            noteRef_full.document( et_templateName.getText().toString().trim().toUpperCase() ).set( tmp );
                                            dialog3.dismiss();
                                            dialog2.dismiss();
                                            checkName();
                                        }
                                    } );
                                }
                            } );
                            change_name.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog3.dismiss();
                                    et_templateName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );

                                }
                            } );
                        } else {

                            TemplateJob tmp = new TemplateJob();
                            tmp.setTempalte_type( "fixed" );
                            tmp.setTemplate_name( et_templateName.getText().toString().trim().toUpperCase() );
                            tmp.setPrice_fixed( Integer.parseInt( et_templatePrice.getText().toString().trim() ) );
                            tmp.setValuta( sp_templateValuta.getSelectedItem().toString() );
                            noteRef_full.document( et_templateName.getText().toString().trim().toUpperCase() ).set( tmp );
                            dialog2.dismiss();
                            //hideKeyboard( AddTemplateActivity.this );
                            adapter_new.notifyDataSetChanged();
                            checkName();


                        }


                    }}
            }
        } );
        cancel.setOnClickListener( v -> dialog2.dismiss() );

    }

    private void checkName() {
        hideKeyboard();
        arrayList_name.clear();
        noteRef_full.get()
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //System.out.println( e.getMessage() );
                    }
                } )
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            TemplateJob tp = q.toObject( TemplateJob.class );
                            //System.out.println( tp.getTemplate_name() );
                            arrayList_name.add( tp.getTemplate_name().toUpperCase() );
                        }
                        //System.out.println( arrayList_name );

                    }
                } );
    }
    private void makeRV() {

        Query query = noteRef_full.orderBy( "template_name", Query.Direction.DESCENDING );
        ;
        FirestoreRecyclerOptions<TemplateJob> options = new FirestoreRecyclerOptions.Builder<TemplateJob>()
                .setQuery( query, TemplateJob.class )
                .build();
        adapter_new = new TemplateAdapter( options );


        recyclerView_new.setHasFixedSize( true );
        recyclerView_new.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView_new.setAdapter( adapter_new );

        new ItemTouchHelper( new ItemTouchHelper.SimpleCallback( 0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
//
                add_to_undelete_data( position );
                adapter_new.deleteItem( position );
                showUndoSnackbar_new();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder( c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive )
                        .addSwipeLeftBackgroundColor( ContextCompat.getColor( AddTemplateActivity.this, R.color.red ) )
                        .addSwipeLeftActionIcon( R.drawable.del_icon )
                        .create()
                        .decorate();
                super.onChildDraw( c, recyclerView, viewHolder, dX / 5, dY, actionState, isCurrentlyActive );
            }
        } ).attachToRecyclerView( recyclerView_new );

        adapter_new.setOnItemClickListener( new TemplateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                TemplateJob tmp = documentSnapshot.toObject( TemplateJob.class );
                

                String priceFix = String.valueOf( tmp.getPrice_fixed() );
                String priceHour = String.valueOf( tmp.getPrice_hour() );
                String priceSm = String.valueOf( tmp.getPrice_smena() );

                String durationSm = String.valueOf( tmp.getSmena_duration() );
                String overTimeProcent = String.valueOf( tmp.getOvertime_pocent() );
                String name = tmp.getTemplate_name().toUpperCase();

                String valuta = tmp.getValuta();

                String rounded_nimutes = String.valueOf( tmp.getRounded_minutes() );
                String half_shiht = String.valueOf( tmp.getHalf_shift() );
                String half_shiht_hours = String.valueOf( tmp.getHalf_shift_hours() );

                String documentName = documentSnapshot.getId();

                String jobType = tmp.getTempalte_type();

                switch (jobType) {
                    case "fixed":
                        AlertDialog.Builder builder = new AlertDialog.Builder( AddTemplateActivity.this );
                        LayoutInflater inflater = LayoutInflater.from( AddTemplateActivity.this );
                        final View regiserWindow = inflater.inflate( R.layout.activity_fixed_template, null );
                        builder.setView( regiserWindow );

                        final Button ok = regiserWindow.findViewById( R.id.button_ok_edit );
                        final Button cancel = regiserWindow.findViewById( R.id.btn_cancel );
                        final EditText et_templateName = regiserWindow.findViewById( R.id.tv_name_jrFixed );
                        final EditText et_templatePrice = regiserWindow.findViewById( R.id.et_price_jrFixed );
                        final Spinner sp_templateValuta = regiserWindow.findViewById( R.id.spinner_valuta );

                        et_templateName.setText( name );
                        et_templatePrice.setText( priceFix );
                        sp_templateValuta.setSelection( Helper.getValutaIndex( valuta ) );

                        et_templateName.addTextChangedListener( new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                et_templateName.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        } );
                        et_templatePrice.addTextChangedListener( new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                et_templatePrice.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        } );

                        final AlertDialog dialog2 = builder.create();
                        dialog2.setCancelable( false );
                        dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                        dialog2.show();

                        ok.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (et_templateName.getText().toString().isEmpty()) {
                                    et_templateName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                } else {

                                    if (et_templatePrice.getText().toString().isEmpty()) {
                                        et_templatePrice.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                    } else {

                                        if (!name.toUpperCase().equals( et_templateName.getText().toString().trim().toUpperCase() )) {
                                            if (arrayList_name.contains( et_templateName.getText().toString().trim().toUpperCase() )) {
                                                checkName();
                                                //System.out.println( "ИМЯ ЕСТЬ и ИЗМЕНЕНО" );
                                                //Helper.template_already_exits( AddTemplateActivity.this, et_templateName.getText().toString().trim().toUpperCase() );
                                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
                                                LayoutInflater inflater = LayoutInflater.from( context);
                                                final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                                                builder.setView( regiserWindow );
                                                final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                                                shapka_txt.setText(et_templateName.getText().toString().trim().toUpperCase() );
                                                final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                                                txt_txt.setText(  getResources().getString( R.string.template_name_already_exits_txt ) );
                                                final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                                                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                                dialog.setCancelable( false );
                                                dialog.show();
                                                ok.setOnClickListener( new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();

                                                    } });

                                            } else {
                                                //System.out.println( "ПЕРЕИМЕНОВЫВЕМ" );
                                                tmp.setTemplate_name( et_templateName.getText().toString().trim().toUpperCase() );
                                                tmp.setPrice_fixed( Integer.parseInt( et_templatePrice.getText().toString().trim() ) );
                                                tmp.setValuta( sp_templateValuta.getSelectedItem().toString() );
                                                noteRef_full.document( et_templateName.getText().toString().trim().toUpperCase() ).set( tmp );
                                                //System.out.println( name + " СТАРОЕ ИМЯ - ПО НЕМУ УДАЛЯЕМ" );
                                                noteRef_full.document( name ).delete();
                                                dialog2.dismiss();
                                                adapter_new.notifyDataSetChanged();
                                                checkName();

                                            }
                                        } else {
                                            //System.out.println( "ВСЕ ОК! ОБНОВЛЯЕМ" );
                                            noteRef_full.document( documentName ).update( "price_fixed", Integer.parseInt( et_templatePrice.getText().toString().trim() ) );
                                            noteRef_full.document( documentName ).update( "valuta", sp_templateValuta.getSelectedItem().toString() );
                                            dialog2.dismiss();
                                            adapter_new.notifyDataSetChanged();
                                            checkName();
                                        }
                                    }
                                }

                            }
                        } );
                        cancel.setOnClickListener( v -> dialog2.dismiss() );
//
                        break;

                    case "for smena":
                        AlertDialog.Builder builder3 = new AlertDialog.Builder( AddTemplateActivity.this );
                        LayoutInflater inflater3 = LayoutInflater.from( AddTemplateActivity.this );
                        final View regiserWindow3 = inflater3.inflate( R.layout.activity_shift_template, null );
                        builder3.setView( regiserWindow3 );

                        final Button ok_shift = regiserWindow3.findViewById( R.id.btn_ok );
                        final Button cancel_shift = regiserWindow3.findViewById( R.id.btn_cancel );
                        final Button btn_infoRounded = regiserWindow3.findViewById( R.id.btn_info );
                        final Button btn_infoOverTime = regiserWindow3.findViewById( R.id.btn_info_overtime );
                        final EditText et_templateName_shift = regiserWindow3.findViewById( R.id.tv_name_jrFixed );
                        final EditText et_templatePrice_shift = regiserWindow3.findViewById( R.id.et_price_for_shift );
                        final EditText et_duration_shift = regiserWindow3.findViewById( R.id.et_dlitelnost_smeny );
                        final EditText et_overTimeHours_shift = regiserWindow3.findViewById( R.id.et_procent_overtime );
                        final EditText et_half_shift = regiserWindow3.findViewById( R.id.et_half_shift );
                        final EditText et_rounded_shift = regiserWindow3.findViewById( R.id.et_okruglenie_minut );
                        final Switch sw_half_shift = regiserWindow3.findViewById( R.id.switch_half_shift );
                        final Spinner sp_templateValuta_shift = regiserWindow3.findViewById( R.id.spinner_valuta );

                        et_templateName_shift.setText( name );
                        et_templatePrice_shift.setText( priceSm );
                        et_rounded_shift.setText( rounded_nimutes );
                        sp_templateValuta_shift.setSelection( Helper.getValutaIndex( valuta ) );
                        et_duration_shift.setText( durationSm );
                        et_overTimeHours_shift.setText( overTimeProcent );

                        if (half_shiht.equals( "true" )) {
                            et_half_shift.setEnabled( true );
                            sw_half_shift.setChecked( true );
                            et_half_shift.setText( half_shiht_hours );
                        } else {
                            et_half_shift.setEnabled( false );
                            sw_half_shift.setChecked( false );
                        }

                        sw_half_shift.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    et_half_shift.setEnabled( true );
                                } else {
                                    et_half_shift.setEnabled( false );
                                }
                            }
                        } );
                        et_templateName_shift.addTextChangedListener( new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                et_templateName_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        } );
                        et_templatePrice_shift.addTextChangedListener( new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                et_templatePrice_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        } );

                        final AlertDialog dialog_shift = builder3.create();
                        dialog_shift.setCancelable( false );
                        dialog_shift.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                        dialog_shift.show();

                        cancel_shift.setOnClickListener( v -> dialog_shift.dismiss() );
                        //btn_infoRounded.setOnClickListener( v -> showInfo() );
                        btn_infoRounded.setOnClickListener( v -> Helper.showInfoRounded( AddTemplateActivity.this ) );
                        btn_infoOverTime.setOnClickListener( v -> Helper.showInfoOvertime( AddTemplateActivity.this ) );
                        ok_shift.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (et_templateName_shift.getText().toString().isEmpty()) {
                                    et_templateName_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                } else {

                                    if (et_templatePrice_shift.getText().toString().isEmpty()) {
                                        et_templatePrice_shift.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                    } else {

                                        if (!name.toUpperCase().equals( et_templateName_shift.getText().toString().trim().toUpperCase() )) {
                                            if (arrayList_name.contains( et_templateName_shift.getText().toString().trim().toUpperCase() )) {
                                                checkName();
                                                //System.out.println( "ИМЯ ЕСТЬ и ИЗМЕНЕНО" );
                                                //Helper.template_already_exits( AddTemplateActivity.this, et_templateName_shift.getText().toString().trim().toUpperCase() );
                                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
                                                LayoutInflater inflater = LayoutInflater.from( context);
                                                final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                                                builder.setView( regiserWindow );
                                                final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                                                shapka_txt.setText(et_templateName_shift.getText().toString().trim().toUpperCase() );
                                                final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                                                txt_txt.setText(  getResources().getString( R.string.template_name_already_exits_txt ) );
                                                final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                                                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                                dialog.setCancelable( false );
                                                dialog.show();
                                                ok.setOnClickListener( new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();


                                                    } });
                                            } else {
                                                //System.out.println( "ПЕРЕИМЕНОВЫВЕМ" );

                                                if (et_duration_shift.getText().toString().trim().isEmpty()) {
                                                    tmp.setSmena_duration( 0 );
                                                    et_duration_shift.setText( "0" );
                                                } else {
                                                    tmp.setSmena_duration( Integer.parseInt( et_duration_shift.getText().toString().trim() ) );
                                                }

                                                if (et_overTimeHours_shift.getText().toString().trim().isEmpty()) {
                                                    tmp.setOvertime_pocent( 0 );
                                                } else {
                                                    tmp.setOvertime_pocent( Integer.parseInt( et_overTimeHours_shift.getText().toString().trim() ) );
                                                }

                                                if (sw_half_shift.isChecked()) {
                                                    if (et_half_shift.getText().toString().isEmpty()) {
                                                        tmp.setHalf_shift( false );

                                                        if (et_rounded_shift.getText().toString().trim().isEmpty()) {
                                                            tmp.setRounded_minutes( 15 );

                                                        } else {
                                                            if (Integer.parseInt( et_rounded_shift.getText().toString().trim() ) > 59) {
                                                                tmp.setRounded_minutes( 15 );
                                                            } else {
                                                                tmp.setRounded_minutes( Integer.parseInt( et_rounded_shift.getText().toString().trim() ) );
                                                            }
                                                        }
                                                        tmp.setTemplate_name( et_templateName_shift.getText().toString().trim().toUpperCase() );
                                                        tmp.setPrice_smena( Integer.parseInt( et_templatePrice_shift.getText().toString().trim() ) );
                                                        tmp.setValuta( sp_templateValuta_shift.getSelectedItem().toString() );
                                                        noteRef_full.document( et_templateName_shift.getText().toString().trim().toUpperCase() ).set( tmp );
                                                        //System.out.println( name + " СТАРОЕ ИМЯ - ПО НЕМУ УДАЛЯЕМ" );
                                                        noteRef_full.document( name ).delete();
                                                        dialog_shift.dismiss();
                                                        adapter_new.notifyDataSetChanged();
                                                        checkName();

                                                    } else {
                                                        if (Integer.parseInt( et_half_shift.getText().toString().trim() ) > Integer.parseInt( et_duration_shift.getText().toString().trim() )) {
                                                            checkName();
                                                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
                                                            LayoutInflater inflater = LayoutInflater.from( context );
                                                            final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                                                            builder.setView( regiserWindow );
                                                            final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                                                            shapka_txt.setText( getResources().getString( R.string.Duration_of_half_shift_is_incorrect )  );
                                                            final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                                                            txt_txt.setText(  getResources().getString( R.string.Duration_half_shift_txt ) );
                                                            final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                                            final androidx.appcompat.app.AlertDialog dialog = builder.create();
                                                            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                                            dialog.setCancelable( false );
                                                            dialog.show();
                                                            ok.setOnClickListener( new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialog.dismiss();

                                                                } });
                                                        } else {
                                                            tmp.setHalf_shift( true );
                                                            tmp.setHalf_shift_hours( Integer.parseInt( et_half_shift.getText().toString().trim() ) );
                                                            if (et_rounded_shift.getText().toString().trim().isEmpty()) {
                                                                tmp.setRounded_minutes( 15 );

                                                            } else {
                                                                if (Integer.parseInt( et_rounded_shift.getText().toString().trim() ) > 59) {
                                                                    tmp.setRounded_minutes( 15 );
                                                                } else {
                                                                    tmp.setRounded_minutes( Integer.parseInt( et_rounded_shift.getText().toString().trim() ) );
                                                                }
                                                            }
                                                            tmp.setTemplate_name( et_templateName_shift.getText().toString().trim().toUpperCase() );
                                                            tmp.setPrice_smena( Integer.parseInt( et_templatePrice_shift.getText().toString().trim() ) );
                                                            tmp.setValuta( sp_templateValuta_shift.getSelectedItem().toString() );
                                                            noteRef_full.document( et_templateName_shift.getText().toString().trim().toUpperCase() ).set( tmp );
                                                            //System.out.println( name + " СТАРОЕ ИМЯ - ПО НЕМУ УДАЛЯЕМ" );
                                                            noteRef_full.document( name ).delete();
                                                            dialog_shift.dismiss();
                                                            adapter_new.notifyDataSetChanged();
                                                            checkName();

                                                        }

                                                    }
                                                } else {
                                                    tmp.setHalf_shift( false );
                                                    if (et_rounded_shift.getText().toString().trim().isEmpty()) {
                                                        tmp.setRounded_minutes( 15 );

                                                    } else {
                                                        if (Integer.parseInt( et_rounded_shift.getText().toString().trim() ) > 59) {
                                                            tmp.setRounded_minutes( 15 );
                                                        } else {
                                                            tmp.setRounded_minutes( Integer.parseInt( et_rounded_shift.getText().toString().trim() ) );
                                                        }
                                                    }
                                                    tmp.setTemplate_name( et_templateName_shift.getText().toString().trim().toUpperCase() );
                                                    tmp.setPrice_smena( Integer.parseInt( et_templatePrice_shift.getText().toString().trim() ) );
                                                    tmp.setValuta( sp_templateValuta_shift.getSelectedItem().toString() );
                                                    noteRef_full.document( et_templateName_shift.getText().toString().trim().toUpperCase() ).set( tmp );
                                                    //System.out.println( name + " СТАРОЕ ИМЯ - ПО НЕМУ УДАЛЯЕМ" );
                                                    noteRef_full.document( name ).delete();
                                                    dialog_shift.dismiss();
                                                    adapter_new.notifyDataSetChanged();
                                                    checkName();

                                                }


                                            }
                                        } else {
                                            //System.out.println( "ВСЕ ОК! ОБНОВЛЯЕМ" );

                                            if (et_duration_shift.getText().toString().trim().isEmpty()) {
                                                noteRef_full.document( documentName ).update( "smena_duration", 0 );
                                                et_duration_shift.setText( "0" );

                                            } else {
                                                noteRef_full.document( documentName ).update( "smena_duration", Integer.parseInt( et_duration_shift.getText().toString().trim() ) );

                                            }

                                            if (et_overTimeHours_shift.getText().toString().trim().isEmpty()) {
                                                noteRef_full.document( documentName ).update( "overtime_pocent", 0 );

                                            } else {
                                                noteRef_full.document( documentName ).update( "overtime_pocent", Integer.parseInt( et_overTimeHours_shift.getText().toString().trim() ) );
                                            }

                                            if (sw_half_shift.isChecked()) {
                                                if (et_half_shift.getText().toString().isEmpty()) {
                                                    noteRef_full.document( documentName ).update( "half_shift", false );
                                                    sw_half_shift.setChecked( false );
                                                    noteRef_full.document( documentName ).update( "price_smena", Integer.parseInt( et_templatePrice_shift.getText().toString().trim() ) );
                                                    noteRef_full.document( documentName ).update( "valuta", sp_templateValuta_shift.getSelectedItem().toString() );
                                                    if (et_rounded_shift.getText().toString().trim().isEmpty() || Integer.parseInt( et_rounded_shift.getText().toString().trim() ) > 59) {
                                                        noteRef_full.document( documentName ).update( "rounded_minutes", 15 );
                                                    } else {
                                                        noteRef_full.document( documentName ).update( "rounded_minutes", Integer.parseInt( et_rounded_shift.getText().toString().trim() ) );
                                                    }
                                                    dialog_shift.dismiss();
                                                    adapter_new.notifyDataSetChanged();
                                                    checkName();

                                                } else {
                                                    if (Integer.parseInt( et_half_shift.getText().toString().trim() ) > Integer.parseInt( et_duration_shift.getText().toString().trim() )) {
                                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
                                                        LayoutInflater inflater = LayoutInflater.from( context );
                                                        final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                                                        builder.setView( regiserWindow );
                                                        final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                                                        shapka_txt.setText( getResources().getString( R.string.Duration_of_half_shift_is_incorrect )  );
                                                        final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                                                        txt_txt.setText(  getResources().getString( R.string.Duration_half_shift_txt ) );
                                                        final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                                        final androidx.appcompat.app.AlertDialog dialog = builder.create();
                                                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                                        dialog.setCancelable( false );
                                                        dialog.show();
                                                        ok.setOnClickListener( new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog.dismiss();

                                                            } });

                                                    } else {
                                                        noteRef_full.document( documentName ).update( "half_shift", true );
                                                        noteRef_full.document( documentName ).update( "half_shift_hours", Integer.parseInt( et_half_shift.getText().toString().trim() ) );
                                                        noteRef_full.document( documentName ).update( "price_smena", Integer.parseInt( et_templatePrice_shift.getText().toString().trim() ) );
                                                        noteRef_full.document( documentName ).update( "valuta", sp_templateValuta_shift.getSelectedItem().toString() );
                                                        if (et_rounded_shift.getText().toString().trim().isEmpty() || Integer.parseInt( et_rounded_shift.getText().toString().trim() ) > 59) {
                                                            noteRef_full.document( documentName ).update( "rounded_minutes", 15 );
                                                        } else {
                                                            noteRef_full.document( documentName ).update( "rounded_minutes", Integer.parseInt( et_rounded_shift.getText().toString().trim() ) );
                                                        }
                                                        dialog_shift.dismiss();
                                                        adapter_new.notifyDataSetChanged();
                                                        checkName();
                                                    }

                                                }
                                            } else {
                                                noteRef_full.document( documentName ).update( "half_shift", false );
                                                noteRef_full.document( documentName ).update( "price_smena", Integer.parseInt( et_templatePrice_shift.getText().toString().trim() ) );
                                                noteRef_full.document( documentName ).update( "valuta", sp_templateValuta_shift.getSelectedItem().toString() );
                                                if (et_rounded_shift.getText().toString().trim().isEmpty() || Integer.parseInt( et_rounded_shift.getText().toString().trim() ) > 59) {
                                                    noteRef_full.document( documentName ).update( "rounded_minutes", 15 );
                                                } else {
                                                    noteRef_full.document( documentName ).update( "rounded_minutes", Integer.parseInt( et_rounded_shift.getText().toString().trim() ) );
                                                }
                                                dialog_shift.dismiss();
                                                adapter_new.notifyDataSetChanged();
                                                checkName();
                                            }
                                        }
                                    }

                                }
                            }
                        } );


                        break;
                    case "for hour":

                        AlertDialog.Builder builder2 = new AlertDialog.Builder( AddTemplateActivity.this );
                        LayoutInflater inflater2 = LayoutInflater.from( AddTemplateActivity.this );
                        final View regiserWindow2 = inflater2.inflate( R.layout.activity_hours_template, null );
                        builder2.setView( regiserWindow2 );

                        final Button ok_hour = regiserWindow2.findViewById( R.id.btn_ok );
                        final Button cancel_hour = regiserWindow2.findViewById( R.id.btn_cancel );
                        final Button info_hour = regiserWindow2.findViewById( R.id.btn_info );
                        final EditText et_templateName_hour = regiserWindow2.findViewById( R.id.tv_name_jrFixed );
                        final EditText et_templatePrice_hour = regiserWindow2.findViewById( R.id.et_price_for_hour );
                        final EditText et_TemplateOkruglenie_minut_hour = regiserWindow2.findViewById( R.id.et_okruglenie_minut );
                        final Spinner sp_templateValuta_hour = regiserWindow2.findViewById( R.id.spinner_valuta );

                        et_templateName_hour.setText( name );
                        et_templatePrice_hour.setText( priceHour );
                        et_TemplateOkruglenie_minut_hour.setText( rounded_nimutes );
                        sp_templateValuta_hour.setSelection( Helper.getValutaIndex( valuta ) );

                        et_templateName_hour.addTextChangedListener( new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                et_templateName_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        } );
                        et_templatePrice_hour.addTextChangedListener( new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                et_templatePrice_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        } );

                        final AlertDialog dialog_hour = builder2.create();
                        dialog_hour.setCancelable( false );
                        dialog_hour.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                        dialog_hour.show();

                        cancel_hour.setOnClickListener( v -> dialog_hour.dismiss() );
                        info_hour.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Helper.showInfoRounded( AddTemplateActivity.this );

                            }
                        } );
                        ok_hour.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (et_templateName_hour.getText().toString().isEmpty()) {
                                    et_templateName_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                } else {

                                    if (et_templatePrice_hour.getText().toString().isEmpty()) {
                                        et_templatePrice_hour.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                                    } else {

                                        if (!name.toUpperCase().equals( et_templateName_hour.getText().toString().trim().toUpperCase() )) {
                                            if (arrayList_name.contains( et_templateName_hour.getText().toString().trim().toUpperCase() )) {

                                                //System.out.println( "ИМЯ ЕСТЬ и ИЗМЕНЕНО" );
                                                //Helper.template_already_exits( AddTemplateActivity.this, et_templateName_hour.getText().toString().trim().toUpperCase() );
                                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
                                                LayoutInflater inflater = LayoutInflater.from( context);
                                                final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                                                builder.setView( regiserWindow );
                                                final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                                                shapka_txt.setText(et_templateName_hour.getText().toString().trim().toUpperCase() );
                                                final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                                                txt_txt.setText(  getResources().getString( R.string.template_name_already_exits_txt ) );
                                                final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                                                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                                                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                                dialog.setCancelable( false );
                                                dialog.show();
                                                ok.setOnClickListener( new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
//                                                        startActivity(new Intent( context, CalendarMainActivity.class));
//                                                        overridePendingTransition(0, 0);
//                                                        finish();
//                                                        dialog.dismiss();

                                                    } });

                                            } else {
                                                //System.out.println( "ПЕРЕИМЕНОВЫВЕМ" );
                                                if (et_TemplateOkruglenie_minut_hour.getText().toString().trim().isEmpty()) {
                                                    tmp.setRounded_minutes( 15 );
                                                } else {
                                                    if (Integer.parseInt( et_TemplateOkruglenie_minut_hour.getText().toString().trim() ) > 59) {
                                                        tmp.setRounded_minutes( 15 );
                                                    } else {
                                                        tmp.setRounded_minutes( Integer.parseInt( et_TemplateOkruglenie_minut_hour.getText().toString().trim() ) );
                                                    }
                                                }
                                                tmp.setTemplate_name( et_templateName_hour.getText().toString().trim().toUpperCase() );
                                                tmp.setPrice_hour( Integer.parseInt( et_templatePrice_hour.getText().toString().trim() ) );
                                                tmp.setValuta( sp_templateValuta_hour.getSelectedItem().toString() );
                                                noteRef_full.document( et_templateName_hour.getText().toString().trim().toUpperCase() ).set( tmp );
                                                //System.out.println( name + " СТАРОЕ ИМЯ - ПО НЕМУ УДАЛЯЕМ" );
                                                noteRef_full.document( name ).delete();
                                                dialog_hour.dismiss();
                                                adapter_new.notifyDataSetChanged();
                                                checkName();

                                            }

                                        } else {
                                            //System.out.println( "ВСЕ ОК! ОБНОВЛЯЕМ" );

                                            noteRef_full.document( documentName ).update( "price_hour", Integer.parseInt( et_templatePrice_hour.getText().toString().trim() ) );
                                            noteRef_full.document( documentName ).update( "valuta", sp_templateValuta_hour.getSelectedItem().toString() );
                                            if (et_TemplateOkruglenie_minut_hour.getText().toString().trim().isEmpty() || Integer.parseInt( et_TemplateOkruglenie_minut_hour.getText().toString().trim() ) > 59) {
                                                noteRef_full.document( documentName ).update( "rounded_minutes", 15 );
                                            } else {
                                                noteRef_full.document( documentName ).update( "rounded_minutes", Integer.parseInt( et_TemplateOkruglenie_minut_hour.getText().toString().trim() ) );
                                            }
                                            dialog_hour.dismiss();
                                            adapter_new.notifyDataSetChanged();
                                            checkName();

                                        }


                                    }
                                }
                            }
                        } );

                        break;
                }
            }
        } );
    }
    private void add_to_undelete_data(int position) {
        ds = adapter_new.getSnapshots().getSnapshot( position );
    }


    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
        adapter_new.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
        adapter_new.stopListening();
    }

    private void showUndoSnackbar_new() {

        View view = findViewById( R.id.constr_id );
        String undo = getString( R.string.UNDO );
        String itemdel = getString( R.string.Itemdeleted );
        Snackbar snackbar = Snackbar.make( view, itemdel, Snackbar.LENGTH_LONG );
        snackbar.setAction( undo, v -> undo( position ) );
        snackbar.show();
    }

    private void undo(int position) {
        TemplateJob tmp = ds.toObject( TemplateJob.class);
        String jname = ds.getString( "template_name" );
        noteRef_full.document(jname).set( tmp );
        adapter_new.notifyDataSetChanged();
        recyclerView_new.setAdapter(adapter_new);

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

        sum_PayAll = 0f;
        sum_noPay = 0f;

        Date date_ = new Date(System.currentTimeMillis());
        String d1_ = Helper.dataToString( date_ );
        Date date_ok_ = Helper.stringToData( d1_ );

        noteRef_addWork_Full.whereLessThan("date", date_ok_ ).whereEqualTo( "needFinish", true )
                .whereEqualTo( "end",null ).orderBy( "date", Query.Direction.ASCENDING ).get()
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

        noteRef_data.get()
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
                                                       Picasso.with( AddTemplateActivity.this ).load( myUri ).into( avatar );

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

        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING ).get()
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

    private void closeMenu() {
        isOpen=false;
        tv_fixed_txt.setAlpha( 0 );
        tv_hour_txt.setAlpha( 0 );
        tv_shift_txt.setAlpha( 0 );
        //tv_template.setAlpha( 0 );


        cl_disable_rv.setAlpha( 0 );
        cl_disable_rv.setClickable( false );
        cl_disable_rv.setFocusable( false );



        fab_show_menu.animate().rotation( 0 );
        fab_fixed.animate().translationY( 0 ).rotation( 0 );
        fab_hour.animate().translationY( 0 ).rotation( 0 );
        fab_shift.animate().translationY( 0 ).rotation( 0 );
        //fab_template.animate().translationY( 0 ).rotation( 0 ).setDuration( 300 );
        //fab_template.animate().translationX( 0 ).rotation( 0 ).setDuration( 300 );

        tv_fixed_txt.animate().translationY( 0 ).rotation( 0 );
        tv_hour_txt.animate().translationY( 0 ).rotation( 0 );
        tv_shift_txt.animate().translationY( 0 ).rotation( 0 );
        //tv_template.animate().translationY( 0 ).rotation( 0 );
    }
    private void openMenu() {
        isOpen=true;
        //tv_template.animate().translationY( -565 ).setDuration(100 );

        tv_fixed_txt.animate().translationY( -430 ).setDuration(300 );
        tv_fixed_txt.setAlpha( 1 );
        tv_hour_txt.animate().translationY( -295 ).setDuration(200 );
        tv_hour_txt.setAlpha( 1 );
        tv_shift_txt.animate().translationY( -160 ).setDuration(100 );
        tv_shift_txt.setAlpha( 1 );
        //tv_template.animate().translationX( -160 ).setDuration(300 );
        //tv_template.setAlpha( 1 );


        cl_disable_rv.setAlpha( 1 );
        cl_disable_rv.setClickable( true );
        cl_disable_rv.setFocusable( true );

        fab_show_menu.animate().rotation( 180 );

        fab_fixed.animate().translationY( -430 ).rotation( 360 ).setDuration(300 );
        fab_hour.animate().translationY( -295 ).rotation( 360 ).setDuration(200 );
        fab_shift.animate().translationY( -160 ).rotation( 360 ).setDuration(100 );
        //fab_template.animate().translationY( -565 ).rotation( 360 ).setDuration(100 );
        //fab_template.animate().translationX( -160 ).rotation( 360 ).setDuration(300 );

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void hideKeyboard() {

        View view = this.getCurrentFocus();

        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity( new Intent( AddTemplateActivity.this, CalendarMainActivity.class ) );
            overridePendingTransition( 0, 0 );
//            customType(AddTemplateActivity.this,"fadein-to-fadeout");
            finish();
            return true;
        }
        return super.onKeyDown( keyCode, event );
    }
}

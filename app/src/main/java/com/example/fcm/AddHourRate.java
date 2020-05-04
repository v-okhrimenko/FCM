package com.example.fcm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.mycalendar.DatePicker;
import com.example.fcm.mycalendar.DatePickerEvents;
import com.example.fcm.recycleviewadapter.AddDateInAddJobActivityAdapter;
import com.example.fcm.recycleviewadapter.TemplateAdapter;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.models.TemplateJob;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AddHourRate extends AppCompatActivity implements AddDateInAddJobActivityAdapter.ItemClickListener {

    private EditText et_JobName, et_HourPrice, et_Descritpion, rounded_minutes;
    private Button btn_Ok, btn_Cancel, btn_info;
    private FloatingActionButton btn_CalendarSelect;
    private Spinner valuta;
    private Switch sw_paid, save_template;
    private RecyclerView recyclerViewCalendarSelected;

    private TemplateAdapter adapter_new;
    private AddDateInAddJobActivityAdapter adapter;
    private List<String> selectDates = new ArrayList<String>();
    private int x = 0;
    private ArrayList tamplate_name;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");
    private CollectionReference noteRef_full = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs_full");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_hour_rate );



        et_JobName = findViewById(R.id.tv_name_jrFixed);
        et_Descritpion = findViewById(R.id.et_description_jrFixed);
        sw_paid = findViewById( R.id.sw_paid2 );
        save_template = findViewById( R.id.sw_save_as_template );
        valuta  = findViewById( R.id.spinner_valuta );
        recyclerViewCalendarSelected = findViewById( R.id.rv_calendarDaysSelected );
        btn_Ok = findViewById(R.id.btn_ok);
        btn_Cancel = findViewById(R.id.btn_cancel);
        btn_info = findViewById(R.id.btn_info);
        //btn_Template = findViewById(R.id.buttontemplate2);
        btn_CalendarSelect = findViewById(R.id.fab_add_to_calendar);
        et_HourPrice = findViewById(R.id.et_price_for_hour);
        rounded_minutes = findViewById(R.id.et_okruglenie_minut);

        try {
            Intent intent = getIntent();
            String isFromTemplate = intent.getStringExtra("isFromTemplate");
            if(isFromTemplate.equals( "true" )){
                et_JobName.setText( intent.getStringExtra("name") );
                et_HourPrice.setText( intent.getStringExtra("priceHour") );
                valuta.setSelection( Helper.getValutaIndex( intent.getStringExtra("valuta") ) );
                rounded_minutes.setText( intent.getStringExtra( "rounded_nimutes" ) );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        tamplate_name = new ArrayList<String>();
        checkTemplateName();


        et_JobName.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_JobName.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        et_HourPrice.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_HourPrice.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        btn_CalendarSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (x ==0) {
                    x=1;

                    AlertDialog.Builder builder = new AlertDialog.Builder( AddHourRate.this);
                    LayoutInflater inflater = LayoutInflater.from( AddHourRate.this);
                    final View regiserWindow1 = inflater.inflate(R.layout.datepicker_inflater, null );
                    builder.setView(regiserWindow1);


                    final DatePicker cw = (DatePicker)regiserWindow1.findViewById( R.id.CustomCalendarView );
                    final Button ok = (Button) regiserWindow1.findViewById( R.id.btn_ok );
                    final Button cancel = (Button) regiserWindow1.findViewById( R.id.btn_cancel );
                    final AlertDialog dialog1 = builder.create();
                    dialog1.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

                    ///noteRef_addWork.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    noteRef_addWork_Full.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot ds: queryDocumentSnapshots){
                                MainWork main_work = ds.toObject( MainWork.class );
//                             //System.out.println( main_work.getDate() );

                                Calendar c = Calendar.getInstance();
                                c.setTime(main_work.getDate());

                                String dateEvent = c.get( Calendar.DAY_OF_MONTH )+"-"+(c.get( Calendar.MONTH )+1)+"-"+c.get( Calendar.YEAR );
//                             //System.out.println( dateEvent );

                                DatePicker.eventsList.add( new DatePickerEvents(main_work.getName(),dateEvent,main_work.getStatus()) );
                            }dialog1.show();


                        }

                    } );

                    cancel.setOnClickListener( v1 -> {
                        x = 0;
                        dialog1.dismiss();
                        DatePicker.test.clear();
                        DatePicker.eventsList.clear();
                    });

                    ok.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            x = 0;

                            dialog1.dismiss();

                            for(String x: DatePicker.test){
                                if(selectDates.contains( x )) {
                                    continue;
                                } else {selectDates.add( x );}

                                makeRV();
                            }
                            DatePicker.test.clear();
                            DatePicker.eventsList.clear();
                        }
                    } );

                } else {

                }
            }
        } );

        btn_info.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( AddHourRate.this);
                LayoutInflater inflater = LayoutInflater.from( AddHourRate.this);
                final View regiserWindow = inflater.inflate(R.layout.info_rounded, null);
                builder.setView(regiserWindow);

                final Button ok = regiserWindow.findViewById( R.id.btn_ok );

                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( false );
                dialog.show();
                ok.setOnClickListener( v1 -> dialog.dismiss() );

            }
        } );

        btn_Cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( AddHourRate.this, CalendarMainActivity.class));
//                customType(CalendarMainActivity.this,"fadein-to-fadeout");
                overridePendingTransition(0, 0);
                finish();
            }
        } );

        btn_Ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_JobName.getText().toString().isEmpty()) {
                    et_JobName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                } else {
                    if (selectDates.isEmpty()) {

                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( AddHourRate.this );
                        LayoutInflater inflater = LayoutInflater.from( AddHourRate.this );
                        final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                        builder.setView( regiserWindow );
                        final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                        shapka_txt.setText( getResources().getString( R.string.Add_a_date ) );
                        final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                        txt_txt.setText(  getResources().getString( R.string.No_Dates_sekected ) );
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
                    }
                    if (et_HourPrice.getText().toString().isEmpty()) {
                        et_HourPrice.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    } else {
                        //// ADD TO FIREBASE

                        if(save_template.isChecked()){
                            saveAsTemplate();
                        } else {
                            addJobToFireBase();
                        }

                    }
                }
            }
        } );

//        btn_Template.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(AddHourRate.this);
//                LayoutInflater inflater = LayoutInflater.from(AddHourRate.this);
//                final View regiserWindow = inflater.inflate(R.layout.template_inflter, null);
//                builder.setView(regiserWindow);
//
//                final RecyclerView recyclerView_new = regiserWindow.findViewById( R.id.recWieJobs_id_new );
//
//                final Button cancel = regiserWindow.findViewById( R.id.btn_close_tamplate_rv );
//
//                final AlertDialog dialog = builder.create();
//                dialog.setCancelable( false );
//                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//                dialog.show();
//                cancel.setOnClickListener( v1 -> {dialog.dismiss();} );
//
//                Query query = noteRef_full.whereEqualTo( "tempalte_type", "for hour" ).orderBy( "template_name", Query.Direction.DESCENDING );;
//                FirestoreRecyclerOptions<TemplateJob> options = new FirestoreRecyclerOptions.Builder<TemplateJob>()
//                        .setQuery(query, TemplateJob.class)
//                        .build();
//                adapter_new = new TemplateAdapter( options );
//                adapter_new.startListening();
//                adapter_new.setOnItemClickListener( new TemplateAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
//
//                        int valutaIndex = 0;
//                        switch (adapter_new.getItem( position ).getValuta()){
//                            case "грн":
//                                valutaIndex = 0;
//                                break;
//                            case "usd":
//                                valutaIndex = 1;
//                                break;
//                            case "eur":
//                                valutaIndex = 2;
//                                break;
//                            case "руб":
//                                valutaIndex = 3;
//                                break;
//                        }
//
//                        et_JobName.setText( adapter_new.getItem( position ).getTemplate_name() );
//                        et_HourPrice.setText( String.valueOf(adapter_new.getItem( position ).getPrice_hour()) );
//                        rounded_minutes.setText( String.valueOf(adapter_new.getItem( position ).getRounded_minutes()) );
//                        valuta.setSelection( valutaIndex );
//                        dialog.dismiss();
//                    }
//                } );
//
//                recyclerView_new.setHasFixedSize( true );
//                recyclerView_new.setLayoutManager( new LinearLayoutManager( getBaseContext() ) );
//                recyclerView_new.setAdapter( adapter_new );
//
//            }
//        } );
    }


    private void makeRV() {
        RecyclerView recyclerView = findViewById(R.id.rv_calendarDaysSelected);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new AddDateInAddJobActivityAdapter(this, selectDates);
        adapter.setClickListener(this);
        runAnimation(recyclerView, 0);
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

    private void addJobToFireBase() {

        boolean checked =  sw_paid.isChecked();
        String selectType = "for hour";
        Boolean needFinish = true;
        float totalEarned = 0.0f;


        Random rand = new Random();
        MainWork main_work = new MainWork();

        for (String i:selectDates) {
            int uniqId = rand.nextInt(100000000);
             //System.out.println( uniqId + " " + et_JobName.getText().toString() + " " + checked+ " "+selectType);

            main_work.setName( et_JobName.getText().toString().toUpperCase() );

            if (et_HourPrice.getText().toString().isEmpty()) {
                main_work.setPrice_fixed( 0 );
            } else {
                main_work.setPrice_hour( Integer.parseInt(et_HourPrice.getText().toString()) );
            }
            if(rounded_minutes.getText().toString().isEmpty() || Integer.valueOf(rounded_minutes.getText().toString().trim())>59 ){
                main_work.setRounded_minut( 15 );
            } else {
                main_work.setRounded_minut( Integer.valueOf( rounded_minutes.getText().toString().trim() ) );
            }

            main_work.setZarabotanoFinal( totalEarned );
            main_work.setNeedFinish( needFinish );
            main_work.setStatus( checked );
            main_work.setTempalte_type( selectType );
            main_work.setDiscription( et_Descritpion.getText().toString() );
            main_work.setDate( Helper.stringToData( i ) );
            main_work.setUniqId( uniqId );
            main_work.setValuta( valuta.getSelectedItem().toString() );

            /// TODO Start End Time
//            Date d = new Date(System.currentTimeMillis());
//             //System.out.println("CURRENT TIME d "+ d );
//            main_work.setStart( d );

            noteRef_addWork_Full.document().set( main_work )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast,
                                    (ViewGroup) findViewById(R.id.custom_toast_container));

                            LinearLayout ln = layout.findViewById( R.id.custom_toast_container );
                            ln.setBackground( getResources().getDrawable( R.drawable.event_bg ) );
                            ln.setBackgroundTintList( ContextCompat.getColorStateList( AddHourRate.this, R.color.green_lite) );

                            ImageView iv = layout.findViewById( R.id.iv );
                            iv.setBackground( getResources().getDrawable( R.drawable.check_ok_white ) );

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText(getString( R.string.saved_successfully));

                            Toast toast = new Toast(getApplicationContext());
//                                    toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                            startActivity(new Intent( AddHourRate.this, CalendarMainActivity.class));
                            overridePendingTransition(0, 0);
                            finish();
                        }

                    } )

                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate( R.layout.custom_toast,
                                    (ViewGroup) findViewById( R.id.custom_toast_container ) );

                            LinearLayout ln = layout.findViewById( R.id.custom_toast_container );
                            ln.setBackground( getResources().getDrawable( R.drawable.event_bg ) );
                            ln.setBackgroundTintList( ContextCompat.getColorStateList( AddHourRate.this, R.color.red ) );


                            ImageView iv = layout.findViewById( R.id.iv );
                            iv.setBackground( getResources().getDrawable( R.drawable.alert ) );

                            TextView text = (TextView) layout.findViewById( R.id.text );
                            text.setText( getString( R.string.somsing_wrong ) );

                            Toast toast = new Toast( getApplicationContext() );
//                                    toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration( Toast.LENGTH_LONG );
                            toast.setView( layout );
                            toast.show();


                        }});

            /// ADD TO FIREBASE HERE


        }
    }

    private void saveAsTemplate() {
        TemplateJob tmp = new TemplateJob();
        if(tamplate_name.contains( et_JobName.getText().toString().toUpperCase().trim() )){

            AlertDialog.Builder builder = new AlertDialog.Builder( AddHourRate.this);
            LayoutInflater inflater = LayoutInflater.from( AddHourRate.this);
            final View regiserWindow = inflater.inflate(R.layout.template_is_present_info_inflater, null);
            builder.setView(regiserWindow);

            final Button update = regiserWindow.findViewById(R.id.btn_update);
            final Button change_name = regiserWindow.findViewById(R.id.btn_change_name_1 );
            final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka_name );
            shapka.setText( et_JobName.getText().toString() );

            final AlertDialog dialog2 = builder.create();
            dialog2.setCancelable( false );
            dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            dialog2.show();

            update.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tmp.setTemplate_name( et_JobName.getText().toString() );
                    tmp.setTempalte_type( "for hour" );
                    tmp.setPrice_hour( Integer.valueOf(et_HourPrice.getText().toString()) );
                    tmp.setValuta( valuta.getSelectedItem().toString() );
                    if(rounded_minutes.getText().toString().isEmpty() || Integer.valueOf(rounded_minutes.getText().toString().trim())>59){
                        tmp.setRounded_minutes( 15 );
                    } else {
                        tmp.setRounded_minutes( Integer.valueOf( rounded_minutes.getText().toString().trim() ) );
                    }
                    noteRef_full.document(et_JobName.getText().toString().trim().toUpperCase()).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentSnapshot.getReference().set( tmp );

                            addJobToFireBase();
                            dialog2.dismiss();

                        }
                    } );
                }
            } );
            change_name.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_JobName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    dialog2.dismiss();

                }
            } );

        } else {
            tmp.setTemplate_name( et_JobName.getText().toString() );
            tmp.setTempalte_type( "for hour" );
            tmp.setPrice_hour( Integer.valueOf(et_HourPrice.getText().toString()) );
            tmp.setValuta( valuta.getSelectedItem().toString() );
            if(rounded_minutes.getText().toString().isEmpty() || Integer.valueOf(rounded_minutes.getText().toString().trim())>59){
                tmp.setRounded_minutes( 15 );
            } else {
                tmp.setRounded_minutes( Integer.valueOf( rounded_minutes.getText().toString().trim() ) );
            }
            noteRef_full.document(et_JobName.getText().toString().trim().toUpperCase()).set(tmp);

            addJobToFireBase();

        }


    }

    private void checkTemplateName() {
        tamplate_name.clear();
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

                        for (QueryDocumentSnapshot q: queryDocumentSnapshots){
                            TemplateJob tp = q.toObject( TemplateJob.class );
                            // //System.out.println( tp.getTemplate_name() );
                            tamplate_name.add( tp.getTemplate_name().toUpperCase() );
                        }
                    }
                } );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            startActivity(new Intent( AddHourRate.this, CalendarMainActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(View view, int position) {
        selectDates.remove( position );
        adapter.notifyItemRemoved( position );
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

}

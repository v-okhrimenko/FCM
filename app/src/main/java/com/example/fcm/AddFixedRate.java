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
import com.example.fcm.models.TempaleJob;
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

public class AddFixedRate extends AppCompatActivity implements AddDateInAddJobActivityAdapter.ItemClickListener {

    private EditText et_JobName, ed_FixedPrice, et_Descritpion;
    private Button btn_Ok, btn_Cancel;
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
        setContentView( R.layout.activity_add_fixed_rate );




        et_JobName = findViewById(R.id.tv_name_jrFixed);
        et_Descritpion = findViewById(R.id.et_description_jrFixed);
        sw_paid = findViewById( R.id.sw_paid2 );
        save_template = findViewById( R.id.sw_save_as_template );
        valuta  = findViewById( R.id.spinner_valuta );
        recyclerViewCalendarSelected = findViewById( R.id.rv_calendarDaysSelected );
        btn_Ok = findViewById(R.id.btn_ok);
        btn_Cancel = findViewById(R.id.btn_cancel);
        btn_CalendarSelect = findViewById(R.id.fab_add_to_calendar);
        ed_FixedPrice = findViewById(R.id.et_price_jrFixed);

        tamplate_name = new ArrayList<String>();
        checkTemplateName();

        try {
            Intent intent = getIntent();
            String isFromTemplate = intent.getStringExtra("isFromTemplate");
            if(isFromTemplate.equals( "true" )){
                et_JobName.setText( intent.getStringExtra("name") );
                ed_FixedPrice.setText( intent.getStringExtra("price") );
                valuta.setSelection( Helper.getValutaIndex( intent.getStringExtra("valuta") ) );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        ed_FixedPrice.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_FixedPrice.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
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

                    AlertDialog.Builder builder = new AlertDialog.Builder( AddFixedRate.this);
                    LayoutInflater inflater = LayoutInflater.from( AddFixedRate.this);
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

                                Calendar c = Calendar.getInstance();
                                c.setTime(main_work.getDate());

                                String dateEvent = c.get( Calendar.DAY_OF_MONTH )+"-"+(c.get( Calendar.MONTH )+1)+"-"+c.get( Calendar.YEAR );

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

        btn_Cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( AddFixedRate.this, CalendarMainActivity.class));
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
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( AddFixedRate.this );
                        LayoutInflater inflater = LayoutInflater.from( AddFixedRate.this );
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


//                        AlertDialog.Builder builder = new AlertDialog.Builder( AddFixedRate.this );
//                        LayoutInflater inflater = LayoutInflater.from( AddFixedRate.this );
//                        final View regiserWindow = inflater.inflate( R.layout.info_no_date_set_inflater, null );
//                        builder.setView( regiserWindow );
//
//                        final Button ok = regiserWindow.findViewById( R.id.btn_no_dateCalrndar_info_alert );
//
//                        final AlertDialog dialog = builder.create();
//                        dialog.setCancelable( false );
//                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//                        dialog.show();
//
//                        ok.setOnClickListener( new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
                    }
                    if (ed_FixedPrice.getText().toString().isEmpty()) {
                        ed_FixedPrice.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    } else {
                        if(save_template.isChecked()){
                            saveAsTemplate();

                        } else {

                            addJobToFireBase();
                        }
                        //// ADD TO FIREBASE

                    }
                }
            }
        } );

//        btn_Template.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(AddFixedRate.this);
//                LayoutInflater inflater = LayoutInflater.from(AddFixedRate.this);
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
//                Query query = noteRef_full.whereEqualTo( "tempalte_type", "fixed" ).orderBy( "template_name", Query.Direction.DESCENDING );;
//                FirestoreRecyclerOptions<TempaleJob> options = new FirestoreRecyclerOptions.Builder<TempaleJob>()
//                        .setQuery(query, TempaleJob.class)
//                        .build();
//                adapter_new = new TemplateAdapter( options );
//                adapter_new.startListening();
//                adapter_new.setOnItemClickListener( new TemplateAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
//                         //System.out.println(adapter_new.getItem( position ).getTemplate_name());
//                         //System.out.println(adapter_new.getItem( position ).getTempalte_type());
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
//                        ed_FixedPrice.setText( String.valueOf(adapter_new.getItem( position ).getPrice_fixed()) );
//                        valuta.setSelection( valutaIndex );
//                        dialog.dismiss();
//                    }
//
//                } );
//
//                recyclerView_new.setHasFixedSize( true );
//                recyclerView_new.setLayoutManager( new LinearLayoutManager( getBaseContext() ) );
//                recyclerView_new.setAdapter( adapter_new );
//
//            }
//        } );
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
                            TempaleJob tp = q.toObject( TempaleJob.class );
                            // //System.out.println( tp.getTemplate_name() );
                            tamplate_name.add( tp.getTemplate_name().toUpperCase() );
                        }
                    }
                } );
    }

    private void saveAsTemplate() {
        TempaleJob tmp = new TempaleJob();
        if(tamplate_name.contains( et_JobName.getText().toString().toUpperCase().trim() )){

            AlertDialog.Builder builder = new AlertDialog.Builder( AddFixedRate.this);
            LayoutInflater inflater = LayoutInflater.from( AddFixedRate.this);
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
                    tmp.setTempalte_type( "fixed" );
                    tmp.setPrice_fixed( Integer.valueOf(ed_FixedPrice.getText().toString()) );
                    tmp.setValuta( valuta.getSelectedItem().toString() );

                    noteRef_full.document(et_JobName.getText().toString().trim().toUpperCase()).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentSnapshot.getReference().set( tmp );
                            //dialog2.dismiss();
                            addJobToFireBase();
                            dialog2.dismiss();

                        }
                    } );
                }
            } );
            change_name.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                    et_JobName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                }
            } );

        } else {
            tmp.setTemplate_name( et_JobName.getText().toString() );
            tmp.setTempalte_type( "fixed" );
            tmp.setPrice_fixed( Integer.valueOf(ed_FixedPrice.getText().toString())  );
            tmp.setValuta( valuta.getSelectedItem().toString() );
            noteRef_full.document(et_JobName.getText().toString().trim().toUpperCase()).set(tmp);
            addJobToFireBase();


        }


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
        String selectType = "fixed";
        Boolean needFinish = false;
        float totalEarned = 0.0f;
        totalEarned = Float.valueOf(ed_FixedPrice.getText().toString().trim());


        Random rand = new Random();
        MainWork main_work = new MainWork();

        for (String i:selectDates) {
            int uniqId = rand.nextInt(100000000);
             //System.out.println( uniqId + " " + et_JobName.getText().toString() + " " + checked+ " "+selectType);

            main_work.setName( et_JobName.getText().toString().toUpperCase() );

            if (ed_FixedPrice.getText().toString().isEmpty()) {
                main_work.setPrice_fixed( 0 );
            } else {
                main_work.setPrice_fixed( Integer.parseInt(ed_FixedPrice.getText().toString()) );
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
                            ln.setBackgroundTintList( ContextCompat.getColorStateList( AddFixedRate.this, R.color.green_lite) );

                            ImageView iv = layout.findViewById( R.id.iv );
                            iv.setBackground( getResources().getDrawable( R.drawable.check_ok_white ) );

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText(getString( R.string.saved_successfully));

                            Toast toast = new Toast(getApplicationContext());
//                                    toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                            startActivity(new Intent( AddFixedRate.this, CalendarMainActivity.class));
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
                            ln.setBackgroundTintList( ContextCompat.getColorStateList( AddFixedRate.this, R.color.red ) );


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


        }}


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            startActivity(new Intent( AddFixedRate.this, CalendarMainActivity.class));
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

package com.example.fcm.fixedrate;

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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.CalendarMainActivity;
import com.example.fcm.R;
import com.example.fcm.helper.DbConnection;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.models.TemplateJob;
import com.example.fcm.mycalendar.DatePicker;
import com.example.fcm.mycalendar.DatePickerEvents;
import com.example.fcm.recycleviewadapter.AddDateInAddJobActivityAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddFixedRate extends AppCompatActivity implements AddDateInAddJobActivityAdapter.ItemClickListener {

    private Context context = AddFixedRate.this;

    private EditText etJobName, etFixedPrice, etDescription;
    private Spinner spValuta;
    private Switch sw_paid, save_template;
    private AddDateInAddJobActivityAdapter adapter;
    private List<String> selectedDaysInCalendarArray = new ArrayList<String>();
    private int x = 0;
    private ArrayList arrayListTemplateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_fixed_rate );

        etJobName = findViewById(R.id.tv_name_jrFixed);
        etDescription = findViewById(R.id.et_description_jrFixed);
        sw_paid = findViewById( R.id.sw_paid2 );
        save_template = findViewById( R.id.sw_save_as_template );
        spValuta = findViewById( R.id.spinner_valuta );
        Button btn_Ok = findViewById( R.id.btn_ok );
        Button btn_Cancel = findViewById( R.id.btn_cancel );
        FloatingActionButton btn_CalendarSelect = findViewById( R.id.fab_add_to_calendar );
        etFixedPrice = findViewById(R.id.et_price_jrFixed);

        arrayListTemplateName = new ArrayList<String>();

        checkTemplateName();

        try {
            Intent intent = getIntent();
            String isFromTemplate = intent.getStringExtra("isFromTemplate");
            if(isFromTemplate.equals( "true" )){
                etJobName.setText( intent.getStringExtra("name") );
                etFixedPrice.setText( intent.getStringExtra("price") );
                spValuta.setSelection( Helper.getValutaIndex( intent.getStringExtra("valuta") ) );
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }


        etJobName.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etJobName.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        etFixedPrice.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etFixedPrice.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        btn_CalendarSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (x==0) {
                    x=1;

                    AlertDialog.Builder builder = new AlertDialog.Builder( AddFixedRate.this);
                    LayoutInflater inflater = LayoutInflater.from( AddFixedRate.this);
                    final View regiserWindow1 = inflater.inflate(R.layout.datepicker_inflater, null );
                    builder.setView(regiserWindow1);

                    final DatePicker cw = (DatePicker)regiserWindow1.findViewById( R.id.CustomCalendarView );
                    final Button ok = (Button) regiserWindow1.findViewById( R.id.btn_ok );
                    final Button cancel = (Button) regiserWindow1.findViewById( R.id.btn_cancel );
                    final AlertDialog datePickerDialog = builder.create();
                    datePickerDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

                    ///noteRef_addWork.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    DbConnection.DBJOBS.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot ds: queryDocumentSnapshots){
                                MainWork main_work = ds.toObject( MainWork.class );

                                Calendar c = Calendar.getInstance();
                                c.setTime(main_work.getDate());
                                String dateEvent = c.get( Calendar.DAY_OF_MONTH )+"-"+(c.get( Calendar.MONTH )+1)+"-"+c.get( Calendar.YEAR );

                                DatePicker.eventsList.add( new DatePickerEvents(main_work.getName(),dateEvent,main_work.getStatus()) );
                            }datePickerDialog.show();
                        }

                    } );

                    cancel.setOnClickListener( v1 -> {
                        x = 0;
                        datePickerDialog.dismiss();
                        DatePicker.test.clear();
                        DatePicker.eventsList.clear();
                    });

                    ok.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            x = 0;

                            datePickerDialog.dismiss();

                            for(String selectDate: DatePicker.test){
                                if(selectedDaysInCalendarArray.contains( selectDate )) {
                                    continue;
                                } else {
                                    selectedDaysInCalendarArray.add( selectDate );}

                                makeRecyclerView();
                            }
                            DatePicker.test.clear();
                            DatePicker.eventsList.clear();
                        }
                    } );
                }
            }
        } );
        btn_Cancel.setOnClickListener( v -> {
            startActivity(new Intent( AddFixedRate.this, CalendarMainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        } );
        btn_Ok.setOnClickListener( v -> {

            ArrayList fixedFieldsArray = new ArrayList();
            FixedRateMetods fixedRateMetods = new FixedRateMetods();

            fixedFieldsArray.add( etJobName.getText().toString().trim() );
            fixedFieldsArray.add( etFixedPrice.getText().toString().trim() );
            fixedFieldsArray.add( selectedDaysInCalendarArray );

            String result = fixedRateMetods.fieldNotEmpty( fixedFieldsArray );

            switch (result){
                case "no name":
                    etJobName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    break;
                case "no price":
                    etFixedPrice.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    break;
                case "all ok":
                    if(save_template.isChecked()){

                        saveAsTemplate();

                    } else {

                        MainWork main_work = initNewJobs();
                        fixedRateMetods.saveJobs(context, main_work, selectedDaysInCalendarArray);

                    }
                    break;
                case "all empty":
                    etJobName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    etFixedPrice.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                    break;
                case "no date":
                    Helper.showInfoNoDateAdd( context );
                    break;
            }
       });
    }

    private void checkTemplateName() {
        arrayListTemplateName.clear();
        DbConnection.DBTEMPLATES.get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        TemplateJob templateJob = documentSnapshot.toObject( TemplateJob.class );
                        arrayListTemplateName.add( templateJob.getTemplate_name().toUpperCase() );
                    }
                } );
    }
    private void saveAsTemplate() {

        FixedRateMetods fixedRateMetods = new FixedRateMetods();

        if(fixedRateMetods.thisNameIsAlreadyUsedInTemplates( arrayListTemplateName, etJobName.getText().toString().trim().toUpperCase() )){
            AlertDialog.Builder builder = new AlertDialog.Builder( AddFixedRate.this);
            LayoutInflater inflater = LayoutInflater.from( AddFixedRate.this);
            final View regiserWindow = inflater.inflate(R.layout.template_is_present_info_inflater, null);
            builder.setView(regiserWindow);

            final Button update = regiserWindow.findViewById(R.id.btn_update);
            final Button change_name = regiserWindow.findViewById(R.id.btn_change_name_1 );
            final TextView shapka = regiserWindow.findViewById( R.id.tv_shapka_name );
            shapka.setText( etJobName.getText().toString() );

            final AlertDialog dialogNameIsPresentInTemplates = builder.create();
            dialogNameIsPresentInTemplates.setCancelable( false );
            dialogNameIsPresentInTemplates.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            dialogNameIsPresentInTemplates.show();

            update.setOnClickListener( v -> {

                TemplateJob updateTemplate = initNewTemplate();
                MainWork main_work = initNewJobs();
                fixedRateMetods.updatePresentTemplate( context, updateTemplate);
                fixedRateMetods.saveJobs( context, main_work, selectedDaysInCalendarArray );
                dialogNameIsPresentInTemplates.dismiss();

            } );

            change_name.setOnClickListener( v -> {
                dialogNameIsPresentInTemplates.dismiss();
                etJobName.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
            } );
        } else {
            TemplateJob templateJob = initNewTemplate();
            fixedRateMetods.saveAsTemplate( context, templateJob );

            MainWork main_work = initNewJobs();
            fixedRateMetods.saveJobs(context, main_work, selectedDaysInCalendarArray);
        }
    }

    private TemplateJob initNewTemplate() {
        TemplateJob templateJob = new TemplateJob();

        templateJob.setTemplate_name( etJobName.getText().toString().toUpperCase() );
        templateJob.setPrice_fixed(  Integer.parseInt( etFixedPrice.getText().toString()) );
        templateJob.setTempalte_type(  "fixed" );
        templateJob.setValuta( spValuta.getSelectedItem().toString() );

        return templateJob;
    }
    private MainWork initNewJobs() {
        MainWork main_work = new MainWork();

        main_work.setName( etJobName.getText().toString().toUpperCase() );
        main_work.setPrice_fixed( Integer.parseInt( etFixedPrice.getText().toString()) );
        main_work.setZarabotanoFinal( Float.valueOf( etFixedPrice.getText().toString().trim()) );
        main_work.setNeedFinish( false );
        main_work.setStatus( sw_paid.isChecked());
        main_work.setTempalte_type( "fixed" );
        main_work.setDiscription( etDescription.getText().toString() );
        main_work.setValuta( spValuta.getSelectedItem().toString() );

        return main_work;


    }

    private void makeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_calendarDaysSelected);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new AddDateInAddJobActivityAdapter(this, selectedDaysInCalendarArray );
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
        selectedDaysInCalendarArray.remove( position );
        adapter.notifyItemRemoved( position );
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}

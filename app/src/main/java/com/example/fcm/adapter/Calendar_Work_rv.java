package com.example.fcm.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.R;
import com.example.fcm.models.Main_work_new;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Calendar_Work_rv extends FirestoreRecyclerAdapter<Main_work_new, Calendar_Work_rv.CalendarHolder> {

    private onItemClickListener listener;
    String day, month, year, day_week;
    Boolean status;
    String name;
    String discription;
    Date date;


    private int price;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs");
    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");

    private Context context;
    public int count = 0;



    public Calendar_Work_rv(@NonNull FirestoreRecyclerOptions<Main_work_new> options) {
        super( options );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onBindViewHolder(@NonNull CalendarHolder holder, int position, @NonNull Main_work_new model) {


        holder.name.setText( model.getName());


        java.text.SimpleDateFormat d = new java.text.SimpleDateFormat("MM");
        java.text.SimpleDateFormat m = new java.text.SimpleDateFormat("dd");
        java.text.SimpleDateFormat y = new java.text.SimpleDateFormat("yyyy");

        day = (m.format(model.getDate().getTime()));

//        month = (d.format(model.getDate().getTime()));

        year = (y.format(model.getDate().getTime()));
        Locale locale = Resources.getSystem().getConfiguration().locale;
        SimpleDateFormat dateFormatOfStringInDB = new SimpleDateFormat("MMMM", locale);
        String month1 = dateFormatOfStringInDB.format(model.getDate());

//        month =(new SimpleDateFormat("MMMM", Locale.getDefault()).format(model.getDate()));


        day_week = (new SimpleDateFormat("EEE", locale).format(model.getDate()));


        holder.day.setText( day );
        holder.month.setText(month1 );
        holder.day_week.setText( day_week );

        Calendar today = Calendar.getInstance();
        today.set( Calendar.HOUR_OF_DAY, 0 );
        today.set( Calendar.MINUTE, 0 );
        today.set( Calendar.SECOND, 0 );
        today.set( Calendar.MILLISECOND, 0 );

        Date d_ = new Date(model.getDate().getTime());
        Calendar sobitie = Calendar.getInstance();
        sobitie.setTime(d_);

        if(today.getTime().equals( sobitie.getTime() )){
            System.out.println("YEAHH");
            holder.cl.setBackgroundTintList(context.getResources().getColorStateList(R.color.green_olive));

        } else {
            System.out.println("NO SORRY");
        }

        if(model.getStatus().equals( true )){
            holder.pay_ico.setVisibility(View.VISIBLE);
        }
        if(model.getStatus().equals( false )){
            holder.pay_ico.setVisibility(View.INVISIBLE);
        }



        Date alarm1 = model.getAlarm1();
        Date date_test = new Date();

        if(model.getAlarm1()==null){
            holder.alarm.setVisibility(View.INVISIBLE);
        }else {
            if (alarm1.before(date_test)){
                holder.alarm.setVisibility(View.INVISIBLE);
            } else {

            }
            holder.alarm.setVisibility(View.VISIBLE);

        }
    }

    @NonNull
    @Override
    public CalendarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.rv_future_work, parent, false );
        context = parent.getContext();
        return new CalendarHolder( v );

    }

    public String get_id(int position){
        String get_id;
        get_id = getSnapshots().getSnapshot( position ).getId();
        if (get_id.isEmpty()){
            return get_id;
        } else {
             return get_id;
        }


    }

    public void update(int position) {

//        getSnapshots().getSnapshot( position ).getReference().update("status", true);
        Main_work_new main_work = getSnapshots().getSnapshot( position ).toObject( Main_work_new.class );

        if(main_work.getStatus().equals( true )){
            getSnapshots().getSnapshot( position ).getReference().update("status", false);
        } else {getSnapshots().getSnapshot( position ).getReference().update("status", true);}
        this.notifyDataSetChanged();


    }
//
//
    public void delete(int position) {

        getSnapshots().getSnapshot( position ).getReference().delete();
        this.notifyItemRemoved( position );

    }


    class CalendarHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView day;
        TextView month;
        TextView day_week;
        ImageView pay_ico;
        ImageView alarm;
        ConstraintLayout cl;

        public CalendarHolder(@NonNull View itemView) {
            super( itemView );
            name = itemView.findViewById( R.id.project_name_text_view_id );
            day = itemView.findViewById( R.id.day_text_view_id );
            month = itemView.findViewById( R.id.day_month_text_view_id);
            day_week = itemView.findViewById( R.id.day_week_text_view_id );
            pay_ico = itemView.findViewById( R.id.pay_icon_visible_id );
            alarm = itemView.findViewById( R.id.alarmOk );
            cl = itemView.findViewById( R.id.cl_000 );


            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick( getSnapshots().getSnapshot( position ), position );
                    }
                }
            } );
        }
    }
    public interface onItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
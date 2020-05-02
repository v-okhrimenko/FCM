package com.example.fcm.recycleviewadapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.R;
import com.example.fcm.models.MainWork;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Locale;

public class StatisticaRv extends FirestoreRecyclerAdapter<MainWork, StatisticaRv.CalendarHolder> {

    private onItemClickListener listener;
    String day, month, year, day_week;
    Boolean status;
    String name;
    String discription;
    Date date;
    public String documentId;

    Drawable green;


    private int price;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private Context context;

    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs");
    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");



    public StatisticaRv(@NonNull FirestoreRecyclerOptions<MainWork> options) {
        super( options );

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onBindViewHolder(@NonNull CalendarHolder holder, int position, @NonNull MainWork model) {

        documentId = getSnapshots().getSnapshot(position).getId();

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


//        day_week = (new SimpleDateFormat("EEE", Locale.getDefault()).format(model.getDate()));


        holder.day.setText( day );
        holder.month.setText(month1 );
        holder.year.setText( year );
        holder.price.setText( Float.toString(model.getZarabotanoFinal()) );


        MainWork main_work = getSnapshots().getSnapshot( position ).toObject( MainWork.class );
        if(main_work.getStatus() == true) {
//            holder.bg.setBackgroundColor( Color.parseColor("#93C750") );
            holder.bg.setBackgroundTintList(context.getResources().getColorStateList(R.color.green_olive));




        }else {

            holder.bg.setBackgroundTintList(context.getResources().getColorStateList(R.color.red_50));

        }

    }

    @NonNull
    @Override
    public CalendarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.rv_nopay_work, parent, false );
        context = parent.getContext();
        green = ResourcesCompat.getDrawable(parent.getContext().getResources(),
                R.drawable.button_ok_blue_ripple, null);

        return new CalendarHolder( v );


    }

    public void update(int position) {

//        getSnapshots().getSnapshot( position ).getReference().update("status", true);
        MainWork main_work = getSnapshots().getSnapshot( position ).toObject( MainWork.class );

        System.out.println("______"+ main_work.getStatus());
        if(main_work.getStatus().equals( true )){
            getSnapshots().getSnapshot( position ).getReference().update("status", false);
        } else {getSnapshots().getSnapshot( position ).getReference().update("status", true);}
        this.notifyDataSetChanged();

    }


    public void delete(int position) {
        getSnapshots().getSnapshot( position ).getReference().delete();
        this.notifyItemRemoved( position );

    }


    class CalendarHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView day;
        TextView month;
        TextView year;
        TextView price;
        ConstraintLayout layout;
        ConstraintLayout bg;

        public CalendarHolder(@NonNull View itemView) {
            super( itemView );
            name = itemView.findViewById( R.id.no_name_text_view_id);

            day = itemView.findViewById( R.id.nday_text_view_id );
            year = itemView.findViewById( R.id.np_year_id );
            month = itemView.findViewById( R.id.nmonth_text_view_id);
            price = itemView.findViewById( R.id.price_noPay_id);
            layout = itemView.findViewById( R.id.bg_rv );
            bg = itemView.findViewById( R.id.constr_bg );

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

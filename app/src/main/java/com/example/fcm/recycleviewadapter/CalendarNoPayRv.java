package com.example.fcm.recycleviewadapter;

import android.content.Context;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class CalendarNoPayRv extends FirestoreRecyclerAdapter<MainWork, CalendarNoPayRv.CalendarHolder> {

    private onItemClickListener listener;
    String day, month, year, day_week;
    Boolean status;
    String name;
    String discription;
    Date date;
    public String documentId;

    private Float price;
    private Context context;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs");
    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");



    public CalendarNoPayRv(@NonNull FirestoreRecyclerOptions<MainWork> options) {
        super( options );

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onBindViewHolder(@NonNull CalendarHolder holder, int position, @NonNull MainWork model) {

        documentId = getSnapshots().getSnapshot(position).getId();

        holder.name.setText( model.getName());
        //holder.name.setTextColor( context.getResources().getColorStateList(R.color.red_50) );
        //holder.price.setTextColor( context.getResources().getColorStateList(R.color.red_50) );

        java.text.SimpleDateFormat d = new java.text.SimpleDateFormat("MM");
        java.text.SimpleDateFormat m = new java.text.SimpleDateFormat("dd");
        java.text.SimpleDateFormat y = new java.text.SimpleDateFormat("yyyy");

        day = (m.format(model.getDate().getTime()));
        year = (y.format(model.getDate().getTime()));

        Locale locale = Resources.getSystem().getConfiguration().locale;
        SimpleDateFormat dateFormatOfStringInDB = new SimpleDateFormat("MMMM", locale);
        String month1 = dateFormatOfStringInDB.format(model.getDate());

        holder.day.setText( day );
        holder.month.setText(month1 );
        holder.year.setText( year );
        try{
            if (model.getZarabotanoFinal().equals( "null" )){
                holder.price.setText( "0" );
            } else {
                holder.price.setText( String.valueOf(model.getZarabotanoFinal()) );
            }

            System.out.println(String.valueOf(model.getZarabotanoFinal()) + "  " + model.getZarabotanoFinal() );

        } catch (Exception e){
            holder.price.setText( "0" );
        }

    }

    @NonNull
    @Override
    public CalendarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.rv_nopay_work, parent, false );
        context = parent.getContext();
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
//        TextView day_week;
        TextView year;
        TextView price;
        ConstraintLayout layout;


//        ImageView pay_ico;



        public CalendarHolder(@NonNull View itemView) {
            super( itemView );
            name = itemView.findViewById( R.id.no_name_text_view_id);
            day = itemView.findViewById( R.id.nday_text_view_id );
            year = itemView.findViewById( R.id.np_year_id );
            month = itemView.findViewById( R.id.nmonth_text_view_id);
            price = itemView.findViewById( R.id.price_noPay_id);
            layout = itemView.findViewById( R.id.constr_bg );


//            day_week = itemView.findViewById( R.id.day_week_text_view_id );
//            pay_ico = itemView.findViewById( R.id.pay_icon_visible_id );

            layout.setOnClickListener( new View.OnClickListener() {
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
